package eideia.surface

import java.lang.reflect.Method

import scalafx.scene.layout.{Pane, Priority}
import eideia.InitApp.state
import eideia.calc.{Driver, Huber}
import eideia.draw.MasterDraft
import eideia.models.Chart

object CanvasSurface {

    val driver = new Huber(state.currentChart.value)

    val drawingPane = new Pane {
        prefWidth = 660
        prefHeight = 660
        minWidth = 400
        minHeight = 400
        maxWidth = 1800
        maxHeight = 1200
        style = "-fx-background-color: white;"
        vgrow = Priority.Always
    }

    def invoqueOp(op: String, chart: Chart): Unit = {
        val method: Method = MasterDraft.getClass.getMethods.find(_.getName == op).head
        method.invoke(MasterDraft,drawingPane)
    }
}
