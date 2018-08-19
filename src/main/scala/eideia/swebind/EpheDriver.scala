package eideia.swebind

import eideia.models.Chart

import scala.collection.mutable.ArrayBuffer

object EpheDriver {

    val swe = Swebind.run()
    swe.swe_set_ephe_path("")
    val gregflag: Int = 1

    def calcHuberPoints(ch: Chart) : ArrayBuffer[(Double,Double)] = {
        val y: Int = ch.date.getYear
        val m: Int = ch.date.getMonthValue
        val d: Int = ch.date.getDayOfMonth
        val h: Double = ch.date.getHour + ch.date.getMinute/60.0
        val jd = swe.swe_julday(y,m,d,h,gregflag)

        val SEFLG_SPEED = 256L
        val err  = new String(new Array[Char](256))
        val xx  = new Array[Double](6)
        val planets = List(0,1,2,3,4,5,6,7,8,9,11) // classic plus true node

        val bodies = ArrayBuffer[(Double,Double)]()

        planets.foreach { pl =>
            val res = swe.swe_calc_ut(jd, pl, SEFLG_SPEED, xx, err)
            assert(res > 0)
            bodies += ((xx(0),xx(3)))
        }
        bodies
    }

}
