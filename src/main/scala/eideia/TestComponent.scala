package eideia

import eideia.component.UserDataExplorerPane
import scalafx.application.JFXApp
import scalafx.Includes._
import scalafx.application
import scalafx.scene.Scene

object TestComponent extends JFXApp {
    stage = new application.JFXApp.PrimaryStage {
        scene = new Scene {
            root = UserDataExplorerPane.explorerPane
            stylesheets += getClass.getResource("/styles.css").toExternalForm
        }
    }
}
