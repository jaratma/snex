package eideia.component

import eideia.InitApp
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control._
import scalafx.scene.layout.{ColumnConstraints, GridPane}
import eideia.InitApp.state.logger
import eideia.controller.LegacyConverterPresenter
import scalafx.geometry.Insets

case class Bare(source: String, destiny: String)

object LegacyConverterDialog {

    val tableChoiceBox: ChoiceBox[String] = new ChoiceBox[String]

    val tableNameField: TextField = new TextField {
        text.onChange { (_, _, nv) =>
            convertButton.disable = nv.trim.isEmpty || !text().matches("[a-zA-Z][_a-zA-Z0-9]{2,25}")}
    }

    val grid: GridPane = new GridPane {
        hgap = 3
        vgap = 3
        padding = Insets(10, 10, 10, 10)

        val colCons1 = new ColumnConstraints(100, 100, 120)
        val colCons2 = new ColumnConstraints(200, 280, 300)

        columnConstraints ++= List(colCons1, colCons2)


        add(new Label("Origen:"), 0, 0)
        add(tableChoiceBox, 1, 0)
        add(new Label("Destino:"), 0, 1)
        add(tableNameField, 1, 1)
    }

    private val presenter = new LegacyConverterPresenter(tableChoiceBox) //userExplorer,searchField,deleteButton)

    def onConverterInvoked(): Unit = {
        if (InitApp.existsLegacyDB)
            convertDialog()
    }


    val dialog = new Dialog[Bare] {
        initOwner(InitApp.stage.value)
        title = "Importar tablas"
        width = 160
        resizable = true
    }

    val convertButtonType = new ButtonType("Convertir", ButtonData.OKDone)
    val closeButtonType = new ButtonType("Cerrar", ButtonData.Finish)
    dialog.dialogPane().buttonTypes = Seq(convertButtonType, closeButtonType)

    val convertButton = dialog.dialogPane().lookupButton(convertButtonType)
    convertButton.disable = true


    dialog.dialogPane().content = grid
    dialog.resultConverter = {
            case db if db == closeButtonType => Bare("Nothing","to show")
            case db if db == convertButtonType => Bare(tableChoiceBox.selectionModel().selectedItemProperty.value, tableNameField.text() )
            case _ => null
        }

    def convertDialog(stage: PrimaryStage = InitApp.stage.value): Unit = {

        val result = dialog.showAndWait()

        result match {
            case Some(Bare(s,d)) => presenter.convert(s,d)
            case Some(_) =>
            case None => logger.info("Dialog returned: None")
        }

    }

}
