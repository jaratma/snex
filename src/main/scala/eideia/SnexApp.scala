package eideia

import eideia.swebind.EpheDriver

object SnexApp extends App{

    val config = InitApp.config

    def displayBodies(bodies: Seq[(Double,Double)]) = {
        bodies.foreach { b =>
            var (lng,speed) = b
            println(s"$lng\t\t$speed")
        }
    }

    //val chart = InitApp.setNowChart
    val chart = InitApp.setChartFromLoadData("personal", 1)
    val bodies = EpheDriver.calcHuberPoints(chart)
    displayBodies(bodies)


}
