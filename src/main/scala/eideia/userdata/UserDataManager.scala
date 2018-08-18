package eideia.userdata

import java.util.concurrent.ForkJoinPool

import eideia.{DateManager => DM}
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.{Await, ExecutionContext}
import eideia.models.{Location, UserData}

import scala.concurrent.duration._
import eideia.InitApp.userConfPath
import eideia.atlas.AtlasQuery

case class LocationNotFounException(smth:String)  extends Exception

object UserDataManager {
    implicit val executor =  ExecutionContext.fromExecutor(new ForkJoinPool(4))

    final class UserDataTable(tag: Tag)
        extends Table[UserData](tag, "colleccion") {
        def first = column[String]("first")
        def last = column[String]("last")
        def tags = column[String]("tags")
        def date = column[String]("date")
        def city = column[String]("city")
        def country = column[String]("country")
        def admin1 = column[String]("admin1")
        def admin2 = column[String]("admin2")

        def * =
            (first, last, tags, date, city, country, admin1, admin2).mapTo[UserData]
    }

    lazy val messages = TableQuery[UserDataTable]

    val url = s"jdbc:sqlite:$userConfPath:collection.db"
    val driver = "org.sqlite.JDBC"
    val db = Database.forURL(url, driver)

    def exec[T](program: DBIO[T]): T = Await.result(db.run(program), 2 seconds)

    def convertLegacyData(legacy: LegacyEssentialFields): UserData = {
        val first: String = legacy.first
        val last: String = legacy.last
        val date: String = legacy.date
        val zone: String = legacy.zone
        val city: String = legacy.city
        val country: String = legacy.country // country code

        val transformedDate = DM.transformString(date, zone)

        val loc = AtlasQuery.getLocationFromLegacyData(legacy) match {
            case Some(location) => location
            case _ => throw LocationNotFounException("Location not found")
        }
        UserData(first,last,tags="",transformedDate, loc.name,loc.country,loc.admin1,loc.admin2)
    }
}

