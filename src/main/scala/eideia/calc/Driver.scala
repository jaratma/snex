package eideia.calc
import eideia.models.{Chart, PlanetData}

trait Driver {

    val chart: Chart
    val planets: Seq[PlanetData]

    def findPlanetByRef(ref: Int): Option[PlanetData] = planets.find(_.ref == ref)
}
