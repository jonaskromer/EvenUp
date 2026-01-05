val scala3Version = "3.7.3"

val setJavaFXVersion = settingKey[Seq[ModuleID]]("Sets the JavaFX version and classifier based on the system architecture")

setJavaFXVersion := {
  val arch = sys.props("os.arch")
  val isArm = arch == "aarch64"
  val javafxVersion = "22"
  val javafxClassifier = sys.props("os.name").toLowerCase match {
    case n if n.contains("linux") => if (isArm) "linux-aarch64" else "linux"
    case n if n.contains("mac") => if (isArm) "mac-aarch64" else "mac"
    case _ => "win"
  }
  Seq(
    "org.openjfx" % "javafx-base" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-controls" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-fxml" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-graphics" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-media" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-swing" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-web" % javafxVersion classifier javafxClassifier
  )
}

lazy val root = project
  .in(file("."))
  .settings(
    name := "EvenUp",
    version := "0.1.0-SNAPSHOT",
    coverageEnabled := true,

    scalaVersion := scala3Version,

    scalacOptions += "-Wconf:msg=Implicit parameters should be provided with a `using` clause:s",

    libraryDependencies ++= (Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalactic" %% "scalactic" % "3.2.19", 
      "org.scalatest" %% "scalatest" % "3.2.19" % "test",
      "org.scalafx" %% "scalafx" % "22.0.0-R33" excludeAll(
        ExclusionRule(organization = "org.openjfx")
      ),
      "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
      "com.google.inject" % "guice" % "7.0.0",
      "net.codingwell" %% "scala-guice" % "7.0.0"
    )++ setJavaFXVersion.value)
  )
