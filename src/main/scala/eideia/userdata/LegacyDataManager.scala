package eideia.userdata

import eideia.models._
import java.sql._
import eideia.InitApp
import scala.util.{Failure, Success, Try}
import scala.collection.mutable.ArrayBuffer

case class LocationTriplet(city: String, region: String, country: String)

object LegacyDataManager {
    val driverClassName ="org.sqlite.JDBC"
    val url: String = s"jdbc:sqlite:${InitApp.userHome}/tmp/charts/charts.db"
    Class.forName(driverClassName)

    def getStatement: Try[Statement] = {
        Try(DriverManager.getConnection(url)).map(_.createStatement())
    }

    def getTableNamesFromDb : Seq[String] = {
        val arybuf = ArrayBuffer[String]()
        getStatement match {
            case Success(stmt) =>
                val sql = "select name from sqlite_master where type='table'"
                val rs: ResultSet = stmt.executeQuery(sql)
                while (rs.next) {
                    arybuf += rs.getString("name")
                }
            case Failure(ex) => List(ex.getMessage)
        }
        arybuf
    }

    def getChartsFromTable(table: String): Seq[String] = {
        val arybuf = ArrayBuffer[String]()
        val stmt: Try[Statement] = getStatement
        assert(stmt.isSuccess)
        val sql = s"select first from $table"
        val rs: ResultSet = stmt.get.executeQuery(sql)
        while (rs.next) {
            arybuf += rs.getString("first")
        }
        arybuf
    }

    def getListOfCountries(table: String): Seq[String] = {
        val arybuf = ArrayBuffer[String]()
        val stmt: Try[Statement] = getStatement
        assert(stmt.isSuccess)
        val sql = s"select country from $table"
        val rs: ResultSet = stmt.get.executeQuery(sql)
        while (rs.next) {
            arybuf += rs.getString("country")
        }
        arybuf

    }

    def getLegacyLocationTriplets(table: String): Seq[LocationTriplet] = {
        val arybuf = ArrayBuffer[LocationTriplet]()
        val stmt: Try[Statement] = getStatement
        assert(stmt.isSuccess)
        val sql = s"select city, region, country from $table"
        val rs: ResultSet = stmt.get.executeQuery(sql)
        while (rs.next) {
            val city = rs.getString("city")
            val region = rs.getString("region")
            val country = rs.getString("country")
            arybuf += LocationTriplet(city,region,country)
        }
        arybuf
    }

    def convertTableChartsToCaseClass(table: String): Seq[LegacyData] = {
        val arybuf = ArrayBuffer[LegacyData]()
        val stmt = getStatement
        assert(stmt.isSuccess)
        val rs  = stmt.map(s => s.executeQuery(s"select * from $table")).get
        while (rs.next) {
            val first: String = rs.getString("first")
            val last: String = rs.getString("last")
            val category: String = rs.getString("category")
            val date: String = rs.getString("date")
            val city: String = rs.getString("city")
            val region: String = rs.getString("region")
            val country: String = rs.getString("country")
            val longitud: Double = rs.getDouble("longitud")
            val latitud: Double = rs.getDouble("latitud")
            val zone: String = rs.getString("zone")
            val sun: Double = rs.getDouble("sun")
            val moo: Double = rs.getDouble("moo")
            val mer: Double = rs.getDouble("mer")
            val ven: Double = rs.getDouble("ven")
            val mar: Double = rs.getDouble("mar")
            val jup: Double = rs.getDouble("jup")
            val sat: Double = rs.getDouble("sat")
            val ura: Double = rs.getDouble("ura")
            val nep: Double = rs.getDouble("nep")
            val plu: Double = rs.getDouble("plu")
            val nod: Double = rs.getDouble("nod")
            val h1: Double = rs.getDouble("h1")
            val h2: Double = rs.getDouble("h2")
            val h3: Double = rs.getDouble("h3")
            val h4: Double = rs.getDouble("h4")
            val h5: Double = rs.getDouble("h5")
            val h6: Double = rs.getDouble("h6")
            val h7: Double = rs.getDouble("h7")
            val h8: Double = rs.getDouble("h8")
            val h9: Double = rs.getDouble("h9")
            val h10: Double = rs.getDouble("h10")
            val h11: Double = rs.getDouble("h11")
            val h12: Double = rs.getDouble("h12")
            val comment: String = rs.getString("comment")
            arybuf += LegacyData(first, last, category, date, city, region, country, longitud, latitud, zone,
                sun, moo, mer, ven, mar, jup, sat, ura, nep, plu, nod,
                h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12, comment)
        }
        arybuf
    }
}


