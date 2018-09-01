package eideia

import java.time.ZonedDateTime

import scalafx.beans.property.{ObjectProperty, StringProperty}

package object models {

    case class NexConf(lang: String, locality: String, country: String, region: String, database: String, housesystem: String)

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

    case class PlanetData(ref: Int, longitud: Double, declination: Double, speed: Double)

    class Person(name_ : String, val id : Long) {
        val name = new StringProperty(this, "name", name_)
        override def toString: String = s"Person: ${name.value}, ${id}"
    }
}
