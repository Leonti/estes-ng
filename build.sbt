name := "Estes"

version := "1.0"

scalaVersion := "2.11.2"


libraryDependencies ++= Seq(
    "io.spray" %% "spray-can" % "1.3.1",
    "io.spray" %% "spray-http" % "1.3.1",
    "io.spray" %% "spray-client" % "1.3.1",
    "io.spray" %% "spray-routing" % "1.3.1",
    "io.spray" %% "spray-json" % "1.2.6",
    "com.typesafe.akka" %% "akka-actor" % "2.3.4",
    "com.typesafe.akka" %% "akka-slf4j" % "2.3.4",
    "ch.qos.logback" % "logback-classic" % "1.0.13",
    "org.mongodb" %% "casbah" % "2.7.3",
    "com.novus" %% "salat" % "1.9.8"
)

resolvers ++= Seq(
    "Spray repository" at "http://repo.spray.io",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype repository" at "https://oss.sonatype.org/content/repositories/releases/"
)

