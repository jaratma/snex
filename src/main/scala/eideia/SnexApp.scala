package eideia

object SnexApp extends App{

    val config = InitApp.config

    println(InitApp.userConfPath)
    println(InitApp.userConfFile)
    println(config.lang)
}
