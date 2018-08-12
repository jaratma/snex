package eideia.userdata

import eideia.models._
import eideia.InitApp
import org.scalatest.{FunSuite, Matchers}

class LegacyDataManagerTest extends FunSuite with Matchers {
    test("tables in charts.db") {
        assert(InitApp.existsLegacyDir)
        val tables = LegacyDataManager.getTableNamesFromDb
        assert(tables.nonEmpty)
        tables.foreach(println(_))
    }

    test("charts in table personal") {
        val names = LegacyDataManager.getChartsFromTable("personal")
        assert(names.nonEmpty)
        names.foreach(println(_))
    }

    test("convert rows to case class") {
        val charts: Seq[LegacyData] = LegacyDataManager.convertTableChartsToCaseClass("personal")
        assert(charts.isInstanceOf[Seq[LegacyData]])
        println(charts.head)

    }

    test("select countries in charts") {
        val countries: Seq[String] = LegacyDataManager.getListOfCountries("personal")
        assert(countries.isInstanceOf[Seq[String]])
        countries.toSet[String].foreach(s => println(s))
    }
}
