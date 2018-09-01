package eideia.calc

import eideia.InitApp
import eideia.ephe.EpheDriver
import org.scalatest.{FunSuite, Matchers}

class HuberTest extends FunSuite with Matchers {
    test("causal char positions") {
        val chart = InitApp.setChartFromLoadData("personal", 1)
        val driver = new Huber(chart)
        val causalPos = driver.causalPlanets
        assert(causalPos.size == 11)
    }

    test("local houses and armc") {
        val chart = InitApp.setChartFromLoadData("personal", 1)
        val driver = new Huber(chart)
        val local = driver.localityHouses
        assert(local.size == 12)
        println(local)

    }

}
