package eideia.component

import eideia.component
import eideia.models.UserData
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control._
import scalafx.scene.layout.GridPane

case class BareData(first: String, last: String, tags: String)


object DataEntryDialog {

    def onShowDataEntryDialog(stage: PrimaryStage): Unit = {

        val dialog = new Dialog[BareData] {
            initOwner(stage)
            title = "Entradas"
        }

        val loginButtonType = new ButtonType("Crear", ButtonData.OKDone)
        dialog.dialogPane().buttonTypes = Seq(loginButtonType, ButtonType.Cancel)

        val firstName = new TextField
        val lastName = new TextField
        val tags = new TextArea

        val grid = new GridPane() {
            hgap = 10
            vgap = 10
            padding = Insets(20, 100, 10, 10)

            add(new Label("Nombre:"), 0, 0)
            add(firstName, 1, 0)
            add(new Label("Apellidos:"), 0, 1)
            add(lastName, 1, 1)
            add(new Label("Tags:"), 0,2)
            add(tags, 1, 2)
        }

        dialog.dialogPane().content = grid
        dialog.resultConverter = dialogButton =>
            if (dialogButton == loginButtonType) BareData(firstName.text(), lastName.text(), tags.text())
            else null

        val result = dialog.showAndWait()

        result match {
            case Some(BareData(f, l, t)) => println("Nombre: " + f + "Apellido: " + l)
            case Some(_) =>
            case None               => println("Dialog returned: None")
        }

    }
}
