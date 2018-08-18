package eideia.userdata

import org.scalatest.{FunSuite, Matchers}

class UserDataManagerTest extends FunSuite with Matchers{

    test("populate new user data") {
        val rows: Option[Int] = UserDataManager.populateUserDataWithLegacy("api")
        assert(rows.get > 0)
    }

}
