package eideia.component

import eideia.atlas.AtlasQuery
import scalafx.Includes._
import org.kordamp.ikonli.javafx.FontIcon
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control._
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.{ColumnConstraints, GridPane, HBox}
import scalafx.scene.paint.Color
import eideia.controller.SearchLocationPresenter
import eideia.{InitApp, PrefixSelectionCustomizer}
import eideia.models.{Location, Place}

case class Loc(loc: Location)

object SearchLocationHelper {
    val searchField = new TextField {
        prefWidth = 180
        maxWidth = 180
    }

    val countryChoiceBox = new ChoiceBox[String] {
        prefWidth = 100
        maxWidth = 100
    }

    val searchLocButton = new Button {
        onAction = ev => presenter.searchLoc(ev, searchField.text.value)
        graphic = new FontIcon {
            setIconLiteral("gmi-search")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.DarkSlateGrey
        }
    }

    val geoLocResults: TableView[Place] = new TableView[Place] {
        id = "geoLocExplorer"
        columns ++= Seq(
            new TableColumn[Place,String]{
                text = ""
                cellValueFactory = { _.value.name}
            },
            new TableColumn[Place,String] {
                text = ""
                cellValueFactory = { _.value.admin}
            },
            new TableColumn[Place,String] {
                text = ""
                cellValueFactory = { _.value.geo}
            }
        )
        columnResizePolicy = TableView.ConstrainedResizePolicy
        visible = false
        prefHeight = 140
    }

    val locBox = new HBox {
        spacing = 5
        alignment = Pos.BaselineLeft
        children = Seq(
            searchField,
            searchLocButton,
            countryChoiceBox
        )
    }

    val saveButton = new Button("Guardar") {
        disable = true
    }

    val warningLabel = new Label("")


    val grid: GridPane = new GridPane() {
        hgap = 3
        vgap = 8
        padding = Insets(10, 10, 10, 10)

        val colCons1 = new ColumnConstraints(100,100, 120)
        val colCons2 = new ColumnConstraints(200,280, 300)

        columnConstraints ++= List(colCons1,colCons2)
        add(new Label("Localidad:"), 0,0)
        add(locBox,1,0)
        add(geoLocResults,0,1,2,1)
        add(warningLabel,1,2)
    }

    PrefixSelectionCustomizer.customize(countryChoiceBox)

    val presenter = new SearchLocationPresenter(countryChoiceBox,searchField,geoLocResults,warningLabel)
    val dialog = new Dialog[Loc] {
        initOwner(InitApp.stage.value)
        title = "Buscar localidad On-line"
        width = 200
        resizable = true
    }
    val locButtonType = new ButtonType("Aceptar", ButtonData.OKDone)
    dialog.dialogPane().buttonTypes = Seq(locButtonType)
    dialog.dialogPane().content = grid


    def onCustomLocEntryDialog(stage: PrimaryStage): Unit = {
        dialog.resultConverter = dialogButton =>
            if (dialogButton == locButtonType) {
                val loc: Location = presenter.selectedGeoLocation.value
                //AtlasQuery.insertCustomLocation(selectedGeoLocation.value)
                Loc(loc)
            }
            else null

        val result = dialog.showAndWait()

        result match {
            case Some(Loc(loc)) => println(loc)
            case Some(_) =>
           case None => println("None selected")
        }
    }

}
