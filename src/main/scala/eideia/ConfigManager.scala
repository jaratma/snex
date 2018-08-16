package eideia

import java.io.{File,FileOutputStream}
import java.nio.file.{Path, Paths}
import org.ini4j.Ini
import org.ini4j.Profile.Section

import eideia.atlas.CountryResolver
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

    def getUserConfig(path: String): NexConf = {
        val file : Path = resourceFromName(path)
        val conf = loadConfig[NexConf](file)
        conf.right.get
    }

    def parseLegacyIniFile(ini: Ini): NexConf = {
        val langSection: Section = ini.get("LANG")
        val defaultSection: Section = ini.get("DEFAULT")
        val lang = langSection.get("lang").toUpperCase
        val country = CountryResolver.fipToIso(defaultSection.get("country"))
        val loc = defaultSection.get("locality")
        val reg = defaultSection.get("region")
        val db = defaultSection.get("database")
        NexConf(lang, loc, country, reg, db)
    }

    def saveNexConf(iniconf: NexConf, path: File): Unit = {
        val conf = ConfigWriter[NexConf].to(iniconf)
        val out = new FileOutputStream(path)
        saveConfigToStream(conf,out)
    }


}
