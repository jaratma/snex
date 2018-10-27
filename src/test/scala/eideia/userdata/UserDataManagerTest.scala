package eideia.userdata

import eideia.{InitApp, SnexApp}
import eideia.models.UserData
import org.scalatest.{FunSuite, Matchers}
import slick.jdbc.SQLiteProfile.api._

class UserDataManagerTest extends FunSuite with Matchers{

    ignore("populate new user data") {
        val rows: Option[Int] = UserDataManager.populateUserDataWithLegacy("api")
        assert(rows.get > 0)
    }

    test("search stored chart data by name") {
        Class.forName("org.sqlite.JDBC")
        val userData: Seq[UserData] = UserDataManager.searchChartByName("Pérez", "personal")
        //val userData = UserDataManager.loadRegisterById("personal",1)
        assert(userData.isInstanceOf[Seq[UserData]])
        assert(userData.size > 0)
        println(userData.size)
    }

    test("get all rows from all tables") {
        Class.forName("org.sqlite.JDBC")
        val allData: Seq[UserData] = UserDataManager.getAllRowsFromDB
        assert(allData.isInstanceOf[Seq[UserData]])
        println(allData.size)
    }

}
