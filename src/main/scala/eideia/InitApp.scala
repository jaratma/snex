package eideia

import scala.util.Properties
import java.nio.file.{Files, Paths}
import java.io.File
import java.time.ZonedDateTime

import eideia.atlas.AtlasQuery
import eideia.models.{Chart, Location, UserData}
import eideia.userdata.{LocationTriplet, UserDataManager}
import org.ini4j.Ini

object InitApp {
    type ZDT = ZonedDateTime
    val userHome: String = Properties.userHome
    val appDir: String = Properties.userDir
    val existsLegacyDir: Boolean = Files.exists(Paths.get(userHome + "/.astronex"))
    val existsLegacyIniFile: Boolean = Files.exists(Paths.get(userHome + "/.astronex/cfg.ini"))

    val osName: String = Properties.osName
    val userConfPath = userHome + "/.nex2"
    val userDir = new File(userConfPath)
    val userConfFile = userConfPath + "/application.conf"
    val existUserConFile: Boolean = Files.exists(Paths.get(userConfFile))


    userDir.mkdir

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

    //AtlasQuery.initCustomDB

    val defaultLocation: Location =
        AtlasQuery.getLocationFromLegacyTriplet(LocationTriplet(config.locality,config.region, config.country)).get

    val defaultTimeZone: String = defaultLocation.timezone

    def localnow: ZDT = DateManager.now(defaultTimeZone)
    def utcnow: ZDT = DateManager.utcFromLocal(localnow)

    def setNowChart: Chart = Chart(utcnow, defaultLocation.latitude, defaultLocation.longitude)

    def setChartFromLoadData(table: String, id: Long): Chart = {
        val userData: UserData = UserDataManager.loadRegisterById(table, id).get
        val date: ZDT = DateManager.parseDateString(userData.date)
        val location: Location = AtlasQuery.getLocationFromUserData(userData).get
        val utc = DateManager.utcFromLocal(date)
        Chart(utc,location.latitude, location.longitude)
    }

}
