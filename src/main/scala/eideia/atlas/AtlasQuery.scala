package eideia.atlas

import java.util.concurrent.ForkJoinPool

import eideia.DateManager
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import eideia.models.{Admin1, Admin2, Location, UserData}
import eideia.userdata.{LegacyEssentialFields, LocationTriplet}

object AtlasQuery {
    implicit val executor =  ExecutionContext.fromExecutor(new ForkJoinPool(2))

    final class LocationTable(tag: Tag)
        extends Table[Location](tag,"cities") {
        def name = column[String]("name")
        def latitude = column[Double]("latitude")
        def longitude = column[Double]("longitude")
        def country = column[String]("country")
        def admin1 = column[String]("admin1")
        def admin2 = column[String]("admin2")
        def elevation = column[Double]("elevation")
        def timezone = column[String]("timezone")
        def id = column[Long]("rowid", O.PrimaryKey, O.AutoInc)

        def * =
            (name, latitude, longitude, country, admin1, admin2, elevation, timezone, id).mapTo[Location]
    }

    final class Admin1Table(tag: Tag) extends Table[Admin1](tag, "admin1") {
        def country = column[String]("country")
        def regionCode = column[String]("regionCode")
        def name = column[String]("name")

        def * = (country,regionCode,name).mapTo[Admin1]
    }

    final class Admin2Table(tag: Tag) extends Table[Admin2](tag, "admin2") {

        def country = column[String]("country")
        def region = column[String]("regionCode")
        def subregion = column[String]("regionCode")
        def name = column[String]("name")

        def * = (country,region, subregion, name).mapTo[Admin2]
    }

    lazy val messages = TableQuery[LocationTable]

    val db = Database.forConfig("cities")

    def exec[T](program: DBIO[T]): T = Await.result(db.run(program), 2 seconds)

    def queryTimezone(timezone: String) : Seq[AtlasQuery.LocationTable#TableElementType] = {
        val query = messages.filter(_.timezone === timezone)
        exec(query.result)
    }

    def getTimeZoneFromLocAndCountry(loc: String, country: String): String = {
        val query = messages.filter(r => r.name === loc  && r.country === country).map(_.timezone)
        exec(query.result.headOption.map(_.get))
    }

    def getAdmin1CodeFromTriplet(triplet: LocationTriplet): String = {
        val region = triplet.region
        lazy val admins1 = TableQuery[Admin1Table]
        val query = admins1.filter(r => r.name like region).map(_.regionCode)
        exec(query.result.headOption) match {
            case Some(code) => code
            case _ => region
        }
    }

    def getCountryCodeFromTriplet(triplet: LocationTriplet) : String = {
        def check(country: String) : String = country match {
            case  "USA" => "Estados Unidos"
            case "Gran Bretaña" => "Reino Unido"
            case s: String  => s
        }
        val country = triplet.country
        val codesFromES : Map[String,String] = CountryResolver.mapLocalizedCountryTocode()

        codesFromES.getOrElse(check(country),country)
    }

    def getCountryCode(country: String): String = {
        def check(country: String) : String = country match {
            case  "USA" => "Estados Unidos"
            case "Gran Bretaña" => "Reino Unido"
            case s: String  => s
        }
        val codesFromES : Map[String,String] = CountryResolver.mapLocalizedCountryTocode()
        codesFromES.getOrElse(check(country),country)
    }

    def getAdmin1Code(region: String): String = {
        lazy val admins1 = TableQuery[Admin1Table]
        val query = admins1.filter(r => r.name like region).map(_.regionCode)
        exec(query.result.headOption) match {
            case Some(code) => code
            case _ => region
        }
    }

    def getLocationFromLegacyTriplet(tpl: LocationTriplet): Option[Location] = {
        val query = messages.filter(r => (r.name like tpl.city)  && r.country === tpl.country && r.admin1 === tpl.region)
        exec(query.result.headOption)
    }

    def getLocationFromLegacyDoublet(tpl: LocationTriplet): Option[Location] = {
        val query = messages.filter(r => (r.name like tpl.city)  && r.country === tpl.country)
        exec(query.result.headOption)
    }

    def getLocationFromLegacyData(legacy: LegacyEssentialFields): Option[Location] = {
        //LegacyEssentialFields(first, last, date, zone, city, country)
        val (zone,city,country) = legacy match { case LegacyEssentialFields(_, _, _, zone, city, country) => (zone,city,country) }
        val query = messages.filter(r => (r.name like city)  && r.country === country && r.timezone ===  zone)
        exec(query.result.headOption)
    }

    def getLocationFromUserData(userdata: UserData): Option[Location] = {
        //UserData(first, last, tags, date, city, country, admin1, admin2, id)
        val (date,city,country) = userdata match { case UserData(_, _, _, date,city,country,_,_,_) => (date,city,country) }
        val zone = DateManager.parseDateString(date).getZone.toString
        val query = messages.filter(r => (r.name like city)  && r.country === country && r.timezone ===  zone)
        exec(query.result.headOption)
    }
}

