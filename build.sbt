val scala3Version = "3.7.3"

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}

lazy val root = project
  .in(file("."))
  .settings(
    name := "EvenUp",
    version := "0.1.0-SNAPSHOT",
    coverageEnabled := true,
    scalaVersion := scala3Version,
    
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.19",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test",
    
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "21.0.0-R32",
      "org.openjfx" % "javafx-controls" % "21.0.1",
      "org.openjfx" % "javafx-fxml" % "21.0.1",
      "org.openjfx" % "javafx-graphics" % "21.0.1",
      "org.openjfx" % "javafx-media" % "21.0.1",
      "org.openjfx" % "javafx-web" % "21.0.1"
    ),
    
    libraryDependencies ++= Seq(
      "org.openjfx" % s"javafx-controls" % "21.0.1" classifier osName,
      "org.openjfx" % s"javafx-fxml" % "21.0.1" classifier osName,
      "org.openjfx" % s"javafx-graphics" % "21.0.1" classifier osName,
      "org.openjfx" % s"javafx-media" % "21.0.1" classifier osName,
      "org.openjfx" % s"javafx-web" % "21.0.1" classifier osName
    ),
    
    fork := true
  )