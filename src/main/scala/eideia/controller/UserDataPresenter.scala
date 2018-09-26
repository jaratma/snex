package eideia.controller

import scalafx.Includes._
import scalafx.scene.control._
import eideia.{InitApp, State}
import eideia.userdata.UserDataManager
import scalafx.collections.ObservableBuffer
import eideia.models.{NexConf, Person, Register, UserData}
import scalafx.collections.transformation.FilteredBuffer
import javafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType

class UserDataPresenter(choice: ChoiceBox[String],
                        explorer: TableView[Person],
                        searchField: TextField)(implicit val state: State ) {

    val config: NexConf = InitApp.config
    val rows: ObservableBuffer[Person] = rowsFromTable(config.database)



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
    explorer.selectionModel().selectedItemProperty().onChange( (_,_,nval) => {
        if (nval != null) {
            val table = choice.selectionModel().selectedItemProperty().value
            state.currentRegister.value = Register(table, nval.id)
            state.currentUserData.value = UserDataManager.loadRegisterById(table,nval.id).get
            state.infoLabels.update(state.currentUserData.value)
        }
    })


    def updateUser(ud: UserData) = {
        val table = state.currentRegister.value.table
        val uid = state.currentUserData.value.id
        val r = UserDataManager.updateUserDate(ud,table,uid)
        state.logger.info(s"updated $r register(s)")
        rows.clear()
        rows ++= rowsFromTable(state.currentDatabase.value)
        explorer.items = rows
        val p = rows.find(p => p.name.value == ud.first).head
        explorer.selectionModel().select(p)
    }

    def inserUser(ud: UserData) = {
        val table = state.currentRegister.value.table
        val r = UserDataManager.insertUserData(ud,table)
        state.logger.info(s"inserted $r register(s)")
        rows.clear()
        rows ++= rowsFromTable(state.currentDatabase.value)
        explorer.items = rows
        val p = rows.find(p => p.name.value == ud.first).head
        explorer.selectionModel().select(p)
    }

    def deleteUser(ev: ActionEvent) = {
        val tev = ev.getEventType()
        val table = choice.selectionModel().selectedItemProperty().value
        val reg: Person = explorer.selectionModel().selectedItemProperty.value
        val alert = new Alert(AlertType.Confirmation) {
            initOwner(InitApp.stage.value)
            title = "Confirmar"
            headerText = "Confirmar eliminar"
            contentText = s"¿Eliminar ${reg.name.value}"
        }
        val result = alert.showAndWait()

        result match  {
            case Some(ButtonType.OK) =>
                val r = UserDataManager.deleteUserData(reg.id, table)
                state.logger.info(s"Deleted $r ${reg.name.value}")
            case _ => state.logger.info("Delete cancelled.")
        }
    }

    def isValidUserData(ud: UserData) : Boolean = {
        ud.first != "" && ud.first.forall(c => c.isLetterOrDigit)
        ud.isInstanceOf[UserData]
    }

    def searchAction(ev: ActionEvent) = {
        val text = searchField.text.value
        if (!text.isEmpty) {
            val table = choice.selectionModel().selectedItemProperty().value
            explorer.items = new FilteredBuffer[Person](rows, (p: Person) => p.name.value.toLowerCase.contains(text.toLowerCase))
        }
    }

    def clearAction(event: ActionEvent) = {
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
            val name = if (c1.isEmpty) c2 else c1 + ", " + c2
            rows += new Person(name, ix)
        }
        rows
    }
}
