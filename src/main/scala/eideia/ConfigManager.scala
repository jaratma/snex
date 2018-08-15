package eideia

import java.nio.file.{Path, Paths}
import org.ini4j.Ini
import org.ini4j.Profile.Section

import pureconfig._

case class NexConf(lang: String, locality: String, country: String, region: String, database: String)

object ConfigManager {
    def resourceFromName(name: String): Path = {
        Paths.get(getClass.getResource(name).getPath)
    }

    def getDefaulConfig: NexConf = {
        val file : Path = resourceFromName("/snexapp.conf")
        val conf = loadConfigFromFiles[NexConf](Seq[Path](file))
        conf.right.get
    }

    def parseLegacyIniFile(ini: Ini):Map[String,String] = {
        val langSection: Section = ini.get("LANG")
        val defaultSection: Section = ini.get("DEFAULT")
        Map[String,String]("lang" ->  langSection.get("lang"),
            "locality" -> defaultSection.get("locality"),
            "country" -> defaultSection.get("country"),
            "region" -> defaultSection.get("region"),
            "database" -> defaultSection.get("database"))
    }


}
