package eideia.component

import scalafx.Includes._
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control._
import scalafx.scene.control.MenuItem._
import scalafx.scene.layout.{HBox, Priority, VBox}
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
        onAction = ev => presenter.clearAction(ev)
    }

    val editButton: Button = new Button {
        graphic = new FontIcon {
            setIconLiteral("gmi-edit")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 60; -fx-background-insets: 0, 0"
        onAction = handle(DataEntryDialog.onEditDataEntryDialog(InitApp.stage.value, presenter))
    }

    val addButton: Button = new Button {
        graphic = new FontIcon {
            setIconLiteral("gmi-add")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 60; -fx-background-insets: 0, 0"
        onAction = handle(DataEntryDialog.onNewDataEntryDialog(InitApp.stage.value, presenter))
    }
    val choiceTable: ChoiceBox[String] = new ChoiceBox[String]() {
        selectionModel().select(config.database)
    }

    val dataMenu = new ContextMenu {
        items +=
            new MenuItem("Eliminar") {
                onAction = { ev => presenter.deleteUser(ev) }
            }
    }

    val userExplorer: TableView[Person] = new TableView[Person]() {
        id = "explorer"
        columns +=  new TableColumn[Person,String] {
                text = ""
                cellValueFactory = { _.value.name }
            }
        columnResizePolicy = TableView.ConstrainedResizePolicy
        //rowFactory = tv => new TableRow[Person] {
        //    onMouseClicked = mv => if (mv.getButton == MouseButton.SECONDARY)  {
        //        selectionModel()
        //        mv.consume()
        //        println(mv)
        //        //println(tv.rowFactory.value)
        //    }
        //}
        //onMouseClicked = ev => { if (ev.getButton == MouseButton.SECONDARY) {
        //   dataMenu.show(userExplorer, ev.getScreenX, ev.getScreenY)
        //} }
        contextMenu = dataMenu
    }

    val searchField: TextField = new TextField {
        hgrow = Priority.Always
        onAction = (ev) => presenter.searchAction(ev)
    }

    val searchBox = new HBox {
            hgrow = Priority.Always
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
                text <== state.infoLabels.firstNameLabel.concat(state.infoLabels.lastNameLabel)
                style = "-fx-text-fill: slateblue"
            },
            new Label {
                text <== state.infoLabels.dateLabel
                style = "-fx-text-fill: sienna"
            },
            new Label {
                text <== state.infoLabels.geoLabel
            },
            new HBox {
                alignment = Pos.BaselineRight
                spacing = 4
                children = Seq(addButton,editButton)
            }
        )
    }


    private val presenter = new UserDataPresenter(choiceTable, userExplorer,searchField)

    val explorerPane = new VBox {
        padding = Insets(10)
        spacing = 6
        children = List(
            card,
            searchBox,
            choiceTable,
            userExplorer
        )
    }
}
