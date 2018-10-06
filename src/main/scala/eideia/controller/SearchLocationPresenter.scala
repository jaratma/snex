package eideia.controller

import scalafx.Includes._
import eideia.{InitApp, Utils}
import eideia.atlas.{AtlasQuery, CountryResolver, QueryGeonames}
import eideia.models.{Location, Place}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control._
import eideia.InitApp.config
import javafx.event.ActionEvent
import scalafx.beans.property.ObjectProperty

class SearchLocationPresenter(countries: ChoiceBox[String],
                         searchField: TextField,
                         geoLocResults: TableView[Place],
                         warningLabel: Label) {

    val countryNames: Seq[String] = CountryResolver.mapLocalizedCountryTocode(config.lang).keys.toList
    val rows = new ObservableBuffer[Place]
    val externLocs = new ObservableBuffer[Location]

    val selectedGeoLocation = new ObjectProperty[Location](this,"selectedGeoLocation")
    val newGeoLocation = new ObjectProperty[Location](this,"selectedGeoLocation")

    countries.items = ObservableBuffer[String](countryNames)
    countries.selectionModel().select(initialCountry)

    searchField.onAction = ev => enterEvent(ev, searchField.text.value)

    geoLocResults.selectionModel().selectedItemProperty().onChange( (_,_,nval) => {
        if (nval != null) {
            val admin = AtlasQuery.getAdmin1Code(nval.admin.value)
            val location: Location = externLocs.find(p => p.name == nval.name.value && p.admin1 == admin).get
            AtlasQuery.searchPresetLocation(location.name, location.country, location.admin1) match {
                case Some(_) =>
                    warningLabel.text() = "Usando localidad existente"
                    selectedGeoLocation.value = location
                case None =>
                    warningLabel.text() = ""
                    newGeoLocation.value = location
            }
        }
    })

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
        externLocs.clear()
        QueryGeonames.sendQuery(text,code) match {
            case Right(res) => externLocs ++= QueryGeonames.parseQuery(res)
            case Left(err) => InitApp.state.logger.info("No response")
        }
        externLocs
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
        geoLocResults.items = rows
        geoLocResults.visible = true
    }

}
