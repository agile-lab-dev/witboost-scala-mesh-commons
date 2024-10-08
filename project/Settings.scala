import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys.{ assembly, assemblyCacheOutput }
import sbtassembly.AssemblyPlugin.autoImport.{ assemblyJarName, assemblyMergeStrategy, MergeStrategy }
import sbtassembly.PathList
import scoverage.ScoverageKeys.{ coverageFailOnMinimum, coverageMinimum }
import wartremover.WartRemover.autoImport.{ wartremoverErrors, Wart, Warts }
import xerial.sbt.Sonatype.autoImport._
import xerial.sbt.Sonatype.sonatypeCentralHost

import scala.collection.Seq

trait Settings {

  lazy val commonAssemblySettings = Seq(
    app.k8ty.sbt.gitlab.K8tyGitlabPlugin.gitlabProjectId := "51107980",
    assemblyJarName in assembly := s"${name.value}-${version.value}.jar",
    assemblyCacheOutput in assembly := false,
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", xs @ _*)       => MergeStrategy.first
      case x                                   => MergeStrategy.defaultMergeStrategy(x)
    }
  )

  //TODO remove as Wart as possible
  lazy val wartremoverSettings = Seq(
    wartremoverErrors in (Compile, compile) ++= Warts.allBut(
      Wart.Any,
      Wart.Nothing,
      Wart.ToString,
      Wart.NonUnitStatements,
      Wart.Equals,
      Wart.ArrayEquals,
      Wart.DefaultArguments,
      Wart.TraversableOps,
      Wart.ImplicitParameter,
      Wart.ImplicitConversion,
      Wart.Null,
      Wart.Enumeration
    )
  )

  lazy val scalacOptionsSettings: Seq[String] = Seq(
    "-encoding",
    "UTF-8"
  )

  lazy val projectSettings = Seq(
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage := Some(url("https://github.com/agile-lab-dev/witboost-scala-mesh-commons")),
    organization := "com.witboost.provisioning",
    scalaVersion := "2.13.3",
    version := sys.env.getOrElse("ARTIFACT_VERSION", "0.0.0"),
    coverageMinimum := 90,
    coverageFailOnMinimum := true,
    scmInfo := Some(
      ScmInfo(
        url(s"https://github.com/agile-lab-dev/witboost-scala-mesh-commons"),
        s"scm:git:https://github.com/agile-lab-dev/witboost-scala-mesh-commons.git",
        Some(s"scm:git:git@github.com:agile-lab-dev/witboost-scala-mesh-commons.git")
      )
    ),
    description := "Common self-service functionalities for implementing Scala Tech Adapters for AWS and CDP",
    scalacOptions ++= scalacOptionsSettings,
    Compile / doc / scalacOptions ++= scalacOptionsSettings ++ Seq("-no-java-comments"),
    versionScheme := Some("early-semver"),
    developers := List(
      Developer("lpirazzini", "Lorenzo Pirazzini", "lorenzo.pirazzini@agilelab.it", url("https://github.com/SpyQuel")),
      Developer("nbidotti", "Nicolò Bidotti", "nicolo.bidotti@agilelab.it", url("https://github.com/nicolobidotti"))
    )
  )

  lazy val publishSettings = Seq(
    publishTo := sonatypePublishToBundle.value,
    sonatypeCredentialHost := sonatypeCentralHost,
    publishConfiguration :=
      publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration :=
      publishLocalConfiguration.value.withOverwrite(true),
    publishArtifact.in(Test) := false,
    publishMavenStyle := true
  )

  lazy val buildSettings = {
    //this is an hack to resolve correctly rs-api
    // [warn] [FAILED     ] javax.ws.rs#javax.ws.rs-api;2.1!javax.ws.rs-api.${packaging.type}:  (0ms)
    // https://github.com/sbt/sbt/issues/3618
    sys.props += "packaging.type" -> "jar"
    Seq(
      resolvers ++= Seq(
        "Cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos"
      )
    )
  }

  lazy val commonSettings = projectSettings ++ buildSettings ++ publishSettings

}

object Settings extends Settings
