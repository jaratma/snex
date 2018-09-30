package eideia.controller

import eideia.InitApp.state.logger
import eideia.userdata.{LegacyDataManager, LegacyEssentialFields}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.ChoiceBox

class LegacyConverterPresenter(choice: ChoiceBox[String]) {

    choice.items = tableNames
    choice.selectionModel().selectFirst()

    def tableNames: ObservableBuffer[String] = {
        val names : Seq[String] = LegacyDataManager.getTableNamesFromDb
        ObservableBuffer(names)
    }

    def convert(source: String, destiny: String) = {
        val legacyData: Seq[LegacyEssentialFields] = LegacyDataManager.getEssentialFieldsFromLegacyData(source)
        println(legacyData)
    }
}

