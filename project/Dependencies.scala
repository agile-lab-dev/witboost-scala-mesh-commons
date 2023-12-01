import sbt._

trait Dependencies {

  lazy val scalaTestVrs           = "3.1.0"
  lazy val scalaMockVrs           = "4.4.0"
  lazy val serverMockVrs          = "5.11.2"
  lazy val typeSafeConfigVrs      = "1.3.4"
  lazy val typeSafeLoggingVrs     = "3.9.4"
  lazy val logBackClassicVrs      = "1.2.10"
  lazy val circeVrs               = "0.14.1"
  lazy val catsVers               = "2.3.0"
  lazy val sttpVrs                = "3.3.15"
  lazy val awsSdkV2Vrs            = "2.17.131"
  lazy val awsLambdaJavaEventsVrs = "2.2.6"
  lazy val awsLambdaJavaCoreVrs   = "1.2.0"
  lazy val cdpSdkVrs              = "0.9.96"

  lazy val typeSafeConfig      = "com.typesafe"                   % "config"                 % typeSafeConfigVrs
  lazy val typeSafeLogging     = "com.typesafe.scala-logging"    %% "scala-logging"          % typeSafeLoggingVrs
  lazy val logBackClassic      = "ch.qos.logback"                 % "logback-classic"        % logBackClassicVrs
  lazy val circeParser         = "io.circe"                      %% "circe-parser"           % circeVrs
  lazy val circeGeneric        = "io.circe"                      %% "circe-generic"          % circeVrs
  lazy val circeYaml           = "io.circe"                      %% "circe-yaml"             % circeVrs
  lazy val catsCore            = "org.typelevel"                 %% "cats-core"              % catsVers
  lazy val sttpCore            = "com.softwaremill.sttp.client3" %% "core"                   % sttpVrs
  lazy val sttpCirce           = "com.softwaremill.sttp.client3" %% "circe"                  % sttpVrs
  lazy val sttpBackend         = "com.softwaremill.sttp.client3" %% "okhttp-backend"         % sttpVrs
  lazy val awsSdkS3            = "software.amazon.awssdk"         % "s3"                     % awsSdkV2Vrs
  lazy val awsS3Control        = "software.amazon.awssdk"         % "s3control"              % awsSdkV2Vrs
  lazy val awsSdkIam           = "software.amazon.awssdk"         % "iam"                    % awsSdkV2Vrs
  lazy val awsSdkLambda        = "software.amazon.awssdk"         % "lambda"                 % awsSdkV2Vrs
  lazy val awsSdkSecrets       = "software.amazon.awssdk"         % "secretsmanager"         % awsSdkV2Vrs
  lazy val awsSdkDynamo        = "software.amazon.awssdk"         % "dynamodb"               % awsSdkV2Vrs
  lazy val awsLambdaJavaEvents = "com.amazonaws"                  % "aws-lambda-java-events" % awsLambdaJavaEventsVrs
  lazy val awsLambdaJavaCore   = "com.amazonaws"                  % "aws-lambda-java-core"   % awsLambdaJavaCoreVrs
  lazy val cdpSdk              = "com.cloudera.cdp"               % "cdp-sdk-java"           % cdpSdkVrs
  lazy val scalaTest           = "org.scalatest"                 %% "scalatest"              % scalaTestVrs  % "test"
  lazy val scalaMock           = "org.scalamock"                 %% "scalamock"              % scalaMockVrs  % "test"
  lazy val serverMock          = "org.mock-server"                % "mockserver-netty"       % serverMockVrs % "test"

  lazy val coreDependencies = Seq(
    typeSafeConfig,
    typeSafeLogging,
    logBackClassic,
    circeParser,
    circeGeneric,
    circeYaml,
    catsCore
  )

  lazy val httpDependencies = Seq(
    sttpCore,
    sttpCirce,
    sttpBackend
  )

  lazy val s3Dependencies = Seq(
    awsSdkS3,
    awsS3Control
  )

  lazy val lambdaHandlerDependencies = Seq(
    awsLambdaJavaEvents,
    awsLambdaJavaCore
  )

  lazy val testDependencies = Seq(
    scalaTest,
    scalaMock
  )

  lazy val testServerDependencies = Seq(
    scalaTest,
    scalaMock,
    serverMock
  )

}

object Dependencies extends Dependencies
