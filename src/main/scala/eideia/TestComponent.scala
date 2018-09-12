package eideia

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application
import scalafx.scene.Scene
import eideia.component.UserDataExplorerPane

object TestComponent extends JFXApp {

    stage = new application.JFXApp.PrimaryStage
    InitApp.stage.value = stage

    stage.scene = new Scene {
        root = UserDataExplorerPane.explorerPane
        stylesheets += getClass.getResource("/styles.css").toExternalForm
    }

}
