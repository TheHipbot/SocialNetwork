lazy val root = (project in file(".")).
  settings(
    name := "socialnetwork",
    version := "1.0",
    scalaVersion := "2.11.6",
    resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "spray repo" at "http://repo.spray.io",
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.9",
    libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.3.9",
    libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.9",
    libraryDependencies += "org.mashupbots.socko" % "socko-webserver_2.11" % "0.6.0",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.4.0"
  )

