# scala-mesh-aws-lambda

## Description
A module that provide a gateway to easily interact with AWS Lambda.
Through the gateway you can:
* asynchronously call a lambda function

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* *lambda*: java based sdk to interact with AWS Lambda

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-aws-lambda" % scalaMeshCommonsVersion
 )
```
