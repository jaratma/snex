package eideia

import java.time.ZonedDateTime

import eideia.atlas.AtlasQuery
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

    case class Register(table: String, rid: Long)

    case class InfoLabels(var userData: UserData, localnow: String) {
        val firstNameLabel = new StringProperty(this, "fist-name-label", userData.first)
        val lastNameLabel = new StringProperty(this, "last-name-label", userData.last)
        private val location = AtlasQuery.getLocationFromUserData(userData).get
        val geoLabel = new StringProperty(this, "geoLabel", Utils.formatGeoData(location))
        val dateLabel = new StringProperty(this, "date-label", Utils.formatDateString(localnow))

        def update(data: UserData) = {
            userData = data
            firstNameLabel.value = data.first
            lastNameLabel.value = data.last
            val location = AtlasQuery.getLocationFromUserData(data).get
            geoLabel.value = Utils.formatGeoData(location)
            dateLabel.value = Utils.formatDateString(data.date)
        }
    }
}
