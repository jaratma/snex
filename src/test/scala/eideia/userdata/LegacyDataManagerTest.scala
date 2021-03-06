package eideia.userdata

import eideia.models._
import eideia.InitApp
import org.scalatest.{FunSuite, Matchers}

class LegacyDataManagerTest extends FunSuite with Matchers {
    test("tables in charts.db") {
        Class.forName("org.sqlite.JDBC")
        assert(InitApp.existsLegacyDir)
        val tables = LegacyDataManager.getTableNamesFromDb
        assert(tables.nonEmpty)
        //tables.foreach(println(_))
    }

    test("charts in table personal") {
        val names = LegacyDataManager.getChartsFromTable("personal")
        assert(names.nonEmpty)
        //names.foreach(println(_))
    }

    test("select countries in charts") {
        val countries: Seq[String] = LegacyDataManager.getListOfCountries("personal")
        assert(countries.isInstanceOf[Seq[String]])
        //countries.toSet[String].foreach(s => println(s))
    }

    test("get location triplets") {
        val triplets : Seq[LocationTriplet] = LegacyDataManager.getLegacyLocationTriplets("personal")
        assert(triplets.isInstanceOf[Seq[LocationTriplet]])
        //println(triplets.size)
    }

    test("get essential legacy case class") {
        val essentialFields = LegacyDataManager.getEssentialFieldsFromLegacyData("personal")
        assert(essentialFields.isInstanceOf[Seq[LegacyEssentialFields]])
    }
}
