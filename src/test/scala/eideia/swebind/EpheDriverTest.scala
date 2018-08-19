package eideia.swebind

import org.scalatest.{FunSuite, Matchers}
import eideia.InitApp

import scala.collection.mutable.ArrayBuffer

class EpheDriverTest  extends FunSuite with Matchers{

    test("calc now chart") {
        val chart = InitApp.setNowChart
        val points = EpheDriver.calcHuberPoints(chart)
        assert(points.isInstanceOf[ArrayBuffer[(Double,Double)]])
    }

}
