package eideia.controller

import scalafx.Includes._
import scalafx.scene.control.{ChoiceBox, Skin, TableView}
import eideia.{InitApp, State}
import eideia.userdata.UserDataManager
import scalafx.collections.ObservableBuffer
import eideia.models.{NexConf, Person}

class UserDataPresenter(choice: ChoiceBox[String], explorer: TableView[Person])(implicit val state: State ) {

    val config: NexConf = InitApp.config
    val rows: ObservableBuffer[Person] = rowsFromTable(config.database)

    choice.items = tableNames

    choice.value.onChange( (_,_,newval ) => {
        rows.clear()
        rows ++= rowsFromTable(newval)
        explorer.items= rows
        explorer.selectionModel().selectFirst()
    })

    explorer.items = rows
    explorer.selectionModel().selectFirst()
    explorer.applyCss()
    explorer.selectionModel().selectedItemProperty().onChange( (_,_,nval) => {
        val table = choice.selectionModel().selectedItemProperty().value
        val chart = InitApp.setChartFromLoadData(table, nval.id)
        state.currentChart.value = chart
        println(state.currentChart)
    })

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
