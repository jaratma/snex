package eideia.userdata

import java.util.concurrent.ForkJoinPool
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.languageFeature.existentials

import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.meta.MTable

import eideia.{State, DateManager => DM}
import eideia.models.UserData
import eideia.InitApp.userConfPath
import eideia.atlas.AtlasQuery
import eideia.InitApp.state


object UserDataManager {
    implicit val executor =  ExecutionContext.fromExecutor(new ForkJoinPool(20))
    implicit lazy val existentials: existentials = language.existentials


    class UserBase(name: String) {
        class UserDataTable(tag: Tag) extends Table[UserData](tag, s"$name") {
                def first = column[String]("first")
                def last = column[String]("last")
                def tags = column[String]("tags")
                def date = column[String]("date")
                def city = column[String]("city")
                def country = column[String]("country")
                def admin1 = column[String]("admin1")
                def admin2 = column[String]("admin2")
                def id = column[Long]("rowid", O.PrimaryKey, O.AutoInc)

                def * =
                    (first, last, tags, date, city, country, admin1, admin2,id).mapTo[UserData]
        }
    }

    def queryForThisTable(tableName: String)  = {
        val base = new UserBase(s"$tableName")
        TableQuery[base.UserDataTable]
    }

    val url = s"jdbc:sqlite:$userConfPath/collection.db"
    val driver = "org.sqlite.JDBC"
    val db = Database.forURL(url, driver)
    //AsyncExecutor("user", numThreads=10, queueSize=1000)

    def exec[T](program: DBIO[T]): T = Await.result(db.run(program), 2 seconds)

    def convertLegacyData(legacy: LegacyEssentialFields)(implicit state: State): UserData = {
        val first: String = legacy.first
        val last: String = legacy.last
        val date: String = legacy.date
        val zone: String = legacy.zone
        val city: String = legacy.city
        val country: String = legacy.country // country code

        val transformedDate = DM.transformString(date, zone)

        val loc = AtlasQuery.getLocationFromLegacyData(legacy) match {
            case Some(location) => location
            case _ => state.defaultLocation
        }
        UserData(first,last,tags="",transformedDate, loc.name,loc.country,loc.admin1,loc.admin2)
    }

    def doesTableExists(table: String): Boolean = {
        val tables : Future[Vector[MTable]] = db.run(MTable.getTables)
        val res = Await.result(tables,Duration.Inf)
        res.toList.map(_.name.name).contains(table)
    }

    def getTableNames: Seq[String] = {
        val tables : Future[Vector[MTable]] = db.run(MTable.getTables)
        val res = Await.result(tables,Duration.Inf)
        res.toList.map(_.name.name)
    }

    def populateUserDataWithLegacy(table: String) = {
        val legacyColl: Seq[LegacyEssentialFields] = LegacyDataManager.getEssentialFieldsFromLegacyData(table)
        val newUserData: Seq[UserData] = legacyColl.map(convertLegacyData)
        val messages = queryForThisTable(table)

        if (!doesTableExists(table)) {
            val schema = messages.schema.create
            Await.result(db.run(DBIO.seq(schema)), 2.seconds)
        } else {
            val rows = Await.result(db.run(sqlu"DELETE FROM #$table"), Duration.Inf)
        }
        val rows: Option[Int] = Await.result(db.run(messages ++= newUserData), Duration.Inf)
        println(s"Inserted ${rows.get} rows.")
        rows
    }

    def insertUserData(data: UserData, table: String): Int = {
        val messages = queryForThisTable(table)
        Await.result(db.run(messages += data), Duration.Inf)
    }

    def updateUserDate(data: UserData, table: String, id: Long): Int = {
        val messages = queryForThisTable(table).filter(_.id === id).map(m => (m.first, m.last, m.tags, m.date,m.city,m.country,m.admin1,m.admin2))
        Await.result(db.run(messages.update((data.first,data.last,data.tags,data.date,data.city,data.country,data.admin1,data.admin2))), Duration.Inf)
    }

    def loadRegisterById(table: String, id: Long) : Option[UserData] = {
        val messages = queryForThisTable(table)
        val query = messages.filter(_.id === id)
        exec(query.result.headOption)
    }

    def searchChartByName(name: String, table: String): Seq[UserData] = {
        val messages = queryForThisTable(table)
        val query = messages.filter(m  => (m.first like s"${name}%") || (m.last like s"${name}%")  )
        exec(query.result)
    }

    def getDisplayRowsFromTable(table: String): Seq[(String, String, Long)] = {
        val messages = queryForThisTable(table)
        val query = messages.sortBy(_.last.asc).map {
                r => (r.last, r.first, r.id)
        }
        exec(query.result)
    }

   def getAllRowsFromDB: Seq[UserData] = {
       val tables = getTableNames
       (for (t <- tables) yield exec(queryForThisTable(t).result)).flatten
   }
}

