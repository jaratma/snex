package eideia

import scala.util.Properties
import java.nio.file.{Files, Paths}
import java.io.File
import java.time.ZonedDateTime
import java.util.Locale

import eideia.atlas.{AtlasQuery, CountryResolver}
import eideia.models.{Location, NexConf}
import eideia.userdata.{LocationTriplet, UserDataManager}
import org.ini4j.Ini
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.ObjectProperty


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
    val userDir = new File(userConfPath)
    val userConfFile = userConfPath + "/application.conf"
    val existUserConFile: Boolean = Files.exists(Paths.get(userConfFile))
    val existsCollectionDB: Boolean =  UserDataManager.checkCollectionDB

//***
    userDir.mkdir
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

    Locale.setDefault(new Locale(config.lang, config.lang))

    val localizedCountries: Map[String,String] = CountryResolver.mapLocalizedCountryTocode(config.lang)

    val defaultLocation: Location =
        AtlasQuery.getLocationFromLegacyTriplet(LocationTriplet(config.locality,config.region, config.country)).get
    val defaultTimeZone: String = defaultLocation.timezone
    val defaultDatabase: String = config.database

    val countries = CountryResolver.mapCodeToLocalizedCountry(config.lang)

    val stage = new ObjectProperty[PrimaryStage](this,"stage")

    implicit val state: State = new State(defaultLocation)

    AtlasQuery.initCustomDB
    //state.logger.info(s"Collection db exists: $existsCollectionDB")
    state.logger.info(s"Legacy db exists: $existsLegacyDB")
}
