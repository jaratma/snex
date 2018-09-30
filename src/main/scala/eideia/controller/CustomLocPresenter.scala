package eideia.controller


import scalafx.Includes._
import eideia.{InitApp, Utils}
import eideia.atlas.{AtlasQuery, CountryResolver, QueryGeonames}
import eideia.models.{CustomPlace, Location, Place}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control._
import eideia.InitApp.config
import javafx.event.ActionEvent
import scalafx.beans.property.ObjectProperty

class CustomLocPresenter(countries: ChoiceBox[String],
                         searchField: TextField,
                         geoLocResults: TableView[Place],
                         customLocExplorer: TableView[CustomPlace],
                         saveButton: Button,
                         warningLabel: Label,
                         deleteButton: Button) {

    val countryNames: Seq[String] = CountryResolver.mapLocalizedCountryTocode(config.lang).keys.toList
    val rows = new ObservableBuffer[Place]
    val customrows = new ObservableBuffer[CustomPlace]
    val externLocs = new ObservableBuffer[Location]
    val customLocs = new ObservableBuffer[Location]

    val selectedGeoLocation = new ObjectProperty[Location](this,"selectedGeoLocation")
    val selectedCustomLocation = new ObjectProperty[Location](this,"selectedCustomLocation")

    countries.items = ObservableBuffer[String](countryNames)
    countries.selectionModel().select(initialCountry)

    searchField.onAction = ev => enterEvent(ev, searchField.text.value)

    fillCustomExplorer()

    geoLocResults.selectionModel().selectedItemProperty().onChange( (_,_,nval) => {
        if (nval != null) {
            val admin = AtlasQuery.getAdmin1Code(nval.admin.value)
            val location: Location = externLocs.find(p => p.name == nval.name.value && p.admin1 == admin).get
            AtlasQuery.searchPresetLocation(location.name, location.country, location.admin1) match {
                case Some(_) =>
                    warningLabel.text() = "Esta localidad ya existe"
                    saveButton.disable = true
                case None =>
                    warningLabel.text() = ""
                    selectedGeoLocation.value = location
                    saveButton.disable = false
                }
            }
        }
    )

    saveButton.onAction = handle {
        AtlasQuery.insertCustomLocation(selectedGeoLocation.value)
        fillCustomExplorer()
    }

    customLocExplorer.selectionModel().selectedItemProperty().onChange(
        (_,_,nval) => {
            if (nval != null) {
                val admin = AtlasQuery.getAdmin1Code(nval.admin.value)
                val location: Location = customLocs.find(p => p.name == nval.name.value && p.admin1 == admin).get
                selectedCustomLocation.value = location
                deleteButton.disable = false
            }
        }
    )
    deleteButton.onAction = handle{
        val r = AtlasQuery.deleteCustomLocation(selectedCustomLocation.value)
        fillCustomExplorer()
        InitApp.state.logger.info(s"Deleted $r row")
    }

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

    def fillCustomExplorer(): Unit = {
        val columns: Seq[Location] = AtlasQuery.listCustomLocation()
        customrows.clear()
        for (l <- columns) {
            val country = InitApp.countries(l.country)
            val admin: String = AtlasQuery.getAdmin1Name(l.country,l.admin1)
            val geo: String = Utils.formatJustLongAndLat(l)
            customrows += new CustomPlace(l.name, country, admin, geo, l.id)
        }
        customLocs ++= columns
        customLocExplorer.items = customrows
    }
}
