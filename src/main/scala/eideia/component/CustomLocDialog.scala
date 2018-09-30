package eideia.component

import eideia.controller.CustomLocPresenter
import scalafx.Includes._
import org.kordamp.ikonli.javafx.FontIcon
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control._
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.{ColumnConstraints, GridPane, HBox}
import scalafx.scene.paint.Color
import eideia.{InitApp, PrefixSelectionCustomizer}
import eideia.models.{CustomPlace, Place}

case class BareLoc(info: String)

object CustomLocDialog {
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

    val customLocExplorer: TableView[CustomPlace] = new TableView[CustomPlace] {
        id = "customLocExplorer"
        columns ++= Seq(
            new TableColumn[CustomPlace,String]{
                text = ""
                cellValueFactory = { _.value.name}
            },
            new TableColumn[CustomPlace,String] {
                text = ""
                cellValueFactory = { _.value.country}
            },
            new TableColumn[CustomPlace,String] {
                text = ""
                cellValueFactory = { _.value.admin}
            },
            new TableColumn[CustomPlace,String] {
                text = ""
                cellValueFactory = { _.value.geo}
            }
        )
        columnResizePolicy = TableView.ConstrainedResizePolicy
        prefHeight = 180
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

    val deleteButton = new Button("Eliminar") {
        disable = true
    }

    val warningLabel = new Label("")

    val labelForCustom = new Label {
        text = "Localidades personalizadas"
        padding = Insets(2)
        style = "-fx-border-color: papayawhip; -fx-text-fill: maroon; -fx-label-padding: 2;"
    }
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
        add(saveButton,0,2)
        add(warningLabel,1,2)
        add(labelForCustom,0,3,2,1)
        add(customLocExplorer,0,4,2,1)
        add(deleteButton,0,5)
    }

    PrefixSelectionCustomizer.customize(countryChoiceBox)

    val presenter = new CustomLocPresenter(countryChoiceBox,searchField,geoLocResults, customLocExplorer, saveButton, warningLabel,deleteButton)
    val dialog = new Dialog[BareLoc] {
        initOwner(InitApp.stage.value)
        title = "Geonames On-line"
        width = 200
        resizable = true
    }
    val locButtonType = new ButtonType("Aceptar", ButtonData.OKDone)
    dialog.dialogPane().buttonTypes = Seq(locButtonType)
    dialog.dialogPane().content = grid


    def onCustomLocEntryDialog(stage: PrimaryStage): Unit = {
        //dialog.resultConverter = dialogButton =>
        //    if (dialogButton == locButtonType) {
        //            BareLoc
        //    }
        val result = dialog.showAndWait()
    }

}
