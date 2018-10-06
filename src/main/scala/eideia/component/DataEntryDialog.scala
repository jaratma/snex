package eideia.component

import java.text.SimpleDateFormat
import java.time._
import java.util.Date

import javafx.scene.control.{SpinnerValueFactory => jfxSpinnerValueFactory}
import scalafx.geometry.{HPos, Insets, Pos}
import scalafx.scene.layout.HBox
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control._
import scalafx.scene.layout.{ColumnConstraints, GridPane}
import scalafx.scene.control.TableColumn._
import scalafx.scene.paint.Color
import org.kordamp.ikonli.javafx.FontIcon
import eideia.controller.{DataEntryPresenter, UserDataPresenter}
import eideia.models.{Location, Place, UserData}
import eideia.InitApp.state
import eideia.atlas.AtlasQuery
import eideia.{InitApp, PrefixSelectionCustomizer}
import scalafx.util.converter.TimeStringConverter

case class BareData(first: String, last: String, tags: String, date: ZonedDateTime, place: Location)


object DataEntryDialog {
    val firstName = new TextField
    val lastName = new TextField
    val tags = new TextArea {
        prefRowCount = 1
        prefColumnCount = 20
        promptText = "cero o más etiquetas"
    }
    val datePicker = new DatePicker(LocalDate.now)
    val hourPicker = new Spinner(min = 0: Int, max = 23: Int, LocalTime.now.getHour) {
        editable = true
        prefWidth = 70
        maxWidth = 70
        editor.value.alignment = Pos.BaselineRight
    }
    val minutePicker = new Spinner(0,59, LocalTime.now.getMinute) {
        editable = true
        prefWidth = 70
        maxWidth = 70
        editor.value.alignment = Pos.BaselineRight
    }

    val secondPicker = new Spinner(0,59, 0) {
        editable = true
        prefWidth = 70
        maxWidth = 70
        editor.value.alignment = Pos.BaselineRight
    }


    def timeField = new TextField {
        val timeFormat = new SimpleDateFormat("HH:mm:ss")
        val converter = new TimeStringConverter("HH:mm:ss")
        textFormatter = new TextFormatter(converter)
        val now = LocalTime.now
        text = s"${now.getHour}:${now.getMinute}:${now.getSecond}"
        alignment = Pos.BaselineRight
    }

    val locExplorer: TableView[Place] = new TableView[Place] {
        id = "locExplorer"
        columns ++= Seq(
            new TableColumn[Place,String]{
                text = ""
                cellValueFactory = { _.value.name}
            },
            new TableColumn[Place,String] {
                text = ""
                cellValueFactory = { _.value.admin}
            },
            new TableColumn[Place,String] {
                text = ""
                cellValueFactory = { _.value.geo}
            }
        )
        columnResizePolicy = TableView.ConstrainedResizePolicy
        visible = false
        prefHeight = 200
    }

    val place = new ObjectProperty[Location](this, "loc", state.currentLocation.value)

    val searchField = new TextField {
        prefWidth = 180
        maxWidth = 180
    }

    val countryChoiceBox = new ChoiceBox[String] {
        prefWidth = 100
        maxWidth = 100
    }

    val searchLocButton = new Button {
        onAction = ev => presenter.searchLoc(ev, searchField.text.value)
        graphic = new FontIcon {
            setIconLiteral("gmi-search")
            iconSizeProperty.value = 18
            iconColorProperty.value = Color.DarkSlateGrey
        }
    }

    val labelForLoc = new Label("") {
        text = place.value.toString
        padding = Insets(2)
        style = "-fx-border-color: papayawhip; -fx-text-fill: maroon; -fx-label-padding: 2;"
    }

    val grid: GridPane = new GridPane() {
        hgap = 3
        vgap = 3
        padding = Insets(10, 10, 10, 10)

        val colCons1 = new ColumnConstraints(100,100, 120)
        val colCons2 = new ColumnConstraints(200,280, 300)

        val timeBox = new HBox {
            spacing = 5
            alignment = Pos.BaselineLeft
            children = Seq(
                hourPicker,
                new Label("h."),
                minutePicker,
                new Label("m."),
                secondPicker,
                new Label("s."),
            )
        }

        val locBox = new HBox {
            spacing = 5
            alignment = Pos.BaselineLeft
            children = Seq(
                searchField,
                searchLocButton,
                countryChoiceBox
            )
        }


        columnConstraints ++= List(colCons1,colCons2)
        add(new Label("Nombre:"), 0, 0)
        add(firstName, 1, 0)
        add(new Label("Apellidos:"), 0, 1)
        add(lastName, 1, 1)
        add(new Label("Tags:"), 0,2)
        add(tags, 1, 2)
        add(new Label("Fecha:"), 0,3)
        add(datePicker,1,3)
        add(new Label("Hora:"), 0,4)
        add(timeBox,1,4)
        add(labelForLoc, 0,5, 2,1)
        add(new Label("Localidad:"), 0,6)
        add(locBox,1,6)
        add(locExplorer,0,7,2,1)
        add(timeField,0,8)
    }

