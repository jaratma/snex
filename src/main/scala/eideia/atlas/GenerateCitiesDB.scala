package eideia.atlas

import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import eideia.models.Location
import eideia.atlas.AtlasQuery.LocationTable
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object GenerateCitiesDB {

    val db = Database.forConfig("actualizedcities")

    def getCitiesLines: List[String] = Source.fromResource("citiesrc/cities1000.txt").getLines.toList

    def getListofLocations: Seq[Location] = {
        val aryBuf = ArrayBuffer[Location]()
        val validFields = List(1,4,5,8,10,11,16,17)
        val lines = getCitiesLines
        lines.foreach{ line =>
            val rec = for { (field,ix) <- line.split("\t").zipWithIndex if validFields.contains(ix) } yield field
          val loc = Location(rec(0), rec(1).toDouble,rec(2).toDouble,rec(3),rec(4),rec(5),rec(6).toDouble,rec(7))
         aryBuf += loc
        }
        aryBuf
    }

    def exec[T](program: DBIO[T]): T = Await.result(db.run(program), 2 seconds)

    def populateDatabase(loc: Seq[Location]) : Option[Int] = {
        lazy val messages = TableQuery[LocationTable]
        val schema = messages.schema.create
        db.run(DBIO.seq(schema))
        val rows: Option[Int] = exec(messages ++= loc)
        println(s"Inserted ${rows.get} rows.")
        rows
    }


}
