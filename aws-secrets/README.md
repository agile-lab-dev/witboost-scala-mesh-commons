# scala-mesh-aws-secrets

## Description
A module that provide a gateway to easily interact with AWS Secrets Manager.
Through the gateway you can:
* retrieve the value for a specific secret, given the associated key

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* *secretsmanager*: java based sdk to interact with AWS Secrets Manager

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-aws-secrets" % scalaMeshCommonsVersion
 )
```
