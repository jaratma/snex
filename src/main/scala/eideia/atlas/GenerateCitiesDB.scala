package eideia.atlas

import eideia.atlas.AtlasQuery.{Admin1Table, Admin2Table, LocationTable}
import eideia.models.{Admin1, Admin2, Location}
import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.meta.MTable

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.Source

object GenerateCitiesDB {

    //def exec[T](program: DBIO[T]): T = Await.result(db.run(program), 2 seconds)

    def readCities: List[String] = Source.fromResource("citiesrc/cities1000.txt").getLines.toList

    def readAdmin1: List[String] = Source.fromResource("citiesrc/admin1CodesASCII.txt").getLines.toList

    def readAdmin2: List[String] = Source.fromResource("citiesrc/admin2Codes.txt").getLines.toList

    def getListofLocations: Seq[Location] = {
        val aryBuf = ArrayBuffer[Location]()
        val validFields = List(1,4,5,8,10,11,16,17)
        val cities = readCities
        cities.foreach{ line =>
            val rec = for { (field,ix) <- line.split("\t").zipWithIndex if validFields.contains(ix) } yield field
            val loc = Location(rec(0), rec(1).toDouble,rec(2).toDouble,rec(3),rec(4),rec(5),rec(6).toDouble,rec(7))
            aryBuf += loc
        }
        aryBuf
    }

    def getAdmin1Names: Seq[Admin1] = {
        val justHead = for (line <- readAdmin1) yield line.split("\t").take(2)
        val hd = justHead.map(a => a.head.split("\\."))
        hd zip justHead.map(_.last) map { case (code,name) => Admin1(code.head,code.last,name) }
    }

    def getAdmin2Names: Seq[Admin2] = {
        val justHead = for (line <- readAdmin2) yield line.split("\t").take(2)
        val hd = justHead.map(a => a.head.split("\\."))
        val ls = justHead.map(_.last).map(s => if (s.startsWith("Provincia")) s.drop(13) else s) //"Provincia de "
        hd zip ls map { case (code,name) => Admin2(code.head,code.tail.head, code.last,name) }

    }

    def deleteAllRowsTable(tab: String) :Int = {
        val db = Database.forURL("jdbc:sqlite::resource:cities.db", driver = "org.sqlite.JDBC",
            executor = AsyncExecutor("cities", numThreads=10, queueSize=1000))
        lazy val locations = TableQuery[LocationTable]
        val rows = Await.result(db.run(sqlu"DELETE FROM cities"), 5 seconds)
        rows
    }

    def doesTableExists(table: String): Boolean = {
        val db = Database.forConfig("cities")
        val tables : Future[Vector[MTable]] = db.run(MTable.getTables)
        val res = Await.result(tables,Duration.Inf)
        res.toList.map(_.name.name).contains(table)
        }

    def populateCitiesDatabase : Option[Int] = {
        val loc: Seq[Location] = getListofLocations
        assert(loc.size == 128559)
        println(loc.size)
        val db = Database.forConfig("cities")
        lazy val locations = TableQuery[LocationTable]
        if (!doesTableExists("cities")) {
            val schema = locations.schema.create
            Await.result(db.run(DBIO.seq(schema)), 2.seconds)
        } else {
            val rows = Await.result(db.run(sqlu"DELETE FROM cities"), Duration.Inf)
        }
        println("Inserting rows...")
        val rows: Option[Int] = Await.result(db.run(locations ++= loc), Duration.Inf)
        println(s"Inserted ${rows.get} rows.")
        rows
    }

    def insertAdmin1Codes: Option[Int] = {
        val ads1: Seq[Admin1] = getAdmin1Names
        val db = Database.forConfig("cities")
        val admins1 = TableQuery[Admin1Table]
        if (!doesTableExists("admin1")) {
            println("Creating schema")
            val schema = admins1.schema.create
            Await.result(db.run(DBIO.seq(schema)), 2.seconds)
        } else {
            println("Deleting rows...")
            val rows = Await.result(db.run(sqlu"DELETE FROM admin1"), Duration.Inf)
        }
        println("Inserting rows")
        val rows: Option[Int] = Await.result(db.run(admins1 ++= ads1), Duration.Inf)
        println(s"Inserted ${rows.get} rows.")
        rows
    }

    def insertAdmin2Codes: Option[Int] = {
        val ads2: Seq[Admin2] = getAdmin2Names
        val db = Database.forConfig("cities")
        lazy val admins2 = TableQuery[Admin2Table]
        if (!doesTableExists("admin2")) {
            val schema = admins2.schema.create
            Await.result(db.run(DBIO.seq(schema)), 2.seconds)
        }  else {
            println("Deleting rows...")
            val rows = Await.result(db.run(sqlu"DELETE FROM admin2"), Duration.Inf)
        }
        println("Inserting rows")
        val rows: Option[Int] = Await.result(db.run(admins2 ++= ads2), Duration.Inf)
        println(s"Inserted ${rows.get} rows.")
        rows
    }
}
