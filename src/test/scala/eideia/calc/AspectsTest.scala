package eideia.calc

import eideia.InitApp
import eideia.ephe.EpheDriver
import org.scalatest.{FunSuite, Matchers}

class AspectsTest extends FunSuite with Matchers{
    ignore("calc aspects for huber planets") {
        InitApp
        InitApp.state.setChartFromLoadData("personal",1)
        val chart = InitApp.state.currentChart()
        val points = EpheDriver.huberPoints(chart)
        val driver = new Aspects(chart)
        val aspects = driver.calculateSimpleAspectsForHuber(points)
        assert(aspects.isInstanceOf[Seq[(Seq[Int],Int,Double,Double)]])
        println(aspects)

    }


}
