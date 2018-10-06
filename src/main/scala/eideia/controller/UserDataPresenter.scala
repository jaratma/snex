package eideia.controller

import scalafx.Includes._
import scalafx.scene.control._
import eideia.{InitApp, State}
import eideia.userdata.UserDataManager
import scalafx.collections.ObservableBuffer
import eideia.models.{NexConf, Person, Register, UserData}
import scalafx.collections.transformation.FilteredBuffer
import javafx.event.ActionEvent
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.GridPane


class UserDataPresenter(choice: ChoiceBox[String],
                        explorer: TableView[Person],
                        searchField: TextField,
                        deleter: Button )(implicit val state: State) {

    val config: NexConf = InitApp.config
    val rows: ObservableBuffer[Person] = rowsFromTable(config.database)
    case class Result(oldname: String, newname: String)

    choice.items = tableNames

    choice.value.onChange( (_,_,newval ) => {
        rows.clear()
        rows ++= rowsFromTable(newval)
        explorer.items = new FilteredBuffer[Person](rows)
        state.currentDatabase.value = newval
    })

    explorer.items = rows
    explorer.selectionModel().selectFirst()
    explorer.applyCss()
    //explorer.selectionModel().selectedItemProperty().onChange( (_,_,nval) => {
    //    if (nval != null) {
    //            //val table = choice.selectionModel().selectedItemProperty().value
    //            //state.currentRegister.value = Register(table, nval.id)
    //            //state.currentUserData.value = UserDataManager.loadRegisterById(table, nval.id).get
    //            //state.infoLabels.update(state.currentUserData.value)
    //    }
    //})

    explorer.setRowFactory( tv => {
        val row = new TableRow[Person]
        row.onMouseClicked = ev => {
            if (ev.clickCount == 2) {
                val table = choice.selectionModel().selectedItemProperty().value
                state.currentRegister.value = Register(table, row.item.value.id)
                state.currentUserData.value = UserDataManager.loadRegisterById(table, row.item.value.id).get
                state.infoLabels.update(state.currentUserData.value)
            }
        }
        row
    })

    deleter.onAction = handle {
        deleteUser(state.currentUserData.value.id)
    }

    def refreshExplorer(table: String = state.currentDatabase.value): Unit = {
        rows.clear()
        rows ++= rowsFromTable(table)
        explorer.items = new FilteredBuffer[Person](rows)
    }

    def updateUser(ud: UserData): Unit = {
        val table = state.currentRegister.value.table
        val uid = state.currentUserData.value.id
        val r = UserDataManager.updateUserDate(ud,table,uid)
        state.logger.info(s"updated $r register(s)")
        refreshExplorer(table)
        val last = if (ud.last.isEmpty) "" else s" ${ud.last }"
        val name = s"${ud.first}$last"
        val p = rows.find(p => p.name.value == name).head
        explorer.selectionModel().select(p)
    }

    def insertUser(ud: UserData) :Unit  = {
        val table = state.currentRegister.value.table
        val r = UserDataManager.insertUserData(ud,table)
        state.logger.info(s"inserted $r register(s)")
        refreshExplorer(table)
        val p = rows.find(p => p.name.value == ud.first).head
        explorer.selectionModel().select(p)
    }

    def deleteUser(uid: Long): Unit = {
        val its = explorer.selectionModel().selectedItems
        val table = choice.selectionModel().selectedItemProperty().value
        val reg: Person = explorer.selectionModel().selectedItemProperty.value
        val alert = new Alert(AlertType.Confirmation) {
            initOwner(InitApp.stage.value)
            title = "Confirmar"
            headerText = "Confirmar eliminar"
            contentText = s"¿Eliminar estos registros ${reg.name.value}..."
        }
        val result = alert.showAndWait()

        result match  {
            case Some(ButtonType.OK) =>
                val r = UserDataManager.deleteUserData(reg.id, table)
                state.logger.info(s"Deleted $r ${reg.name.value}")
                refreshExplorer()
            case _ => state.logger.info("Delete cancelled.")
        }
    }

    def isValidUserData(ud: UserData): Boolean = {
        ud.first != "" && ud.first.forall(c => c.isLetterOrDigit)
        ud.isInstanceOf[UserData]
    }

    def searchAction(ev: ActionEvent): Unit = {
        val text = searchField.text.value
        if (!text.isEmpty) {
            val table = choice.selectionModel().selectedItemProperty().value
            explorer.items = new FilteredBuffer[Person](rows, (p: Person) => p.name.value.toLowerCase.contains(text.toLowerCase))
        }
    }

    def clearAction(event: ActionEvent): Unit = {
        searchField.text.value = ""
        explorer.items = rows
    }

    def tableNames: ObservableBuffer[String] = {
        val names : Seq[String] = UserDataManager.getTableNames
        ObservableBuffer(names)
    }

    def rowsFromTable(table: String) : ObservableBuffer[Person] = {
        val rows = new ObservableBuffer[Person]()
        val columns: Seq[(String, String, Long)] = UserDataManager.getDisplayRowsFromTable(table)
        for ((c1,c2,ix) <- columns) {
            val surname = if (c2.isEmpty) "" else s" $c2"
            val name = c1 + surname
            rows += new Person(name, ix)
        }
        rows
    }

    def createTable() = {
        val result = showCreateDialog(InitApp.stage.value)
        result match {
            case table if table.matches("[a-zA-Z][_a-zA-Z0-9]{2,25}") =>
                UserDataManager.createNewCollection(table)
                choice.items.value += table
            case _ =>
        }
    }

    def copyTable(): Unit = {
        val result = showRenameDialog(InitApp.stage.value)
        val r = result match {
            case (old,nval) if nval.matches("[a-zA-Z][_a-zA-Z0-9]{2,25}") =>
                UserDataManager.copyCollection(old,nval)
                //choice.items.value += table
            case _ =>
        }
    }

    def moveRegister() = {
        val reg = explorer.selectionModel().selectedItemProperty().value
        val source = choice.selectionModel().selectedItemProperty().value
        val destiny = onMoveDialog(InitApp.stage.value)
        if (!destiny.isEmpty) {
            UserDataManager.moveRegister(reg,source,destiny)
        }
    }

    def deleteTable() = {
        val table: String = showDeleteDialog(InitApp.stage.value)
        UserDataManager.dropCollection(table)
        choice.items.value -= table
    }

    def showDeleteDialog(stage: PrimaryStage): String = {
        val tables = tableNames
        tables -= config.database
        val dialog = new ChoiceDialog[String](tables.head,tables) {
            initOwner(stage)
            title = s"Eliminar tabla"
            contentText = "Nombre de la tabla:"
        }
        val result = dialog.showAndWait()
        result match {
            case Some(name) => name
            case None       => ""
        }
    }

    def showCreateDialog(stage: PrimaryStage): String  = {
        val dialog = new TextInputDialog() {
            initOwner(stage)
            title = s"Crear tabla"
            contentText = "Nombre de la tabla:"
        }
        val result = dialog.showAndWait()
        result match {
            case Some(name) => name
            case None       => ""
        }
    }

    def showRenameDialog(stage: PrimaryStage) = {
        val dialog = new Dialog[Result]() {
            initOwner(stage)
            title = "Copiar colección"
        }
        val loginButtonType = new ButtonType("Aceptar", ButtonData.OKDone)
        dialog.dialogPane().buttonTypes = Seq(loginButtonType, ButtonType.Cancel)

        val newName = new TextField

        val tables = tableNames
        tables -= config.database
        val oldChoice = new ChoiceBox[String](tables) {
            selectionModel().selectFirst()
        }

        val grid = new GridPane {
            hgap = 10
            vgap = 10
            padding = Insets(20, 100, 10, 10)
            add(new Label("Copiar datos"), 0, 0)
            add(oldChoice, 1, 0)
            add(new Label("A (crea o vacía destino)"), 0, 1)
            add(newName, 1, 1)
        }

        dialog.dialogPane().content = grid

        dialog.resultConverter = dialogButton =>
            if (dialogButton == loginButtonType)
                Result(oldChoice.selectionModel().selectedItemProperty().value, newName.text())
            else
                null

        val result = dialog.showAndWait()

        result match {
            case Some(Result(old,nval)) => (old,nval)
            case _ => ("","")
        }
    }

    def onMoveDialog(stage: PrimaryStage): String = {
        val tables = tableNames
        tables -= choice.selectionModel().selectedItemProperty().value
        val dialog = new ChoiceDialog[String](tables.head,tables) {
            initOwner(stage)
            title = s"Mover a"
            contentText = "Nombre de la tabla:"
        }
        val result = dialog.showAndWait()
        result match {
            case Some(name) => name
            case None       => ""
        }
    }

}
