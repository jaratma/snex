package eideia.models

import java.time.ZonedDateTime

case class UserData(first:String,
                    last:String,
                    tags: String,
                    date: String,
                    city: String,
                    country: String,
                    admin1: String,
                    admin2: String,
                    id: Long = 0L)


case class Location(
        name: String,
        latitude: Double,
        longitude: Double,
        country: String,
        admin1: String,
        admin2: String,
        elevation: Double,
        timezone: String,
        id: Long = 0L)

case class Admin1(country: String, regionCode: String, name: String)

case class Admin2(country: String, region: String, subRegion: String, name: String)

case class Chart(date: ZonedDateTime, lat: Double, lng: Double)


