package eideia.atlas

import java.util.concurrent.ForkJoinPool

import slick.jdbc.meta.MTable
import slick.jdbc.SQLiteProfile.api._
import eideia.{DateManager, State}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import eideia.models.{Admin1, Admin2, Location, UserData}
import eideia.userdata.{LegacyEssentialFields, LocationTriplet}
import eideia.InitApp. userConfPath

object AtlasQuery {
    implicit val executor =  ExecutionContext.fromExecutor(new ForkJoinPool(2))
    def defaultLocation(implicit state: State) : Location = state.defaultLocation
    lazy val messages = TableQuery[LocationTable]
    val locDb = Database.forConfig("cities")
    def exec[T](program: DBIO[T]): T = Await.result(locDb.run(program), 2 seconds)
/*
    Custom atlas db
 */
    val url = s"jdbc:sqlite:$userConfPath/customloc.db"
    val driver = "org.sqlite.JDBC"
    val customDb = Database.forURL(url, driver)

    def initCustomDB = {
        val tables : Future[Vector[MTable]] = customDb.run(MTable.getTables)
        val res = Await.result(tables,Duration.Inf)
        if (!res.toList.map(_.name.name).contains("cities")) {
            val queryCustom = TableQuery[LocationTable]
            val schema = queryCustom.schema.create
            Await.result(customDb.run(DBIO.seq(schema)), 2.seconds)
        }
    }

    def insertCustomLocation(loc: Location): Int = {
        val queryCustom = TableQuery[LocationTable]
        Await.result(customDb.run(queryCustom += loc), 2.seconds)
    }

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
        val codesFromES : Map[String,String] = CountryResolver.mapLocalizedCountryTocode()
        codesFromES.getOrElse(country,country)
    }

    def getAdmin1Code(region: String): String = {
        lazy val admins1 = TableQuery[Admin1Table]
        val query = admins1.filter(r => r.name like region).map(_.regionCode)
        exec(query.result.headOption) match {
            case Some(code) => code
            case _ => region
        }
    }

    def getAdmin1Name(country: String, code: String): String = {
        lazy val admins1 = TableQuery[Admin1Table]
        val query = admins1.filter(r => (r.country === country) && (r.regionCode === code) ).map(_.name)
        exec(query.result.headOption) match {
            case Some(name) => name
            case _ => code
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

    def getLocationFromCityAndCountryCode(city: String, code: String): Seq[AtlasQuery.LocationTable#TableElementType]= {
        val query = messages.filter(r => (r.name like s"${city}%")  && r.country === code).sortBy(r => r.name)
        Await.result(locDb.run(query.result), 5.seconds) ++ Await.result(customDb.run(query.result), 5.seconds)
    }

    def getLocationFromCityAndId(city: String, lid: Long): Location = {
        val query = messages.filter(r => r.name === city  && r.id === lid)
        (Await.result(locDb.run(query.result), 5.seconds) ++ Await.result(customDb.run(query.result), 5.seconds)).head
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
        (Await.result(customDb.run(query.result), 3.seconds) ++ Await.result(locDb.run(query.result), 5.seconds)).headOption
        //exec(query.result.headOption)
    }

    // search location in both databases
    def searchLocation(city: String): Seq[Location] = {
        println("enter search")
        val query = messages.filter(r => r.name like city)
        Await.result(locDb.run(query.result), 5.seconds) ++ Await.result(customDb.run(query.result), 3.seconds)
    }

    class LocationTable(tag: Tag)
        extends Table[Location](tag, "cities") {
        def * =
            (name, latitude, longitude, country, admin1, admin2, elevation, timezone, id).mapTo[Location]

        def name = column[String]("name")
        def latitude = column[Double]("latitude")
        def longitude = column[Double]("longitude")
        def country = column[String]("country")
        def admin1 = column[String]("admin1")
        def admin2 = column[String]("admin2")
        def elevation = column[Double]("elevation")
        def timezone = column[String]("timezone")
        def id = column[Long]("rowid", O.PrimaryKey, O.AutoInc)
    }

    final class Admin1Table(tag: Tag) extends Table[Admin1](tag, "admin1") {
        def * = (country,regionCode,name).mapTo[Admin1]
        def country = column[String]("country")
        def regionCode = column[String]("regionCode")
        def name = column[String]("name")
    }

    final class Admin2Table(tag: Tag) extends Table[Admin2](tag, "admin2") {
        def * = (country,region, subregion, name).mapTo[Admin2]
        def country = column[String]("country")
        def region = column[String]("regionCode")
        def subregion = column[String]("regionCode")
        def name = column[String]("name")
    }
}

