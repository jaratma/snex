package eideia

import java.io.{File, FileOutputStream, ObjectOutputStream}

import scala.util.Properties
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import eideia.component.UserDataExplorerPane
import eideia.surface.CanvasSurface
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout._
import scalafx.scene.text.TextFlow


object SnexApp extends JFXApp {
    val userHome: String = Properties.userHome
    val userConfPath = userHome + "/.nex2"
    val userDir = new File(userConfPath)
    userDir.mkdir

    stage = new JFXApp.PrimaryStage {
        title = "Astro-Nex"
        minWidth = 720
        minHeight = 420
    }
    InitApp.stage.value = stage

    //val infoTextFlow = new TextFlow {
    //    padding = Insets(3)
    //    prefWidth = 600
    //    prefHeight = 80
    //    style = "-fx-border-color: lightsteelblue; "
    //}

    val mainHbox = new BorderPane {
        prefWidth = 1320
        prefHeight = 800
        minWidth = 720
        minHeight = 420
        left = UserDataExplorerPane.explorerPane
        center =  CanvasSurface.drawingPane
        bottom = new HBox {
            style = "-fx-background-color: #e0e0e0"
            children = Seq(
                new Label("status text") {
                    padding = Insets(2)
                    prefHeight = 18
                    maxHeight = 18
                    minHeight = 18
            })
        }
    }

    stage.scene = new Scene(1320,800) {
        root = mainHbox
        stylesheets += getClass.getResource("/styles.css").toExternalForm
        InitApp.state.currentOperation.value = "drawRadix"
    }

    override def stopApp() {
        if (InitApp.mostRecentData.nonEmpty) {
            val f = new File(InitApp.mruFile)
            val oos = new ObjectOutputStream(new FileOutputStream(f))
            oos.writeObject(InitApp.mostRecentData)
            oos.close()
        }
    }
}
