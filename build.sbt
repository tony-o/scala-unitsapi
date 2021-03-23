name := "unitConverter"
version := "0.1"
organization := "com.tonyo"
scalaVersion := "2.13.5"

resolvers ++= Seq("releases" at "https://oss.sonatype.org/content/repositories/releases",
                  "staging" at "https://oss.sonatype.org/content/repositories/staging",
                  "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
                 )

enablePlugins(JettyPlugin)

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  Seq("net.liftweb" %% "lift-webkit"         % "3.4.3" % "compile",
      "org.eclipse.jetty" % "jetty-webapp"   % "9.4.9.v20180320" % "container;provided",
      "org.eclipse.jetty"  % "jetty-runner"  % "9.4.9.v20180320",
      "org.eclipse.jetty"  % "jetty-servlet" % "9.4.9.v20180320",
      "org.eclipse.jetty"  % "jetty-plus"    % "9.4.9.v20180320",
      "org.scalatest" %% "scalatest" % "3.2.5" % "test",
      "org.slf4j" % "slf4j-log4j12"       % "1.7.9",
     )
}

assemblyMergeStrategy in assembly := {
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}
assemblyOutputPath in assembly := file("target/unitconverter.jar")
