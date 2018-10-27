package eideia.userdata

import eideia.models._
import java.sql._

import eideia.atlas.AtlasQuery

import scala.util.{Failure, Properties, Success, Try}
import scala.collection.mutable.ArrayBuffer

case class LocationTriplet(city: String, region: String, country: String)
case class LegacyEssentialFields(first: String, last: String, date: String, zone: String, city: String, country: String, lat: Double, lng: Double)

object LegacyDataManager {
    val url = s"jdbc:sqlite:${Properties.userHome}/.astronex/charts.db"
    Class.forName("org.sqlite.JDBC")

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

    def getListOfAStringField(field: String, table: String) : Seq[String] = {
        assert(List("first","last","date","city","region","country","zone").contains(field))
        val arybuf = ArrayBuffer[String]()
        val stmt: Try[Statement] = getStatement
        assert(stmt.isSuccess)
        val sql = s"select $field from $table"
        val rs: ResultSet = stmt.get.executeQuery(sql)
        while (rs.next) {
            arybuf += rs.getString(s"$field")
        }
        arybuf
    }

    def getListOfCountries(table: String): Seq[String] = {
        getListOfAStringField("country", table)
    }

    def getListOfDates(table: String): Seq[String] = {
        getListOfAStringField("date", table)
    }

    def getListOfZones(table: String): Seq[String] = {
        getListOfAStringField("zone", table)
    }

    def getFirstPairDateZone(table: String): (String,String) = {
        val dt = getListOfDates(table).head
        val zn = getListOfZones(table).head
        (dt,zn)
    }

    def getLegacyLocationTriplets(table: String): Seq[LocationTriplet] = {
        val arybuf = ArrayBuffer[LocationTriplet]()
        val stmt: Try[Statement] = getStatement
        //assert(stmt.isSuccess)
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

    def encodedTriplets(table: String): Seq[LocationTriplet] = {
        val legacyTriplets = getLegacyLocationTriplets(table)
        val arybuf = ArrayBuffer[LocationTriplet]()
        legacyTriplets.foreach { t =>
            val regionCode = AtlasQuery.getAdmin1Code(t.region)
            val countryCode = AtlasQuery.getCountryCode(t.country)
            arybuf += LocationTriplet(t.city,regionCode,countryCode)
        }
        arybuf
    }

    def getLocationsFromAtlas(table: String): Seq[Location] = {
        val triplets = encodedTriplets(table)
         (for {t <- triplets} yield AtlasQuery.getLocationFromLegacyDoublet(t)).flatten
    }

    def getEssentialFieldsFromLegacyData(table: String) : Seq[LegacyEssentialFields] = {
        val arybuf = ArrayBuffer[LegacyEssentialFields]()
        val stmt: Try[Statement] = getStatement
        assert(stmt.isSuccess)
        val sql = s"select first, last, date, city, region, country, zone, latitud, longitud from $table"
        val rs: ResultSet = stmt.get.executeQuery(sql)
        while (rs.next) {
            val first = rs.getString("first")
            val last = rs.getString("last")
            val date = rs.getString("date")
            val city = rs.getString("city")
            val country = rs.getString("country")
            val zone = rs.getString("zone")
            val lat = rs.getDouble("latitud")
            val lng = rs.getDouble("longitud")
            val countryCode = AtlasQuery.getCountryCode(country)
            arybuf += LegacyEssentialFields(first, last, date, zone, city, countryCode,lat,lng)
        }
        arybuf
    }
}


