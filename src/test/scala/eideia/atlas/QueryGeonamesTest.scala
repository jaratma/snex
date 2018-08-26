package eideia.atlas

import org.scalatest.{FunSuite, Matchers}
import eideia.atlas.{QueryGeonames => QG}
import eideia.models.Location
import eideia.InitApp

class QueryGeonamesTest extends FunSuite with Matchers {

    test("get a location from query") {
        val locs =  QG.parseQuery(QG.sendQuery("Gurtnellen","CH"))
        assert(locs.isInstanceOf[Seq[Location]])
    }

    ignore("insert new custom location") {
        val i = InitApp
        val locs =  QG.parseQuery(QG.sendQuery("Gurtnellen","CH"))
        val loc: Location = locs.head
        val rows = AtlasQuery.inserCustomLocaton(loc)
        assert(rows == 1)
    }
}
