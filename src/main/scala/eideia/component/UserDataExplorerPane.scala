package eideia.component

import scalafx.Includes._
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, Pane, Priority, VBox}
import scalafx.scene.control.TableColumn._
import scalafx.scene.text.Text
import scalafx.scene.paint.Color
import org.kordamp.ikonli.javafx.FontIcon
import eideia.InitApp.state
import eideia.InitApp
import eideia.models.Person
import eideia.controller.UserDataPresenter
import scalafx.scene.control.MenuItem._

object UserDataExplorerPane {
    lazy val config = InitApp.config


    val spacer: Pane = new Pane {
        minWidth = 10
        hgrow = Priority.Always
    }

    val toc = new ToggleGroup {
        selectedToggle.onChange(
            (_,old,nval) => {
                nval match {
                    case null => deleteButton.graphic.value = delIconInactive
                    case _ => deleteButton.graphic.value = delIconActive
                }
            }
        )
    }

    val delIconActive: FontIcon = new FontIcon {
            setIconLiteral("gmi-delete")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }

    val delIconInactive: FontIcon = new FontIcon {
        setIconLiteral("gmi-delete")
        iconSizeProperty.value = 18
        iconColorProperty.value = Color.Silver
    }


    val deleteButton: ToggleButton = new ToggleButton {
        toggleGroup = toc
        selected = false
        graphic = delIconInactive
        tooltip = new Tooltip("Activar para eliminar")
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 5; -fx-background-insets: 0, 0"
    }

    val editButton: Button = new Button {
        graphic = new FontIcon {
            setIconLiteral("gmi-edit")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }
        tooltip = new Tooltip("Editar")
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 5; -fx-background-insets: 0, 0"
        onAction = handle(DataEntryDialog.onEditDataEntryDialog(InitApp.stage.value, presenter))
    }

    val addButton: Button = new Button {
        graphic = new FontIcon {
            setIconLiteral("gmi-add")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }
        tooltip = new Tooltip("Añadir")
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 5; -fx-background-insets: 0, 0"
        onAction = handle(DataEntryDialog.onNewDataEntryDialog(InitApp.stage.value, presenter))
    }

    /* Table box */

    val choiceTable: ChoiceBox[String] = new ChoiceBox[String]() {
        selectionModel().select(config.database)
    }

    val settingsMenu = new ContextMenu {
        items +=  new MenuItem("Convertir datos v.1") {
            //onAction = presenter.convertLegacyData
        }
    }

    val settingsButton: Button = new Button {
        graphic = new FontIcon {
            setIconLiteral("gmi-settings")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }
        tooltip = new Tooltip("Configurar tablas")
        onMousePressed = ev => settingsMenu.show(this, ev.getScreenX, ev.getScreenY)
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 5; -fx-background-insets: 0, 0"
    }

    val tableBox: HBox = new HBox {
        hgrow = Priority.Always
        spacing = 4
        children = Seq(
            choiceTable,
            spacer,
            settingsButton
        )
    }

    /* Explorer */

    val userExplorer: TableView[Person] = new TableView[Person]() {
        id = "explorer"
        columns +=  new TableColumn[Person,String] {
                text = ""
                cellValueFactory = { _.value.name }
            }
        columnResizePolicy = TableView.ConstrainedResizePolicy
    }

    /* Search box  */

    val searchField: TextField = new TextField {
        hgrow = Priority.Always
        onAction = (ev) => presenter.searchAction(ev)
    }

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
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 5; -fx-background-insets: 0, 0"
        onAction = ev => presenter.clearAction(ev)
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

    /* Labels card */
    val card = new VBox {
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
                children = Seq(addButton,editButton,deleteButton)
            }
        )
    }


    private val presenter = new UserDataPresenter(choiceTable, userExplorer,searchField,deleteButton)

    val explorerPane = new VBox {
        padding = Insets(10)
        spacing = 6
        children = List(
            card,
            searchBox,
            tableBox,
            userExplorer
        )
    }
}
