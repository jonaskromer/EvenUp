val scala3Version = "3.7.3"

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

    libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32",

    libraryDependencies ++= Seq(
      "org.openjfx" % "javafx-controls" % "21.0.1" classifier "win",
      "org.openjfx" % "javafx-controls" % "21.0.1" classifier "linux",
      "org.openjfx" % "javafx-controls" % "21.0.1" classifier "mac",
      "org.openjfx" % "javafx-fxml" % "21.0.1" classifier "win",
      "org.openjfx" % "javafx-fxml" % "21.0.1" classifier "linux",
      "org.openjfx" % "javafx-fxml" % "21.0.1" classifier "mac",
      "org.openjfx" % "javafx-graphics" % "21.0.1" classifier "win",
      "org.openjfx" % "javafx-graphics" % "21.0.1" classifier "linux",
      "org.openjfx" % "javafx-graphics" % "21.0.1" classifier "mac",
      "org.openjfx" % "javafx-media" % "21.0.1" classifier "win",
      "org.openjfx" % "javafx-media" % "21.0.1" classifier "linux",
      "org.openjfx" % "javafx-media" % "21.0.1" classifier "mac",
      "org.openjfx" % "javafx-web" % "21.0.1" classifier "win",
      "org.openjfx" % "javafx-web" % "21.0.1" classifier "linux",
      "org.openjfx" % "javafx-web" % "21.0.1" classifier "mac"
    ),

    fork := true,

    //If you dont use this there will be problems mit implicit parameters from ScalaFX
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-Wconf:msg=Implicit parameters should be provided with a `using` clause:s"
    ),

    javaOptions += "--enable-native-access=ALL-UNNAMED"
  )