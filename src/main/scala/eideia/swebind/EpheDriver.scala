package eideia.swebind

import java.time.ZonedDateTime

import eideia.InitApp
import eideia.models.{Chart, PlanetData}

import scala.collection.mutable.ArrayBuffer

object EpheDriver {

    val swe = Swebind.run()
    swe.swe_set_ephe_path("")
    val GREGFLAG: Int = 1
    val SEFLG_SPEED = 256L
    val SEFLG_EQUATORIAL = 2048L
    val SE_ECL_NUT = -1
    val HOUSESYSTEM = InitApp.config.housesystem(0).toInt // TODO: beautify to char to int
    val ARMC_INDEX = 12

    val allPoints = (0 to 20).toList :+ 55 // + Vulcan = 21

    def julDay(date: ZonedDateTime) : Double = {
        val y: Int = date.getYear
        val m: Int = date.getMonthValue
        val d: Int = date.getDayOfMonth
        val h: Double = date.getHour + date.getMinute/60.0
        swe.swe_julday(y,m,d,h,GREGFLAG)
    }

    def calcAllPoints(ch: Chart) : ArrayBuffer[PlanetData] = {
        val jd = julDay(ch.date)
        val err  = new String(new Array[Char](256))
        val xx  = new Array[Double](6)
        val bodies = ArrayBuffer[PlanetData]()

        allPoints.foreach { pl =>
            val res = swe.swe_calc_ut(jd, pl, SEFLG_SPEED, xx, err)
            try {
                assert(res > 0)
            } catch {
                case e: AssertionError => println(err)
            }
            val lng = xx(0)
            val speed =  xx(3)
            val res1 = swe.swe_calc_ut(jd, pl, SEFLG_SPEED | SEFLG_EQUATORIAL, xx, err)
            val decl = xx(1)
            bodies += PlanetData(pl, lng, decl, speed)
        }
        bodies.filter(pl => !List(10,13,14).contains(pl.ref))
    }

    def huberPoints(ch: Chart) : ArrayBuffer[PlanetData] = {
        val allPoints: ArrayBuffer[PlanetData]  = calcAllPoints(ch)
        val huber = List(0,1,2,3,4,5,6,7,8,9,11) // classic plus true node
        allPoints.filter(pl => huber.contains(pl.ref))
    }


    def asteroidPoints(ch: Chart) : ArrayBuffer[PlanetData] = {
        val allPoints: ArrayBuffer[PlanetData]  = calcAllPoints(ch)
        val asteroids = List(15,16,17,18,19,20) // chiron et al.
        allPoints.filter(pl => asteroids.contains(pl.ref))
    }

    def lilith(ch: Chart): PlanetData = calcAllPoints(ch).filter(p => p.ref == 12).head

    def vulcan(ch: Chart): PlanetData = calcAllPoints(ch).filter(p => p.ref == 55).head

    def calcHouses(ch: Chart): Seq[Double] = {
        val jd = julDay(ch.date)
        val cusps  = new Array[Double](13)
        val ascmc  = new Array[Double](10)

        val res = swe.swe_houses(jd, ch.lat, ch.lng, HOUSESYSTEM, cusps, ascmc)
        cusps.drop(1) :+ ascmc(3)
    }

    def calcLocalityHouses(ch: Chart): Seq[Double] = {
        val jd = julDay(ch.date)
        val armc = (ch.lng + 360) % 360
        val lat = ch.lat
        val cusps  = new Array[Double](13)
        val ascmc  = new Array[Double](10)
        val err  = new String(new Array[Char](256))
        val xx  = new Array[Double](6)

        swe.swe_calc_ut(jd, SE_ECL_NUT, SEFLG_SPEED, xx, err)
        val eps = xx(0)

        swe.swe_houses_armc(armc, lat, eps, HOUSESYSTEM, cusps, ascmc)
        cusps.drop(1)
    }
}
