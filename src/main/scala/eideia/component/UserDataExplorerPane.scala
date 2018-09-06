package eideia.component

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.control.TableColumn._
import scalafx.scene.text.Text
import scalafx.scene.paint.Color
import org.kordamp.ikonli.javafx.FontIcon
import eideia.InitApp.state
import eideia.InitApp
import eideia.models.Person
import eideia.controller.UserDataPresenter

object UserDataExplorerPane {
    lazy val config = InitApp.config


    val searchIcon: Text = new FontIcon {
            setIconLiteral("gmi-search")
            iconSizeProperty.value = 28
            iconColorProperty.value = Color.Thistle
        }

    val clearButton: Button = new Button {
        graphic = new FontIcon {
            setIconLiteral("gmi-clear")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 60; -fx-background-insets: 0, 0"
        onAction = (ev) => presenter.clearAction(ev)
    }

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

    val searchField: TextField = new TextField {
        onAction = (ev) => presenter.searchAction(ev)
    }

    val hbox = new HBox {
            spacing = 4
            children = Seq(
                searchField,
                searchIcon,
                clearButton
            )
        }

    val card= new VBox {
        padding = Insets(6)
        spacing = 4
        style = "-fx-border-color: lightsteelblue; -fx-border-radius: 2;"
        children = Seq(
            new Label {
                text <== state.infoLabels().firstNameLabel.concat(state.infoLabels().lastNameLabel)
                style = "-fx-text-fill: slateblue"
            },
            new Label {
                text <== state.dateLabel
                style = "-fx-text-fill: sienna"
            },
            new Label {
                text <== state.infoLabels.value.geoLabel
            }
        )
    }


    private val presenter = new UserDataPresenter(choiceTable, userExplorer,searchField)

    val explorerPane = new VBox {
        padding = Insets(10)
        spacing = 6
        children = List(
            card,
            hbox,
            choiceTable,
            userExplorer
        )
    }
}
