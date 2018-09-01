package eideia.component

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.{ChoiceBox, TableColumn, TableView}
import scalafx.scene.layout.VBox
import scalafx.scene.control.TableColumn._
import eideia.InitApp.state
import eideia.{InitApp, State}
import eideia.models.Person
import eideia.controller.UserDataPresenter

object UserDataExplorerPane {
    lazy val config = InitApp.config

    val choiceTable: ChoiceBox[String] = new ChoiceBox[String]() {
        selectionModel().select(config.database)
    }

    val userExplorer: TableView[Person] = new TableView[Person]() {
        id = "explorer"
        columns +=  new TableColumn[Person,String] {
                text = ""
                cellValueFactory = { _.value.name }
            }
        columnResizePolicy = TableView.ConstrainedResizePolicy
    }


    private val presenter = new UserDataPresenter(choiceTable, userExplorer)

    val explorerPane = new VBox {
        padding = Insets(10)
        spacing = 6
        children = List(
            choiceTable,
            userExplorer
        )
    }
}
