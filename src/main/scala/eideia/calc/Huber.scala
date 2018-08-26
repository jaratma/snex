package eideia.calc

import eideia.models.{Chart, PlanetData}
import eideia.swebind.EpheDriver

class Huber(val chart: Chart) extends Driver {
    val planets: Seq[PlanetData] = EpheDriver.huberPoints(chart)
    val allPoints: Seq[Double] = EpheDriver.calcHouses(chart)
    val houses: Seq[Double] = allPoints.init
    val vertex: Double = allPoints.last

    def houseSizes: Seq[Double] =
        List(for((h1,h2) <- houses.take(6) zip houses.slice(1,7)) yield ((h2-h1) + 360) % 360).flatMap(l => l ++ l)

    def localityHouses: Seq[Double] = EpheDriver.calcLocalityHouses(chart)

    def causalPlanets: Seq[Double] =
        for (p <- planets; s = (p.longitud/30).toInt) yield (houses(s) + (p.longitud - s * 30) * houseSizes(s)/30) % 360

}
