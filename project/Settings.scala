import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys.{ assembly, assemblyCacheOutput }
import sbtassembly.AssemblyPlugin.autoImport.{ assemblyJarName, assemblyMergeStrategy, MergeStrategy }
import sbtassembly.PathList
import wartremover.WartRemover.autoImport.{ wartremoverErrors, Wart, Warts }

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
      Wart.Null
    )
  )

  lazy val artifactorySettings = Seq(
    resolvers ++= Seq(
      "Cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos"
    )
  )

}

object Settings extends Settings
