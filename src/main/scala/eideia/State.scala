package eideia

import eideia.InitApp.ZDT
import scalafx.beans.property.ObjectProperty
import scalafx.beans.binding.Bindings
import eideia.models._
import eideia.atlas.AtlasQuery
import eideia.userdata.LocationTriplet


class State(val config: NexConf) {
    val defaultLocation: Location =
        AtlasQuery.getLocationFromLegacyTriplet(LocationTriplet(config.locality,config.region, config.country)).get

    val defaultTimeZone: String = defaultLocation.timezone

    def localnow: ZDT = DateManager.now(defaultTimeZone)
    def utcnow: ZDT = DateManager.utcFromLocal(localnow)

    val currentChart = new ObjectProperty[Chart](this, "currentChart",setNowChart)
    val currentDatabase = new ObjectProperty[String](this, "currentDatabase", config.database)
    val currentUserData = new ObjectProperty[UserData](this, "currentUserData", setUserFromHereAndNow)

    val currentLocation = Bindings.createObjectBinding(
        () => AtlasQuery.getLocationFromUserData(currentUserData.value).get, currentUserData
    )

    def setNowChart: Chart = Chart(utcnow, defaultLocation.latitude, defaultLocation.longitude)

    def setUserFromHereAndNow: UserData =
        UserData("Momento actual","","",localnow.toString,defaultLocation.name,defaultLocation.country,defaultLocation.admin1,defaultLocation.admin2,defaultLocation.id)
}
