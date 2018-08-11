package eideia.userdata

import eideia.models._
import java.sql._
import eideia.InitApp
import scala.util.{Failure, Success, Try}

object LegacyDataManager {
    val driverClassName ="org.sqlite.JDBC"
    val url: String = s"jdbc:sqlite:${InitApp.userHome}/tmp/charts/charts.db"
    Class.forName(driverClassName)

    Try(DriverManager.getConnection(url)).map(_.createStatement()) match {
        case Success(stmt) =>
            val sql = "select name from sqlite_master where type='table'"
            val rs: ResultSet = stmt.executeQuery(sql)
            while (rs.next) {
                println(rs.getString("name"))
            }
        case Failure(exception) => println("Something nasty happened.")
    }
}


