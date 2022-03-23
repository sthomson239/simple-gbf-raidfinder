logLevel := Level.Warn

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.8")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.4")

addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.2.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.9.0") //1.8.0

addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.1.0")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.1.0")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.1"