name := """crm-services"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  //jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test,
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.1",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "mysql" % "mysql-connector-java" % "5.1.26",
  "org.postgresql" % "postgresql" % "9.4.1208.jre7",
  "com.h2database" % "h2" % "1.4.191",
  "com.typesafe.play" % "play-mailer_2.11" % "5.0.0-M1",
  "org.mindrot" % "jbcrypt" % "0.3m"
)

libraryDependencies += filters

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
