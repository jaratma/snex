package eideia.atlas

import eideia.models.{Location, Admin1, Admin2}
import org.scalatest.{FunSuite, Matchers}

class GenerateCitiesDBTest extends FunSuite with Matchers {
    test("read cities file") {
        val lines = GenerateCitiesDB.readCities
        assert(lines.size == 128559)
    }

    test("read admin1 file") {
        val lines = GenerateCitiesDB.readAdmin1
        assert(lines.size == 3968)
    }

    test("get admin1 names") {
        val codes = GenerateCitiesDB.getAdmin1Names
        assert(codes.isInstanceOf[Seq[Admin1]])
    }

    test("get admin2 names") {
        val codes = GenerateCitiesDB.getAdmin2Names
        assert(codes.isInstanceOf[Seq[Admin2]])
    }

    test("read admin2 file") {
        val lines = GenerateCitiesDB.readAdmin2
        assert(lines.size == 45681)
    }


    test("get list of locations") {
        val ary = GenerateCitiesDB.getListofLocations
        assert(ary.isInstanceOf[Seq[Location]])
    }

    ignore("insert locations in db") {
        val rows = GenerateCitiesDB.populateCitiesDatabase
        assert(rows.get == 128559)
    }

    ignore("delete all rows") {
        val rows = GenerateCitiesDB.deleteAllRowsTable("cities")
        assert(rows == 128559)
    }

    test("check cities table exists") {
        val res: Boolean = GenerateCitiesDB.doesTableExists("cities")
        assert(res)
    }



}
