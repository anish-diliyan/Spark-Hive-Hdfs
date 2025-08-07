name := "spark-hive-hdfs"

version := "1.0"

scalaVersion := "2.12.15"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

// Code coverage settings (disabled for assembly)
coverageEnabled := false
coverageMinimumStmtTotal := 80
coverageFailOnMinimum := true
coverageExcludedPackages := ".*Main.*;.*App.*"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")

Test / parallelExecution := false
Test / fork := true
Test / testOptions += Tests.Argument("-oD")

val sparkVersion = "3.3.0"
val hadoopVersion = "3.2.2"
val hiveVersion = "4.0.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-hive" % sparkVersion % "provided",
  "org.apache.hadoop" % "hadoop-client" % hadoopVersion % "provided",
  "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion % "provided",
  "org.apache.hive" % "hive-jdbc" % hiveVersion % "provided",
  "com.typesafe" % "config" % "1.4.2",
  "ch.qos.logback" % "logback-classic" % "1.5.13",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.40.17" % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test
)