    PrefixSelectionCustomizer.customize(countryChoiceBox)

    val presenter = new DataEntryPresenter(grid,searchField,countryChoiceBox,locExplorer,place,labelForLoc)

    val dialog = new Dialog[BareData] {
        initOwner(InitApp.stage.value)
        title = "Entradas"
        width = 200
        resizable = true
    }
    val loginButtonType = new ButtonType("Aceptar", ButtonData.OKDone)
    dialog.dialogPane().buttonTypes = Seq(loginButtonType, ButtonType.Cancel)
    dialog.dialogPane().content = grid

    def onEditDataEntryDialog(stage: PrimaryStage, userExplorer: UserDataPresenter): Unit = {
        val currentUser: UserData = state.currentUserData.value
        firstName.text = currentUser.first
        lastName.text = currentUser.last
        tags.text = currentUser.tags

        val datetime: ZonedDateTime= ZonedDateTime.parse(currentUser.date)
        datePicker.value = datetime.toLocalDate
        val hfactory: jfxSpinnerValueFactory[Int] = hourPicker.valueFactory.value.asInstanceOf[jfxSpinnerValueFactory[Int]]
        hfactory.value_=( datetime.toLocalTime.getHour)
        val mfactory: jfxSpinnerValueFactory[Int] = minutePicker.valueFactory.value.asInstanceOf[jfxSpinnerValueFactory[Int]]
        mfactory.value_=(datetime.toLocalTime.getMinute)
        val sfactory: jfxSpinnerValueFactory[Int] = secondPicker.valueFactory.value.asInstanceOf[jfxSpinnerValueFactory[Int]]
        sfactory.value_=(datetime.toLocalTime.getSecond)
        place.value = AtlasQuery.getLocationFromUserData(currentUser).getOrElse(state.currentLocation.value)
        labelForLoc.text.value = place.value.toString

        dialog.resultConverter = dialogButton =>
            if (dialogButton == loginButtonType) {
                val lt = LocalTime.of(hfactory.value.value,mfactory.value.value,sfactory.value.value)
                val localDateTime = LocalDateTime.of(datePicker.value(),lt)
                val zdt = ZonedDateTime.of(localDateTime, ZoneId.of(place.value.timezone))
                BareData(firstName.text(), lastName.text(), tags.text(), zdt, place.value)
            }
            else null

        val result = dialog.showAndWait()

        result match {
            case Some(BareData(f, l, t, d, p)) =>
                val ud = UserData(f,l,t,d.toString,p.name,p.country,p.admin1,p.admin2)
                userExplorer.updateUser(ud)
            case _ => println("Dialog returned: None")
        }

    }

    def onNewDataEntryDialog(stage: PrimaryStage, userExplorer: UserDataPresenter): Unit = {
        val hfactory: jfxSpinnerValueFactory[Int] = hourPicker.valueFactory.value.asInstanceOf[jfxSpinnerValueFactory[Int]]
        val mfactory: jfxSpinnerValueFactory[Int] = minutePicker.valueFactory.value.asInstanceOf[jfxSpinnerValueFactory[Int]]
        val sfactory: jfxSpinnerValueFactory[Int] = secondPicker.valueFactory.value.asInstanceOf[jfxSpinnerValueFactory[Int]]
        firstName.text.value = "sin nombre"

        dialog.resultConverter = dialogButton =>
            if (dialogButton == loginButtonType) {
                val lt = LocalTime.of(hfactory.value.value,mfactory.value.value,sfactory.value.value)
                val localDateTime = LocalDateTime.of(datePicker.value(),lt)
                val zdt = ZonedDateTime.of(localDateTime, ZoneId.of(place.value.timezone))
                BareData(firstName.text(), lastName.text(), tags.text(), zdt, place.value)
            }
            else null

        val result = dialog.showAndWait()

        result match {
            case Some(BareData(f, l, t, d, p)) =>
                val ud = UserData(f,l,t,d.toString,p.name,p.country,p.admin1,p.admin2)
                userExplorer.insertUser(ud)
            case _ =>
                println("Dialog returned: None")
        }
    }
}
