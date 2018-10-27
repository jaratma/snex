package eideia.userdata

import java.util.concurrent.ForkJoinPool

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.languageFeature.existentials
import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.meta.MTable
import eideia.{InitApp, State, DateManager => DM}
import eideia.models.UserData
import eideia.models.Person
import eideia.atlas.AtlasQuery
import eideia.InitApp.state
import eideia.InitApp.logger
import scala.util.{Failure, Properties, Success, Try}


object UserDataManager {
    implicit val executor =  ExecutionContext.fromExecutor(new ForkJoinPool(20))
    implicit lazy val existentials: existentials = language.existentials

    Class.forName("org.sqlite.JDBC")
    val url = s"jdbc:sqlite:${Properties.userHome}/.nex2/collection.db"
    val driver = "org.sqlite.JDBC"
    val colDB = Database.forURL(url, driver)

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

                def ixname = index(s"${name}ix",(first,last), unique=true)

                def * =
                    (first, last, tags, date, city, country, admin1, admin2,id).mapTo[UserData]
        }
    }

    def queryForThisTable(tableName: String)  = {
        val base = new UserBase(s"$tableName")
        TableQuery[base.UserDataTable]
    }

    def checkCollectionDB: Boolean = {
        val tables : Future[Vector[MTable]] = colDB.run(MTable.getTables)
        val res = Await.result(tables,Duration.Inf)
        val defaultDb = InitApp.defaultDatabase
        res.toList.map(_.name.name).contains(defaultDb)
    }

    def initCollectionDB(defaultDb: String = InitApp.defaultDatabase) = {
        if (!doesTableExists(defaultDb)) {
            val queryCustom = queryForThisTable(defaultDb)
            val schema = queryCustom.schema.create
            Await.result(colDB.run(DBIO.seq(schema)), 2.seconds)
            logger.info(s"Created $defaultDb collection.")
        } else
            logger.info(s"$defaultDb collection exists.")
    }

    def createNewCollection(table: String) = initCollectionDB(table)

    def dropCollection(table: String) = {
        val queryCustom = queryForThisTable(table)
        val schema = queryCustom.schema.drop
        Await.result(colDB.run(DBIO.seq(schema)), 2.seconds)
        logger.info(s"Deleted $table collection.")
    }

    def copyCollection(source: String, destiny :String) = {
        val sourceQuery = queryForThisTable(source)
        val destQuery = queryForThisTable(destiny)
        if (!doesTableExists(destiny)) {
            val schema = destQuery.schema.create
            Await.result(colDB.run(DBIO.seq(schema)), 2.seconds)
        } else {
            val rows = Await.result(colDB.run(sqlu"DELETE FROM #$destiny"), Duration.Inf)
        }
        val col: Seq[UserData] = Await.result(colDB.run(sourceQuery.result), 2.seconds)
        Await.result(colDB.run(destQuery ++= col), 2.seconds)
        logger.info(s"Data copied from $source to $destiny.")
    }

    def exec[T](program: DBIO[T]): T = Await.result(colDB.run(program), 2 seconds)

    def convertLegacyData(legacy: LegacyEssentialFields)(implicit state: State): UserData = {
        val first: String = legacy.first
        val last: String = legacy.last
        val date: String = legacy.date
        val zone: String = legacy.zone
        val city: String = legacy.city
        val country: String = legacy.country // country code

        val transformedDate = DM.transformString(date, zone)

        AtlasQuery.getLocationFromLegacyData(legacy) match {
            case Some(loc) =>
                UserData(first,last,tags="",transformedDate, loc.name,loc.country,loc.admin1,loc.admin2)
            case None => throw new Exception
        }
    }

    def doesTableExists(table: String): Boolean = {
        val tables : Future[Vector[MTable]] = colDB.run(MTable.getTables)
        val res = Await.result(tables,Duration.Inf)
        res.toList.map(_.name.name).contains(table)
    }

    def getTableNames: Seq[String] = {
        val tables : Future[Vector[MTable]] = colDB.run(MTable.getTables)
        val res = Await.result(tables,Duration.Inf)
        res.toList.map(_.name.name)
    }

    def populateUserDataWithLegacy(table: String): Option[Int] = {
        val legacyColl: Seq[LegacyEssentialFields] = LegacyDataManager.getEssentialFieldsFromLegacyData(table)
        val newUserData: Seq[UserData] = legacyColl.map(convertLegacyData)
        val messages = queryForThisTable(table)

        if (!doesTableExists(table)) {
            val schema = messages.schema.create
            Await.result(colDB.run(DBIO.seq(schema)), 2.seconds)
        } else {
            val rows = Await.result(colDB.run(sqlu"DELETE FROM #$table"), Duration.Inf)
        }
        val rows: Option[Int] = Await.result(colDB.run(messages ++= newUserData), Duration.Inf)
        logger.info(s"Inserted ${rows.get} rows.")
        rows
    }

    def insertUserData(data: UserData, table: String): Int = {
        val messages = queryForThisTable(table)
        if (!doesTableExists(table)) {
            val schema = messages.schema.create
            Await.result(colDB.run(DBIO.seq(schema)), 2.seconds)
        }
        val res: Try[Int] = Try(Await.result(colDB.run(messages += data), Duration.Inf))
        res match {
            case Failure(ex) =>
                logger.error(ex.getMessage)
                0
            case Success(value) => value
        }
    }

    def insertBatchData(data: Seq[UserData], table: String) = {
        val messages = queryForThisTable(table)
        if (!doesTableExists(table)) {
            val schema = messages.schema.create
            Await.result(colDB.run(DBIO.seq(schema)), 2.seconds)
        } else {
            val rows = Await.result(colDB.run(sqlu"DELETE FROM #$table"), Duration.Inf)
        }
        Await.result(colDB.run(messages ++= data), Duration.Inf)
    }

    def deleteUserData(uid: Long, table: String): Int = {
        val messages = queryForThisTable(table)
        val query = messages.filter(_.id === uid).delete
        Await.result(colDB.run(query), Duration.Inf)
    }

    def moveRegister(reg: Person, source: String, destiny: String) = {
        val sourceQuery = queryForThisTable(source)
        val ud: UserData = Await.result(colDB.run(sourceQuery.filter(_.id === reg.id).result), Duration.Inf).head
        val destQuery = queryForThisTable(destiny)
        val res = Await.result(colDB.run(destQuery.filter(u => u.first === ud.first && u.last === ud.last).result), Duration.Inf).headOption
        res match {
            case Some(data) =>
            case None =>
                insertUserData(ud, destiny)
                deleteUserData(reg.id, source)
        }
    }

    def copyRegister(reg: Person, source: String, destiny: String) = {
        val sourceQuery = queryForThisTable(source)
        val ud: UserData = Await.result(colDB.run(sourceQuery.filter(_.id === reg.id).result), Duration.Inf).head
        val destQuery = queryForThisTable(destiny)
        val res = Await.result(colDB.run(destQuery.filter(u => u.first === ud.first && u.last === ud.last).result), Duration.Inf).headOption
        res match {
            case Some(data) =>
            case None =>
                insertUserData(ud, destiny)
        }
    }

    def updateUserDate(data: UserData, table: String, id: Long): Int = {
        val messages = queryForThisTable(table).filter(_.id === id).map(m => (m.first, m.last, m.tags, m.date,m.city,m.country,m.admin1,m.admin2))
        Await.result(colDB.run(messages.update((data.first,data.last,data.tags,data.date,data.city,data.country,data.admin1,data.admin2))), Duration.Inf)
    }

    def loadRegisterById(table: String, id: Long): Option[UserData] = {
        val messages = queryForThisTable(table)
        val query = messages.filter(_.id === id)
        exec(query.result.headOption)
    }

    def searchChartByName(name: String, table: String): Seq[UserData] = {
        val messages = queryForThisTable(table)
        val query = messages.filter(m  => (m.first like s"${name}%") || (m.last like s"${name}%")  )
        Await.result(colDB.run(query.result), 2 seconds)
    }

    def getDisplayRowsFromTable(table: String): Seq[(String, String, Long)] = {
        val messages = queryForThisTable(table)
        val query = messages.sortBy{ m => (m.first,m.last) }.map { r => (r.first, r.last, r.id) }
        exec(query.result)
    }

   def getAllRowsFromDB: Seq[UserData] = {
       val tables: Seq[String] = getTableNames
       (for (t <- tables) yield exec(queryForThisTable(t).result)).flatten
   }

    def getAllRowsFromTable(table:String): Seq[UserData] = {
        if (doesTableExists(table))
            exec(queryForThisTable(table).result)
        else
            Seq[UserData]()
    }

}

