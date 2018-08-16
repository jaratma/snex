package eideia

import scala.util.Properties
import java.nio.file.{Files, Paths}
import java.io.File
import org.ini4j.Ini

object InitApp {
    val userHome: String = Properties.userHome
    val appDir: String = Properties.userDir
    val existsLegacyDir: Boolean = Files.exists(Paths.get(userHome + "/.astronex"))
    val existsLegacyIniFile: Boolean = Files.exists(Paths.get(userHome + "/.astronex/cfg.ini"))

    val osName: String = Properties.osName
    val userConfPath = userHome + "/.nex2"
    val userDir = new File(userConfPath)
    val userConfFile = userConfPath + "/application.conf"
    val existUserConFile: Boolean = Files.exists(Paths.get(userConfFile))

    def init(): Unit = {
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
    }

}
