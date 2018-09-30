package eideia

import eideia.atlas.{AtlasQuery, QueryGeonames => QG}
import eideia.calc.Huber
import eideia.InitApp.state
import eideia.models.Register

object SnexApp { //} extends App{

    val config = InitApp.config

    def displayBodies(bodies: Seq[(Double,Double)]) = {
        bodies.foreach { b =>
            var (lng,speed) = b
            println(s"$lng\t\t$speed")
        }
    }

    def displayChart(implicit state: State) = {
        //val chart = InitApp.setNowChart
        //val chart = InitApp.state.setChartFromLoadData("personal", 1)
        state.currentRegister() =  Register("personal", 1L)
        val driver = new Huber(state.currentChart.value)
        val planets = driver.planets
        planets.foreach(println)
        val houses = driver.houses
        houses.foreach (println )
        println(driver.vertex)
        //val causalPos = driver.causalPlanets
        //println("Causal:")
        //causalPos.foreach(println)
    }

    def displaySearchLocation(place: String, country: String) = {
        val locs =  QG.parseQuery(QG.sendQuery(place,country).right.get)
        println(locs(0))
    }
    //displaySearchLocation("Gurtnellen", "CH")

    //val locs = AtlasQuery.searchLocation("Gurtnellen")
    //println(locs.head.name)
    displayChart

}
