package eideia

import org.scalatest.{FunSuite, Matchers}

class ConfigManagerTest extends FunSuite with Matchers {
    test("basic conf") {
        val conf = ConfigManager.getDefaulConfig
        assert(conf.region == "56")
    }
}
