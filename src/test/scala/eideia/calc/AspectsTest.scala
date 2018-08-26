package eideia.calc

import eideia.InitApp
import eideia.swebind.EpheDriver
import org.scalatest.{FunSuite, Matchers}

class AspectsTest extends FunSuite with Matchers{
    test("calc aspects for huber planets") {
        InitApp
        val chart = InitApp.setChartFromLoadData("personal",1)
        val points = EpheDriver.huberPoints(chart)
        val driver = new Aspects(chart)
        val aspects = driver.calculateSimpleAspectsForHuber(points)
        assert(aspects.isInstanceOf[Seq[(Seq[Int],Int,Double,Double)]])
        println(aspects)

    }


}
