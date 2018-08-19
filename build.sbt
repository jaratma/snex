
name := "snex"

version := "0.1"

scalaVersion := "2.12.6"

scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-unchecked",
    "-feature",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-Ywarn-dead-code",
    "-Xfatal-warnings"
)

fork in run := true

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test,
    "org.scalacheck" %% "scalacheck" % "1.14.0" % "test",
    "net.java.dev.jna" % "jna" % "4.5.1",
    "com.typesafe.slick" %% "slick"           % "3.2.3",
    "org.xerial"          % "sqlite-jdbc"     % "3.21.0",
    "com.github.pureconfig" %% "pureconfig" % "0.9.1",
    "org.ini4j" % "ini4j" % "0.5.4",
    "com.softwaremill.sttp" %% "core" % "1.3.0",
    "org.scala-lang.modules" %% "scala-xml" % "1.1.0",
    "ch.qos.logback"      % "logback-classic" % "1.2.3"
)

unmanagedResourceDirectories in Compile += baseDirectory.value / "lib_extra/darwin"
unmanagedResourceDirectories in Test += baseDirectory.value / "lib_extra/darwin"
includeFilter in (Compile, unmanagedResourceDirectories):= ".dylib,.dll,.so"

//mappings in (Compile, packageBin) += {
//    (baseDirectory.value / "lib_extra" / "darwin" / "libswe.dylib") -> "darwin/libswe.dylib"
//}


assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}