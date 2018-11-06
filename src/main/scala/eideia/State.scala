package eideia

import eideia.InitApp.ZDT
import eideia.InitApp.{logger, mostRecentData}
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.beans.binding.{Bindings, ObjectBinding}
import eideia.models._
import eideia.atlas.AtlasQuery
import eideia.surface.CanvasSurface
import eideia.userdata.UserDataManager


class State(val defaultLocation: Location) {


    def localnow: ObjectProperty[ZDT] = new ObjectProperty[ZDT](this, "localnow", DateManager.now(InitApp.defaultTimeZone))

    def utcnow: ObjectBinding[ZDT] = Bindings.createObjectBinding(
        () => DateManager.utcFromLocal(localnow.value), localnow
    )

    val currentLocation = new ObjectProperty[Location](this, "location", AtlasQuery.getLocationFromUserData(setUserFromHereAndNow).get)
    val currentChart = new ObjectProperty[Chart](this, "currentChart", setNowChart)
    val currentDatabase = new ObjectProperty[String](this, "currentDatabase", InitApp.defaultDatabase)
    val currentUserData = new ObjectProperty[UserData](this, "currentUserData", setUserFromHereAndNow) {
        onChange(
            (_, _, nval) => {
                val date: ZDT = DateManager.parseDateString(nval.date)
                val location: Location = AtlasQuery.getLocationFromUserData(nval).getOrElse(InitApp.defaultLocation)
                val utc = DateManager.utcFromLocal(date)
                currentChart.value = Chart(utc, location.latitude, location.longitude)
                CanvasSurface.invoqueOp(currentOperation.value,currentChart.value)
                logger.info(s"Current User: $nval")
                if (!mostRecentData.contains(nval))
                    mostRecentData += nval
            }
        )
    }



    val infoLabels = InfoLabels(currentUserData(), localnow.value.toString)
    val currentRegister = new ObjectProperty[Register](this, "currentRegister", Register(currentDatabase.value, 0L)) {
        onChange { (_, _, nval) => {
            currentUserData.value = UserDataManager.loadRegisterById(nval.table, nval.rid).get
            infoLabels.update(currentUserData.value)
            }
        }
    }

    val currentOperation = new StringProperty(this,"currentOp" ) {
        onChange(
            (_,_,op) => {
                CanvasSurface.invoqueOp(op,currentChart.value)
            }
        )
    }

    def setNow = {
        currentUserData.value = setUserFromHereAndNow
        currentChart.value = setNowChart
        infoLabels.update(currentUserData.value)
    }

    def setNowChart: Chart = Chart(utcnow.value, defaultLocation.latitude, defaultLocation.longitude)

    def setUserFromHereAndNow: UserData = {
        UserData("Momento actual", "", "", localnow.value.toString, defaultLocation.name, defaultLocation.country, defaultLocation.admin1, defaultLocation.admin2, defaultLocation.id)
    }

    def setChartFromLoadData(table: String, id: Long): Unit = {
        currentRegister.value = Register(table, id)
    }
}

