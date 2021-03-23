import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {
val liftVersion = "3.3.0"

override def libraryDependencies = Set(
  "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
  "net.liftweb" %% "lift-common" % liftVersion % "compile->default",
  "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
  "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default"
) ++ super.libraryDependencies

}
