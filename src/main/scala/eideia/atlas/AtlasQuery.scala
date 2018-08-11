package eideia.atlas

import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import eideia.models.Location

object AtlasQuery {

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
}

