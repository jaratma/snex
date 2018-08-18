package eideia

import java.time.{ZonedDateTime,ZoneId, LocalDate, LocalTime, LocalDateTime}

object DateManager {

    def now(tz: String): ZonedDateTime = ZonedDateTime.now(ZoneId.of(tz))

    def utcFromLocalDatePlusTimePlusZone(date: LocalDate, time: LocalTime, zone: ZoneId): ZonedDateTime = {
        val ld = ZonedDateTime.of(date,time,zone)
        ld.withZoneSameInstant(ZoneId.of("UTC"))
    }

    def utcFromJustLocalDateTimePlusZone(dt: LocalDateTime, zone: ZoneId): ZonedDateTime = {
        val ld = ZonedDateTime.of(dt,zone)
        ld.withZoneSameInstant(ZoneId.of("UTC"))
    }

    def localDateTimeFromLegacyStringDate(date: String) : LocalDateTime = {
        val (dt,tx) = date.split('T') match { case Array(a,b) => (a,b) }
        val tm = tx.split(Array[Char]('+','-'))(0)
        val (y,m,d) = dt.split('-') match { case Array(a,b,c) => (a.toInt,b.toInt,c.toInt) }
        val (h,mn,s) = tm.split(':') match { case Array(a,b,c) => (a.toInt,b.toInt,c.toInt) }
        LocalDateTime.of(y,m,d,h,mn,s)
    }

    def zonedLocalDateTime(dt: LocalDateTime, zone: String) = ZonedDateTime.of(dt,ZoneId.of(zone))


    def parseDateString(date: String): ZonedDateTime = ZonedDateTime.parse(date)

    def transformString(date: String, zone: String): String = {
        val dt = localDateTimeFromLegacyStringDate(date)
        zonedLocalDateTime(dt, zone).toString
    }
}
