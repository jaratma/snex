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
    val existsUserDir : Boolean = Files.exists(Paths.get(userHome + "/.nex2"))

    if (existsLegacyIniFile) {
        val ini: Ini = new Ini(new File(userHome + "/.astronex/cfg.ini"))
        ConfigManager.parseLegacyIniFile(ini)
    }

}
