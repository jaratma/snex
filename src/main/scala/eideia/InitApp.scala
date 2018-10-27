package eideia

import scala.util.Properties
import java.nio.file.{Files, Paths}
import java.io.{File, FileInputStream, ObjectInputStream}
import java.time.ZonedDateTime
import java.util.Locale

import eideia.atlas.{AtlasQuery, CountryResolver}
import eideia.models.{Location, NexConf, Register, UserData}
import eideia.userdata.{LocationTriplet, UserDataManager}
import org.ini4j.Ini
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.ObjectProperty
import com.typesafe.scalalogging.Logger

import scala.collection.mutable
//import eideia.calc.Huber


object InitApp {
    type ZDT = ZonedDateTime
    val userHome: String = Properties.userHome
    val appDir: String = Properties.userDir
    val existsLegacyDir: Boolean = Files.exists(Paths.get(userHome + "/.astronex"))
    val existsLegacyIniFile: Boolean = Files.exists(Paths.get(userHome + "/.astronex/cfg.ini"))
    val existsLegacyDB: Boolean = Files.exists(Paths.get(userHome + "/.astronex/charts.db"))
    val legacyDBFile = userHome + "/.astronex/charts.db"

    val osName: String = Properties.osName
    val userConfPath = userHome + "/.nex2"
    val failDir = new File(userConfPath+"/failedImports")
    val userConfFile = userConfPath + "/application.conf"
    val existUserConFile: Boolean = Files.exists(Paths.get(userConfFile))
    val existsCollectionDB: Boolean =  UserDataManager.checkCollectionDB
    val customDbUrl = s"jdbc:sqlite:$userConfPath/customloc.db"
    val collectionUrl = s"jdbc:sqlite:$userConfPath/collection.db"

    val mruFile = userConfPath + "/mru.dat"
    val existsMruFile: Boolean = Files.exists(Paths.get(mruFile))

//***
    failDir.mkdir()
//***

    val config: NexConf = existUserConFile match {
        case false => existsLegacyIniFile match {
            case true =>
                val ini: Ini = new Ini(new File(userHome + "/.astronex/cfg.ini"))
                val legacyConf = ConfigManager.parseLegacyIniFile(ini)
                ConfigManager.saveNexConf(legacyConf, new File(userConfFile))
                legacyConf
            case _ =>
                val conf: NexConf = ConfigManager.getDefaulConfig
                ConfigManager.saveNexConf(conf, new File(userConfFile))
                conf
        }
        case _ =>
            ConfigManager.getUserConfig(userConfFile)
    }

    implicit val logger = Logger[State]
    AtlasQuery.initCustomDB

    Locale.setDefault(new Locale(config.lang, config.lang))

    val localizedCountries: Map[String,String] = CountryResolver.mapLocalizedCountryTocode(config.lang)

    val defaultLocation: Location =
        AtlasQuery.getLocationFromLegacyTriplet(LocationTriplet(config.locality,config.region, config.country)).get
    val defaultTimeZone: String = defaultLocation.timezone
    val defaultDatabase: String = config.database

    val countries = CountryResolver.mapCodeToLocalizedCountry(config.lang)

    val stage = new ObjectProperty[PrimaryStage](this,"stage")

    implicit val state: State = new State(defaultLocation)

    val mostRecentData = mutable.Queue[UserData]()

    if (existsMruFile) {
        val f = new File(InitApp.mruFile)
        val iis = new ObjectInputStream(new FileInputStream(f))
        val col = iis.readObject.asInstanceOf[mutable.Queue[UserData]]
        mostRecentData ++= col
        iis.close()
    }

    UserDataManager.initCollectionDB()
    //logger.info(s"Collection db exists: $existsCollectionDB")
    //logger.info(s"Legacy db exists: $existsLegacyDB")

    //def displayChart(implicit state: State) = {
    //    state.currentRegister() =  Register("personal", 1L)
    //    val driver = new Huber(state.currentChart.value)
    //    val planets = driver.planets
    //    planets.foreach(println)
    //    val houses = driver.houses
    //    houses.foreach (println )
    //    println(driver.vertex)
}
