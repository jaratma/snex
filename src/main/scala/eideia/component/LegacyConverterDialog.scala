package eideia.component

import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{ButtonType, Dialog}
import scalafx.scene.layout.GridPane
import eideia.InitApp.state.logger

object LegacyConverterDialog {

    case class Bare(info: String)

    val grid: GridPane = new GridPane()

    def onConveterInvoked(stage: PrimaryStage): Unit = {

        val dialog = new Dialog[Bare] {
            initOwner(stage)
            title = "Entradas"
            width = 200
            resizable = true
        }

        val closeButtonType = new ButtonType("Cerrar", ButtonData.OKDone)
        dialog.dialogPane().content = grid
        dialog.resultConverter = dialogButton =>
            if (dialogButton == closeButtonType)
                Bare("Dialog closed.")
            else null

        val result = dialog.showAndWait()

        result match {
            case Some(Bare(text)) => logger.info(text)
            case Some(_) =>
            case None => logger.info("Dialog returned: None")
        }

    }

}
