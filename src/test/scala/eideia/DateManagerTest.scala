package eideia

import org.scalatest.{FunSuite, Matchers}
import java.time.{ZonedDateTime,ZoneId, LocalDate, LocalTime}


class DateManagerTest extends FunSuite with Matchers {

    test("check utc dat ") {
        val dt = DateManager
        val ld = LocalDate.of(1960,2,19)
        val lt = LocalTime.of(18,18)
        val tz = ZoneId.of("Atlantic/Canary")
        val birth: ZonedDateTime = dt.utcFromLocalDateTimeZone(ld,lt,tz)
        assert(birth.getHour == 18)
    }

    test("check cet time") {
        val dt = DateManager
        val ld = LocalDate.of(2018,8,10)
        val lt = LocalTime.of(12,0)
        val tz = ZoneId.of("Europe/Madrid")
        val now: ZonedDateTime = dt.utcFromLocalDateTimeZone(ld,lt,tz)
        assert(now.getHour == 10)
    }

    test("check here and now") {
        import ConfigManager._
        import atlas.AtlasQuery.getTimeZoneFromLocAndCountry
        val conf: NexConf = getDefaulConfig
        val timeZone: String = getTimeZoneFromLocAndCountry(conf.locality, conf.country)
        val now = DateManager.now(timeZone)
        assert(now.getZone.toString == timeZone)
    }
}
