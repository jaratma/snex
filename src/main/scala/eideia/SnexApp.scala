package eideia

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application
import scalafx.scene.Scene
import eideia.component.UserDataExplorerPane
import eideia.models.UserData
import java.io._

object SnexApp extends JFXApp {

    stage = new application.JFXApp.PrimaryStage
    InitApp.stage.value = stage

    //val f = new File("/users/jose/sernex")
    //val iis = new ObjectInputStream(new FileInputStream(f))
    //val col = iis.readObject.asInstanceOf[List[UserData]]

    stage.scene = new Scene {
        root = UserDataExplorerPane.explorerPane
        stylesheets += getClass.getResource("/styles.css").toExternalForm
    }

}
