package eideia.ephe

import org.scalatest.{FunSuite, Matchers}
import eideia.InitApp
import eideia.models.PlanetData

import scala.collection.mutable.ArrayBuffer

class EpheDriverTest  extends FunSuite with Matchers{

    test("calc now chart") {
        val chart = InitApp.state.setNowChart
        val points = EpheDriver.huberPoints(chart)
        assert(points.isInstanceOf[ArrayBuffer[PlanetData]])
        assert(points.size == 11)
    }

    test("calc all points") {
        val chart = InitApp.state.setNowChart
        val points = EpheDriver.calcAllPoints(chart)
        assert(points.isInstanceOf[Seq[PlanetData]])
        assert(points.size == 19)
    }

    test("lilith") {
        val chart = InitApp.state.setNowChart
        val points = EpheDriver.lilith(chart)
        assert(points.isInstanceOf[PlanetData])
    }

    test("vulcan") {
        val chart = InitApp.state.setNowChart
        val points = EpheDriver.vulcan(chart)
        assert(points.isInstanceOf[PlanetData])
    }
}
