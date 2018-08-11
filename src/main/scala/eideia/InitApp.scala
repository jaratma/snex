package eideia

import java.nio.file.{Files, Paths}

object InitApp {
    val userHome: String = System.getProperty("user.home")
    val appDir: String = System.getProperty("user.dir")
    val existsLegacyDir: Boolean = Files.exists(Paths.get(userHome + "/.astronex"))

    val osName: String = System.getProperty("os.name")
    val existsUserDir : Boolean = Files.exists(Paths.get(userHome + "/.nex2"))


}
