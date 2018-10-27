package eideia

import org.ini4j.Ini
import java.io.File
import java.nio.file.{Files, Paths}
import org.scalatest.{FunSuite, Matchers}
import InitApp.userDir

class ConfigManagerTest extends FunSuite with Matchers {
    test("basic conf") {
        val conf = ConfigManager.getDefaulConfig
        assert(conf.region == "56")
    }

    ignore("save conf") {
        val ini: Ini = new Ini(new File("/Users/jose/.astronex/cfg.ini"))
        val conf = ConfigManager.parseLegacyIniFile(ini)
        ConfigManager.saveNexConf(conf, new File(userDir+"/snex.conf"))
        assert(Files.exists(Paths.get(userDir + "/snex.conf")))
    }
}
