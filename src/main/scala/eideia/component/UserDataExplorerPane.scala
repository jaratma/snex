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
import eideia.models.{Person, UserData}
import eideia.controller.UserDataPresenter
import eideia.userdata.UserDataManager
import scalafx.scene.control.MenuItem._

object UserDataExplorerPane {
    lazy val config = InitApp.config


    def spacer: Pane = new Pane {
        minWidth = 10
        hgrow = Priority.Always
    }

    val delIconActive: FontIcon = new FontIcon {
            setIconLiteral("gmi-delete")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }

    val deleteButton: Button = new Button {
        graphic = delIconActive
        tooltip = new Tooltip("Eliminar registro")
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

    val placeButton: Button = new Button {
        graphic = new FontIcon {
            setIconLiteral("gmi-place")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }
        tooltip = new Tooltip("Lugares")
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 5; -fx-background-insets: 0, 0"
        onAction = handle(CustomLocDialog.onCustomLocEntryDialog(InitApp.stage.value))
    }

    val nowButton: Button = new Button {
        graphic = new FontIcon {
            setIconLiteral("gmi-access-time")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }
        tooltip = new Tooltip("Momento actual")
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 5; -fx-background-insets: 0, 0"
        onAction = handle(state.setNow)
    }

    val bookmarksMenu = new ContextMenu {
        items ++= Seq()
    }

    val bookmarksButton: Button = new Button {
        graphic = new FontIcon {
            setIconLiteral("gmi-book")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }
        tooltip = new Tooltip("Marcadores")
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 5; -fx-background-insets: 0, 0"
        onMouseClicked = ev => {
            bookmarksMenu.items.clear()
            val fav: Seq[UserData] = UserDataManager.getAllRowsFromTable(config.bookmarks)
            fav.foreach { u =>
                bookmarksMenu.items += new MenuItem(u.first + " " + u.last) {
                    onAction = handle {
                        state.currentUserData.value = u
                        state.infoLabels.update(state.currentUserData.value)
                    }
                }
            }
            bookmarksMenu.show(this, ev.getScreenX, ev.getScreenY)
        }
    }


    val histMenu = new ContextMenu {
        items ++= Seq()
    }

    val recentButton: Button = new Button {
        graphic = new FontIcon {
            setIconLiteral("gmi-recent-actors")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }
        tooltip = new Tooltip("Registros recientes")
        style = "-fx-background-color: #e3e4e4; -fx-background-radius: 5; -fx-background-insets: 0, 0"
        onMouseClicked = ev => {
            histMenu.items.clear()
            InitApp.mostRecentData.foreach { u =>
                histMenu.items += new MenuItem(u.first + " " + u.last) {
                    onAction = handle {
                        state.currentUserData.value = u
                        state.infoLabels.update(state.currentUserData.value)
                    }
                }
            }
            histMenu.show(this, ev.getScreenX, ev.getScreenY)
        }
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

    /* */

    val rowsMenu = new ContextMenu {
        items ++= Seq(
            new MenuItem("Copiar") {
                onAction = handle(presenter.copyRegister)
            },
            new MenuItem("Mover") {
                onAction = handle(presenter.moveRegister)
            },
        )
    }

    /* */

    /* Table box */

    val choiceTable: ChoiceBox[String] = new ChoiceBox[String]() {
        selectionModel().select(config.database)
    }

    val settingsMenu = new ContextMenu {
        items ++= Seq(
            new MenuItem("Crear colección"){
                onAction = handle(presenter.createTable)
            },
            new MenuItem("Eliminar colección"){
                onAction = handle(presenter.deleteTable)
            },
            new MenuItem("Copiar colección") {
                onAction = handle(presenter.copyTable)
            },
            new MenuItem("Exportar colección") {
                onAction = handle(presenter.exportTable)
            },
            new MenuItem("Importar colección") {
                onAction = handle(presenter.importTable)
            },
            new MenuItem("Convertir datos v.1") {
                onAction = handle(LegacyConverterDialog.onConverterInvoked(choiceTable))
            },
        )
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
        selectionModel().selectionMode = SelectionMode.Multiple
        contextMenu = rowsMenu
    }

    /* Search box  */

    val searchField: TextField = new TextField {
        hgrow = Priority.Always
        onAction = (ev) => presenter.searchAction(ev)
    }

    val searchIcon: Text = new FontIcon {
        setIconLiteral("gmi-search")
        iconSizeProperty.value = 24
        iconColorProperty.value = Color.Thistle
    }

    val clearButton: Button = new Button {
        graphic = new FontIcon {
            setIconLiteral("gmi-clear")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.SlateGray
        }
        tooltip = new Tooltip("Limpiar")
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
                children = Seq(placeButton,spacer,nowButton,bookmarksButton,recentButton,addButton,editButton,deleteButton)
            }
        )
    }


    private val presenter = new UserDataPresenter(choiceTable, userExplorer,searchField,deleteButton)

    val explorerPane = new VBox {
        prefWidth = 320
        prefHeight = 700
        padding = Insets(6)
        spacing = 6
        children = List(
            card,
            searchBox,
            tableBox,
            userExplorer
        )
        vgrow = Priority.Always
    }
}
