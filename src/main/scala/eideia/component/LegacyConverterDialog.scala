package eideia.component

import eideia.InitApp
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control._
import scalafx.scene.layout.{ColumnConstraints, GridPane}
import eideia.InitApp.logger
import eideia.controller.LegacyConverterPresenter
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.scene.text.{Text, TextFlow}

case class Bare(source: String, destiny: String)

object LegacyConverterDialog {

    val mainTableChoice = new ObjectProperty[ChoiceBox[String]](this, "maintables")

    val tableChoiceBox: ChoiceBox[String] = new ChoiceBox[String]

    val tableNameField: TextField = new TextField {
        text.onChange { (_, _, nv) =>
            convertButton.disable = nv.trim.isEmpty || !text().matches("[a-zA-Z][_a-zA-Z0-9]{2,25}")}
    }

    val convertButton = new Button("Convertir"){
        disable = true
        onAction = handle {
            val dest: String = presenter.convert(tableChoiceBox.selectionModel().selectedItemProperty.value, tableNameField.text())
            mainTableChoice.value.items() += dest
        }
    }

    val flow: TextFlow = new TextFlow {
        padding = Insets(3)
    }

    val spane: ScrollPane = new ScrollPane {
        content = flow
        vvalue = 1.0
        minHeight = 200
    }

    val recover = new Hyperlink("Recuperar") {
        onAction = handle { presenter.checkDestinyCollecion(tableNameField.text.value) }
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
        add(convertButton,0,2)
        add(spane ,0,3,2,1)
        add(recover ,0,4,2,1)
    }

    private val presenter = new LegacyConverterPresenter(tableChoiceBox, flow, spane)

    def onConverterInvoked(mainchoicer: ChoiceBox[String]): Unit = {
        if (InitApp.existsLegacyDB)
            mainTableChoice.value = mainchoicer
            convertDialog()
    }

    val dialog = new Dialog[Bare] {
        initOwner(InitApp.stage.value)
        title = "Importar tablas"
        width = 160
        resizable = true
    }

    val closeButtonType = new ButtonType("Cerrar", ButtonData.Finish)
    dialog.dialogPane().buttonTypes = Seq(closeButtonType)
    dialog.dialogPane().content = grid

    def convertDialog(stage: PrimaryStage = InitApp.stage.value): Unit =  {
        dialog.showAndWait()
        flow.children.clear()
        tableNameField.text() = ""
    }

}
