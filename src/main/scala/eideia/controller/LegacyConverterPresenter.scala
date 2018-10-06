package eideia.controller

import java.io._

import eideia.InitApp
import eideia.InitApp.state
import eideia.component.SearchLocationHelper
import eideia.models.{Location, UserData}
import scalafx.Includes._
import eideia.userdata.{LegacyDataManager, LegacyEssentialFields, UserDataManager}
import scalafx.beans.property.StringProperty
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.text.TextFlow

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success, Try}

class LegacyConverterPresenter(choice: ChoiceBox[String], flow: TextFlow, spane: ScrollPane) {

    var err = false
    choice.items = tableNames
    choice.selectionModel().selectFirst()

    def tableNames: ObservableBuffer[String] = {
        val names : Seq[String] = LegacyDataManager.getTableNamesFromDb
        ObservableBuffer(names)
    }

    def convert(source: String, destiny: String): String = {
        val legacyData: Seq[LegacyEssentialFields] = LegacyDataManager.getEssentialFieldsFromLegacyData(source)
        var realData: ArrayBuffer[UserData] = new ArrayBuffer[UserData]()
        legacyData foreach { ld =>
            val text = s"${ld.first}, ${ld.last} ${ld.city} ${ld.country}"
            Try(UserDataManager.convertLegacyData(ld)) match {
                case Success(userdata) =>
                    err = false
                    realData += userdata
                    insertLogText(s"Convirtiendo $text")
                case Failure(exception) =>
                    err = true
                    val name = ld.first.replace(" ", "-") + "-" + ld.date
                    val oos = new ObjectOutputStream(new FileOutputStream(InitApp.failDir+s"/$name"))
                    oos.writeObject(ld)
                    oos.close()
                    insertLogText(s"Error convirtiendo $text")
            }
        }
        UserDataManager.insertBatchData(realData, destiny)
        destiny
    }

    def checkDestinyCollecion(table: String) = {
        table match {
            case name if name.isEmpty =>
                new Alert(AlertType.Warning) {
                        initOwner(InitApp.stage.value)
                        title = "Information Dialog"
                        headerText = "Nombre de tabla"
                        contentText = "Se necesita una tabla destino para los datos."
                    }.showAndWait()
            case name => recoverFailed(name)
        }
    }

    def recoverFailed(table: String) = {
        val failed = mutable.Map[String,LegacyEssentialFields]()
        flow.children.clear()
        InitApp.failDir.listFiles foreach { f =>
            val ois = new ObjectInputStream(new FileInputStream(f))
            val legacy = ois.readObject.asInstanceOf[LegacyEssentialFields]
            ois.close()
            val name = s"${legacy.first} ${legacy.last}"
            failed += name -> legacy
            val link = new Hyperlink(name) {
                id = legacy.first.replace(" ", "-") + "-" + legacy.date
                onAction = ev => { manageRecover(failed(name),table, id) }
            }
            flow.children.add(new Text("\n"))
            flow.children.add(link)
        }
    }

    def manageRecover(legacy: LegacyEssentialFields, table: String, eid: StringProperty) = {
        val loc: Location = SearchLocationHelper.onSearchHelperDialog(InitApp.stage.value,legacy)
        val toBeLoc = Option(loc)
        toBeLoc match {
            case Some(loc) =>
                val leg = legacy.copy(city = loc.name, country = loc.country, zone = loc.timezone)
                val data = UserDataManager.convertLegacyData(leg)
                val r = UserDataManager.insertUserData(data,table)
                state.logger.info(s"Inserted $r register.")
                val link = flow.children.find(_.id == eid)
                link match {
                    case Some(el) =>
                        flow.children.removeAll(el)
                        val path = InitApp.failDir + s"/${eid.value}"
                        val recfile = new File(path)
                        recfile.delete()
                        state.logger.info(s"Recovery file deleted: ${eid.value}")
                    case None =>
                }
            case _ =>
        }

    }

    def insertLogText(data: String) = {
        val txt = new Text {
            text = s"$data\n"
            fill = if (err) Color.OrangeRed else Color.BlueViolet
        }
        flow.children.addAll(txt)
        spane.vvalue = 1.0
    }

}

