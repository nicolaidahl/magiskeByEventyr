import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "magiskebyeventyr"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "mysql" % "mysql-connector-java" % "5.1.16",
    "com.amazonaws" % "aws-java-sdk" % "1.3.11",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
