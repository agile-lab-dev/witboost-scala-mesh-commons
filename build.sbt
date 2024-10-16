import Settings._

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-commons",
    publish / skip := true
  )
  .disablePlugins(AssemblyPlugin)
  .aggregate(
    core,
    http,
    awsS3,
    awsIam,
    awsLambda,
    awsSecrets,
    awsLambdaHandlers,
    cdpIam,
    cdpDe,
    cdpDl,
    cdpDw,
    cdpEnv,
    ranger,
    repository,
    selfService,
    selfServiceLambda,
    principalsMapping,
    principalsMappingSamples
  )

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-core",
    libraryDependencies ++= Dependencies.coreDependencies ++ Dependencies.testDependencies,
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)

lazy val http = (project in file("http"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-http",
    libraryDependencies ++= Dependencies.httpDependencies ++ Dependencies.testServerDependencies,
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val awsS3 = (project in file("aws-s3"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-aws-s3",
    libraryDependencies ++= Dependencies.testDependencies ++ Dependencies.s3Dependencies,
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val awsIam = (project in file("aws-iam"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-aws-iam",
    libraryDependencies ++= Dependencies.testDependencies ++ Seq(
      Dependencies.awsSdkIam
    ),
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val awsLambda = (project in file("aws-lambda"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-aws-lambda",
    libraryDependencies ++= Dependencies.testDependencies ++ Seq(
      Dependencies.awsSdkLambda
    ),
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val awsSecrets = (project in file("aws-secrets"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-aws-secrets",
    libraryDependencies ++= Dependencies.testDependencies ++ Seq(
      Dependencies.awsSdkSecrets
    ),
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val awsLambdaHandlers = (project in file("aws-lambda-handlers"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-aws-lambda-handlers",
    libraryDependencies ++= Dependencies.testDependencies ++ Dependencies.lambdaHandlerDependencies,
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val cdpDe = (project in file("cdp-de"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-cdp-de",
    libraryDependencies ++= Dependencies.testDependencies ++ Seq(
      Dependencies.cdpSdk
    ),
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core, http)

lazy val cdpDl = (project in file("cdp-dl"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-cdp-dl",
    libraryDependencies ++= Dependencies.testDependencies ++ Seq(
      Dependencies.cdpSdk
    ),
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val cdpDw = (project in file("cdp-dw"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-cdp-dw",
    libraryDependencies ++= Dependencies.testDependencies ++ Seq(
      Dependencies.cdpSdk
    ),
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val cdpEnv = (project in file("cdp-env"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-cdp-env",
    libraryDependencies ++= Dependencies.testDependencies ++ Seq(
      Dependencies.cdpSdk
    ),
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val cdpIam = (project in file("cdp-iam"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-cdp-iam",
    libraryDependencies ++= Dependencies.testDependencies ++ Seq(
      Dependencies.cdpSdk
    ),
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val ranger = (project in file("ranger"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-ranger",
    libraryDependencies ++= Dependencies.testDependencies ++ Seq(
      Dependencies.rangerIntegration
    ),
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core, http)

lazy val repository = (project in file("repository"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-repository",
    libraryDependencies ++= Dependencies.testDependencies ++ Seq(
      Dependencies.awsSdkDynamo
    ),
    wartremoverSettings,
    commonAssemblySettings
  )
  .disablePlugins(AssemblyPlugin)
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val selfService = (project in file("self-service"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-self-service",
    libraryDependencies ++= Dependencies.testDependencies,
    wartremoverSettings,
    commonAssemblySettings
  )
  .dependsOn(core, repository, principalsMapping, principalsMappingSamples)
  .enablePlugins(K8tyGitlabPlugin)

lazy val selfServiceLambda = (project in file("self-service-lambda"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-self-service-lambda",
    libraryDependencies ++= Dependencies.testDependencies,
    wartremoverSettings,
    commonAssemblySettings
  )
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core, awsLambda, awsLambdaHandlers, selfService)

lazy val principalsMapping = (project in file("principals-mapping"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-principals-mapping",
    libraryDependencies ++= Dependencies.testDependencies,
    wartremoverSettings,
    commonAssemblySettings
  )
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core)

lazy val principalsMappingSamples = (project in file("principals-mapping-samples"))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-mesh-principals-mapping-samples",
    libraryDependencies ++= Dependencies.testDependencies,
    wartremoverSettings,
    commonAssemblySettings
  )
  .enablePlugins(K8tyGitlabPlugin)
  .dependsOn(core, principalsMapping, cdpIam)
