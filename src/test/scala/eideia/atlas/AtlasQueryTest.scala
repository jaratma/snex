package eideia.atlas

import eideia.models.Location
import org.scalatest.{FunSuite, Matchers}
import eideia.userdata.{LegacyDataManager, LocationTriplet}
import scala.language.reflectiveCalls

import scala.collection.mutable.ArrayBuffer

class AtlasQueryTest extends FunSuite  with  Matchers{

    test("simple") {
        val res = AtlasQuery.queryTimezone("Atlantic/Canary")
        assert(res.size == 100)
    }

    test("find region code from legacy name") {
        val triplets = LegacyDataManager.getLegacyLocationTriplets("personal")
        val arybuf = ArrayBuffer[String]()
        triplets.foreach { t => arybuf += AtlasQuery.getAdmin1CodeFromTriplet(t) }
        assert(triplets.size == arybuf.size)
        //arybuf.foreach(println(_))
    }

    test("get country code from legacy name") {
        val triplets = LegacyDataManager.getLegacyLocationTriplets("api")
        val arybuf = ArrayBuffer[String]()
        triplets.foreach( { t => arybuf += AtlasQuery.getCountryCodeFromTriplet(t)})
        assert(triplets.size == arybuf.size)
        //arybuf.foreach(println(_))
    }

    test("encode triplets") {
        val triplets = LegacyDataManager.getLegacyLocationTriplets("api")
        val arybuf = ArrayBuffer[LocationTriplet]()
        triplets.foreach { t =>
            val regionCode = AtlasQuery.getAdmin1Code(t.region)
            val countryCode = AtlasQuery.getCountryCode(t.country)
            arybuf += LocationTriplet(t.city,regionCode,countryCode)
        }
        assert(triplets.size == arybuf.size)
    }

    test("get encoded triplets(city-country+region)") {
        val triplets = LegacyDataManager.encodedTriplets("personal")
        val successBuf = ArrayBuffer[Option[Location]]()
        var failBuf = 0
        triplets.foreach{ t =>
            val opLoc: Option[Location] = AtlasQuery.getLocationFromLegacyTriplet(t)
            opLoc match {
                case Some(loc) => successBuf += opLoc
                case None =>
                    //println(s"${t.city} ${t.country} ${t.region}")
                    failBuf += 1
            }
        }
        assert(successBuf.flatten.size + failBuf == triplets.size)
        println(s"Failed triplets: $failBuf")
    }

    val fixture = new {
        val successBuf = ArrayBuffer[Option[Location]]()
        var failBuf = 0
    }

    test("get encoded doublets (city+country)") {
        val triplets = LegacyDataManager.encodedTriplets("api")
        val successBuf = ArrayBuffer[Option[Location]]()
        var failBuf = 0
        triplets.foreach{ t =>
            val opLoc: Option[Location] = AtlasQuery.getLocationFromLegacyDoublet(t)
            opLoc match {
                case Some(loc) => successBuf += opLoc
                case None =>
                    //println(s"${t.city} ${t.country} ${t.region}")
                    failBuf += 1
            }
        }
        assert(successBuf.flatten.size + failBuf == triplets.size)
        println(s"Failed triplets: $failBuf")
    }

    test("get locations from essential legacy data") {
        val essentials = LegacyDataManager.getEssentialFieldsFromLegacyData("personal")
        essentials.foreach { e =>
            val opLoc: Option[Location] = AtlasQuery.getLocationFromLegacyData(e)
            opLoc match {
                case Some(loc) => fixture.successBuf += opLoc
                case None =>
                    //println(s"${e.city} ${e.country} ${e.zone}")
                    fixture.failBuf += 1
            }
        }
        assert(fixture.successBuf.flatten.size + fixture.failBuf == essentials.size)
        println(s"Failed triplets: ${fixture.failBuf}")
    }
}
