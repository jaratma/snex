package eideia.swebind

import org.scalatest.{FunSuite, Matchers}

class SwebindTest extends FunSuite with Matchers {

    test("version") {
        val cn  = new String(new Array[Char](256))
        val swe = Swebind.run()
        val ver = swe.swe_version(cn)
        assert(ver == "2.07.01")
    }

    test("path") {
        pending
        val swe = Swebind.run()
        swe.swe_set_ephe_path("")
        //assert("/Users/jose/share/ephe")
    }

    test("close and path") {
        pending
        val swe = Swebind.run()
        swe.swe_set_ephe_path("")
        val cn  = new String(new Array[Char](256))
        val path = swe.swe_get_library_path(cn)
        swe.swe_close()
        val currentDirectory = new java.io.File(".").getCanonicalPath
        assert(path == currentDirectory + "/lib_extra/libswe.dylib")
    }

    test("julday") {
        val flag = 1
        val swe = Swebind.run()
        swe.swe_set_ephe_path("")
        val jd = swe.swe_julday(1960,2,19,18.3,flag)
        assert(jd ==2436984.2625)
    }

    test("calc sun") {
        val flag = 1
        val SEFLG_SPEED = 256L
        val swe = Swebind.run()
        swe.swe_set_ephe_path("")
        val jd = swe.swe_julday(1960,2,19,18.3,flag)
        val err  = new String(new Array[Char](256))
        val xx  = new Array[Double](6)
        val res = swe.swe_calc_ut(jd, 0, SEFLG_SPEED, xx, err)
        assert(res > 0)
        xx(0) should be > 330.0
    }

    test("houses") {
        val flag = 1
        val swe = Swebind.run()
        swe.swe_set_ephe_path("")
        val jd = swe.swe_julday(1960,2,19,18.3,flag)
        val cusps  = new Array[Double](13)
        val ascmc  = new Array[Double](10)
        val res = swe.swe_houses(jd, 28.11666667, -15.38, 'K'.toInt, cusps, ascmc)
        ascmc(0) should be > 143.0
    }

    test("now sun") {
        import java.time.{ZonedDateTime,ZoneId}
        val now = ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
        val ut = now.withZoneSameInstant(ZoneId.of("UTC"))
        val (y,m,d) = (ut.toLocalDate.getYear, ut.toLocalDate.getMonthValue, ut.toLocalDate.getDayOfMonth)
        val tm = ut.toLocalTime.getHour + ut.toLocalTime.getMinute/60.0
        val flag = 1
        val SEFLG_SPEED = 256L
        val swe = Swebind.run()
        swe.swe_set_ephe_path("")
        val jd = swe.swe_julday(y,m,d,tm,flag)
        val err  = new String(new Array[Char](256))
        val xx  = new Array[Double](6)
        val res = swe.swe_calc_ut(jd, 0, SEFLG_SPEED, xx, err)
        assert(res > 0)
    }
}
