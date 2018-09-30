package eideia.controller

import scalafx.Includes._
import scalafx.scene.control.{ChoiceBox, Label, TableView, TextField}
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.layout.GridPane
import eideia.{InitApp, Utils}
import eideia.InitApp.config
import eideia.atlas.{AtlasQuery, CountryResolver, QueryGeonames}
import eideia.models.{Location, Place}
import scalafx.beans.property.ObjectProperty

class DataEntryPresenter(grid: GridPane,
                         searchField: TextField,
                         countries: ChoiceBox[String],
                         locExplorer: TableView[Place],
                         place: ObjectProperty[Location],
                         labelforLoc: Label) {

    val countryNames: Seq[String] = CountryResolver.mapLocalizedCountryTocode(config.lang).keys.toList
    val rows = new ObservableBuffer[Place]
    val externLocs = new ObservableBuffer[Location]

    countries.items = ObservableBuffer[String](countryNames)
    countries.selectionModel().select(initialCountry)

    searchField.onAction = ev => enterEvent(ev, searchField.text.value)

    def initialCountry: String = CountryResolver.choiceOne(config.country, config.lang)

    def enterEvent(ev: ActionEvent, text: String) = {
        if (!text.isEmpty) {
            fillExplorer(text)
        }
    }

    def searchLoc(ev: ActionEvent, text: String) = {
        if (!text.isEmpty) {
            fillExplorer(text)
        }
    }

    def fillColumns(text: String,code: String): Seq[Location] = {
        println(text, code)
        val columns = AtlasQuery.getLocationFromCityAndCountryCode(text,code)
        println(columns)
        columns match {
            case Vector() =>
                externLocs.clear()
                QueryGeonames.sendQuery(text,code) match {
                    case Right(res) => externLocs ++= QueryGeonames.parseQuery(res)
                    case Left(err) => InitApp.state.logger.info("No response")
                }
                externLocs
            case _ => columns
        }
    }

    def fillExplorer(text: String): Unit = {
        val countryName = countries.selectionModel().selectedItemProperty().getValue
        val code = InitApp.localizedCountries(countryName)
        val columns = fillColumns(text,code)
        rows.clear()
        for (l <- columns) {
            val admin: String = AtlasQuery.getAdmin1Name(code,l.admin1)
            val geo: String = Utils.formatJustLongAndLat(l)
            rows += new Place(l.name, admin, geo, l.id)
        }
        locExplorer.items = rows
        locExplorer.visible = true
    }

    locExplorer.selectionModel().selectedItemProperty().onChange( (_,_,nval) => {
        if (nval != null) {
            val admin = AtlasQuery.getAdmin1Code(nval.admin.value)
            val location: Location = externLocs.find(p => p.name == nval.name.value && p.admin1 == admin) match {
                case Some(loc) =>
                    AtlasQuery.insertCustomLocation(loc)
                    loc
                case None => AtlasQuery.getLocationFromCityAndId(nval.name.value, nval.id)
            }
            place.value = location
            labelforLoc.text.value = location.toString
            locExplorer.visible = false
            searchField.text.value = ""
        }
    })
}
