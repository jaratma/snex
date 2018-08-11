package eideia

import java.time.{ZonedDateTime,ZoneId, LocalDate, LocalTime}

object DateManager {

    def utcFromLocalDateTimeZone(date: LocalDate, time: LocalTime, zone: ZoneId): ZonedDateTime = {
        val ld = ZonedDateTime.of(date,time,zone)
        ld.withZoneSameInstant(ZoneId.of("UTC"))
    }

    def now(tz: String): ZonedDateTime = ZonedDateTime.now(ZoneId.of(tz))

}
