package eideia.calc

import eideia.InitApp
import eideia.models.Chart
import org.scalatest.{FunSuite, Matchers}

class DirectionsTest extends FunSuite with Matchers {

    ignore("secondary progressed planets") {
        implicit val now = InitApp.state.setNowChart
        InitApp.state.setChartFromLoadData("personal",1)
        val stored = InitApp.state.currentChart()
        val points = Directions.secondaryProgressionPlanets(stored)
        assert(points.size == 11)
        println(points)
    }

    ignore("secondary progressed houses") {
        implicit val now = InitApp.state.setNowChart
        InitApp.state.setChartFromLoadData("personal",1)
        val stored = InitApp.state.currentChart()
        val houses = Directions.secondaryProgressionHouses(stored)
        assert(houses.size == 12)
        println(houses)
    }

    ignore("tertiary progressed planets") {
        implicit val now = InitApp.state.setNowChart
        InitApp.state.setChartFromLoadData("personal",1)
        val stored = InitApp.state.currentChart()
        val points = Directions.tertiaryProgressionPlanets(stored)
        assert(points.size == 11)
        println(points)
    }

    ignore("tertiary progressed houses") {
        implicit val now = InitApp.state.setNowChart
        InitApp.state.setChartFromLoadData("personal",1)
        val stored = InitApp.state.currentChart()
        val tertSun = Directions.tertiaryProgressionPlanets(stored).head.longitud
        val houses = Directions.tertiaryProgressedHouses(stored, tertSun)
        assert(houses.size == 12)
        println(houses)
    }
}
