package eideia

import org.scalatest.{FunSuite, Matchers}
import java.time._

import eideia.userdata.LegacyDataManager


class DateManagerTest extends FunSuite with Matchers {

    test("check utc dat ") {
        val ld = LocalDate.of(1960,2,19)
        val lt = LocalTime.of(18,18)
        val tz = ZoneId.of("Atlantic/Canary")
        val birth: ZonedDateTime = DateManager.utcFromLocalDatePlusTimePlusZone(ld,lt,tz)
        assert(birth.getHour == 18)
    }

    test("check cet time") {
        val ld = LocalDate.of(2018,8,10)
        val lt = LocalTime.of(12,0)
        val tz = ZoneId.of("Europe/Madrid")
        val now: ZonedDateTime = DateManager.utcFromLocalDatePlusTimePlusZone(ld,lt,tz)
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

    test("parse string date as a LocalDateTime") {
        val datesAsStrings = LegacyDataManager.getListOfDates("personal")
        val takeFirst = datesAsStrings.head
        val ldt: LocalDateTime = DateManager.localDateTimeFromLegacyStringDate(takeFirst)
        assert(ldt.isInstanceOf[LocalDateTime])
    }

    test("utc from LocalDateTime") {
        val (date, zone) = LegacyDataManager.getFirstPairDateZone("personal")
        val ldt: LocalDateTime = DateManager.localDateTimeFromLegacyStringDate(date)
        val zoneddt = ZonedDateTime.of(ldt,ZoneId.of(zone))
        val fetchedDate = DateManager.utcFromJustLocalDateTimePlusZone(ldt, ZoneId.of(zone))
        println(s"${fetchedDate.toString}  ${zoneddt.toString}")
        assert(zoneddt.toLocalDateTime == fetchedDate.toLocalDateTime)
    }

    test("parse date string right") {
        val zdt = ZonedDateTime.of(2018,8,17,18,22,0,0,ZoneId.of("Europe/Madrid"))
        val dateString = zdt.toString
        assert(DateManager.parseDateString(dateString) == zdt)
    }
}
