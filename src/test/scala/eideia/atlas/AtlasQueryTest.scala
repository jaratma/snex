package eideia.atlas

import org.scalatest.{FunSuite, Matchers}

class AtlasQueryTest extends FunSuite  with  Matchers{

    test("simple") {
        val res = AtlasQuery.queryTimezone("Atlantic/Canary")
        assert(res.size == 99)
    }

}
