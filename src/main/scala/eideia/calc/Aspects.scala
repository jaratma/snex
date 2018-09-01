package eideia.calc

import eideia.models.{Chart, PlanetData}
import eideia.ephe.EpheDriver

class Aspects(val ch: Chart) {

    type AspectTable = Seq[(Seq[Int],Int,Double,Double)]

    val angles: Seq[Double] = List(
        0, 30, 36, 40, 45, 51.438,
        60, 72, 80, 90, 102.857,
        108, 120, 135, 144, 150,
        154.28, 160, 180)

    val huberAngles: Seq[Int] = List(0, 30, 60, 90, 120, 150, 180)
    val octilesAngles: Seq[Int] = List(0, 45, 90, 135, 180)
    val decilesAngles: Seq[Int] = List(0, 36, 72, 108, 144)
    val septilesAngles: Seq[Int] = List(0, 51, 102, 154)
    val nonilesAngles: Seq[Int] = List(0, 40, 80, 120, 160)


    def calculateSimpleAspectsForHuber(plan: Seq[PlanetData]) : AspectTable = {
        val planets = EpheDriver.huberPoints(ch)
        val comb: Seq[Seq[Double]] = planets.combinations(2).toSeq.map(s => Seq(s.head.longitud,s.last.longitud))
        val refs: Seq[Seq[Int]] = planets.combinations(2).toSeq.map(s => Seq(s.head.ref,s.last.ref))
        val distances: Seq[Double] = comb.map(_.reduce(_ - _)).map(math.abs)
        val normals: Seq[Double] = for (d <- distances) yield angles.minBy(v => math.abs(v - d))
        for { (r,d,n) <- (refs,distances,normals).zipped.toList
              offset = math.abs(d - n)
              orb1: Double = Orbs.orbs(r.head)(n.toInt)
              orb2: Double = if (r.last == 11) orb1 else Orbs.orbs(r.last)(n.toInt)
              if offset < orb1|| offset < orb2
        } yield (r, n.toInt, offset/orb1, offset/orb2)
    }

    def filterHuberAngles(table: AspectTable): AspectTable = table.filter(n => huberAngles.contains(n._2))

}
