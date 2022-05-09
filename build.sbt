version := "1.0"

lazy val root = (project in file(".")).aggregate(server, client)

lazy val stream = (project in file("stream"))
  .settings(
    scalaVersion := "2.13.1",
    name := "raidfinder-stream",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-testkit" % Versions.Akka % Test,
      "com.typesafe.akka" %% "akka-actor-typed" % Versions.Akka,
      "com.lihaoyi" %% "upickle" % "1.4.0",
      "com.typesafe.akka" %% "akka-stream-typed" % Versions.Akka,
      "org.twitter4j" % "twitter4j-stream" % Versions.Twitter4j
    )
  ).dependsOn(protocolJVM)

lazy val protocol = (crossProject in file("protocol"))
  .settings(
    scalaVersion := "2.13.1",
    name := "raidfinder-protocol",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "1.4.0"
    ))
  .enablePlugins(ScalaJSPlugin)
  .settings()
lazy val protocolJVM = protocol.jvm
lazy val protocolJS = protocol.js

lazy val server = (project in file("server"))
  .settings(
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    herokuProcessTypes in Compile := Map(
      "web" -> "target/universal/stage/bin/raidfinder-server -Dhttp.port=$PORT",
    ),
    herokuIncludePaths in Compile := Seq(
      "server/app",
      "server/conf/application.conf",
      "server/public"
    ),
    herokuSkipSubProjects in Compile := false,
    herokuAppName in Compile := "simple-gbf-raidfinder",
    scalaVersion := "2.13.1",
    name := "raidfinder-server",
    libraryDependencies ++= Seq(
      "com.vmunier" %% "scalajs-scripts" % "1.2.0",
      "org.webjars" % "bootstrap" % "3.3.6",
      "com.iheart" %% "ficus" % "1.5.1",
      "com.typesafe.play" %% "play" % Versions.Play,
      "com.lihaoyi" %% "upickle" % "1.4.0",
      "com.lihaoyi" %% "scalatags" % "0.9.4",
      "com.typesafe.play" %% "filters-helpers" % Versions.Play,
      "com.typesafe.play" %% "play-logback" % Versions.Play,
      "com.typesafe.play" %% "play-netty-server" % Versions.Play
    )
  ).enablePlugins(PlayScala).dependsOn(stream, protocolJVM)

val jsPath = settingKey[File]("Output directory for scala.js compiled files")
lazy val client = (project in file("client")).dependsOn(protocolJS)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalacOptions += "-Ymacro-annotations",
    scalaVersion := "2.13.1",
    name := "raidfinder-client",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0",
      "org.lrng.binding" %%% "html" % "1.0.3",
      "com.lihaoyi" %%% "upickle" % "1.4.0",
      "com.lihaoyi" %%% "scalatags" % "0.9.4",
      "org.webjars.npm" % "moment" % Versions.MomentJS,
      "org.webjars.bower" % "dialog-polyfill" % Versions.DialogPolyfillJS,
    )
  ).enablePlugins(ScalaJSWeb)

onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )