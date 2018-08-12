package eideia.atlas

import eideia.models.Location
import org.scalatest.{FunSuite, Matchers}

class GenerateCitiesDBTest extends FunSuite with Matchers {
    test("read cities file") {
        val lines = GenerateCitiesDB.getCitiesLines
        assert(lines.size == 128559)
    }

    test("get list of locations") {
        val ary = GenerateCitiesDB.getListofLocations
        assert(ary.isInstanceOf[Seq[Location]])
    }

    test("insert locations in db") {
        val rows = GenerateCitiesDB.populateDatabase(GenerateCitiesDB.getListofLocations)
        assert(rows.get == 128559)
    }
}
