package eideia.userdata

import eideia.InitApp
import eideia.models.UserData
import org.scalatest.{FunSuite, Matchers}

class UserDataManagerTest extends FunSuite with Matchers{

    ignore("populate new user data") {
        val rows: Option[Int] = UserDataManager.populateUserDataWithLegacy("api")
        assert(rows.get > 0)
    }

    test("search stored chart data by name") {
        val conf = InitApp.config
        val userData: Seq[UserData] = UserDataManager.searchChartByName("Pérez", "personal")
        assert(userData.isInstanceOf[Seq[UserData]])
        assert(userData.size > 0)
        println(userData.size)
    }

}
