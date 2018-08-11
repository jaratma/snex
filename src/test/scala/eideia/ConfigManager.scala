package eideia

import pureconfig._
import java.nio.file.{ Files, Path, Paths }

case class Location(locality: String, country: String)

object ConfigManager {
    def resourceFromName(name: String): Path = {
        Paths.get(getClass.getResource(name).getPath)
    }

    def getDefaulConfig: Location = {
        val file = resourceFromName("/snexapp.conf")
        val loc = loadConfigFromFiles[Location](Seq[Path](file))
        loc.right.get
    }

}
