package eideia

import eideia.ephe.EpheDriver
import eideia.atlas.{AtlasQuery, QueryGeonames => QG}
import eideia.calc.Huber

object SnexApp { //} extends App{

    val config = InitApp.config

    def displayBodies(bodies: Seq[(Double,Double)]) = {
        bodies.foreach { b =>
            var (lng,speed) = b
            println(s"$lng\t\t$speed")
        }
    }

    def displayChart = {
        //val chart = InitApp.setNowChart
        val chart = InitApp.setChartFromLoadData("personal", 1)
        val driver = new Huber(chart)
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
        val locs =  QG.parseQuery(QG.sendQuery(place,country))
        println(locs(0))
    }
    //displaySearchLocation("Gurtnellen", "CH")

    //val locs = AtlasQuery.searchLocation("Gurtnellen")
    //println(locs.head.name)
    displayChart

}
