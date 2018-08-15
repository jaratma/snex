package eideia.atlas

import org.scalatest.{FunSuite, Matchers}
import eideia.userdata.{LegacyDataManager, LocationTriplet}

import scala.collection.mutable.ArrayBuffer

class AtlasQueryTest extends FunSuite  with  Matchers{

    test("simple") {
        val res = AtlasQuery.queryTimezone("Atlantic/Canary")
        assert(res.size == 100)
    }

    test("find region code fro legacy name") {
        val triplets = LegacyDataManager.getLegacyLocationTriplets("personal")
        val arybuf = ArrayBuffer[String]()
        triplets.foreach { t => arybuf += AtlasQuery.getAdmin1CodeFromTriplet(t) }
        assert(triplets.size == arybuf.size)
        arybuf.foreach(println(_))
    }

}
