addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")

ThisBuild / resolvers ++= Seq(
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal
)

name := "Flink Project"

version := "0.1-SNAPSHOT"

organization := "eeg.assessment"

ThisBuild / scalaVersion := "2.11.11"

val flinkVersion = "1.13.2"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "io.spray" %% "spray-json" % "1.3.6",
  "org.apache.flink" %% "flink-connector-kafka" % flinkVersion,
  //  "org.apache.flink" %% "flink-table" % flinkVersion % "provided",
)

//val moduleDependencies = Seq(
//  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.4"
//)

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= flinkDependencies
//    libraryDependencies ++= moduleDependencies
  )

assembly / mainClass := Some("eeg.assessment.StreamingJob")

// make run command include the provided dependencies
Compile / run  := Defaults.runTask(Compile / fullClasspath,
  Compile / run / mainClass,
  Compile / run / runner
).evaluated

// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true

// exclude Scala library from assembly
assembly / assemblyOption  := (assembly / assemblyOption).value.copy(includeScala = false)
