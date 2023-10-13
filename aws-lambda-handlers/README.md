# scala-mesh-aws-lambda-handlers

## Description
A module that provide some lambda handler trait in scala idiomatic way.
Through the gateway you can:
* create a lambda that handle StreamEvent (Read inputStream to a String, then call the handle method and write the output to the OutputStream)
* create a Lambda RequestHandler based on ApiGatewayProxy integration (to call a lambda through AWS ApiGateway)

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* *aws-lambda-java-events*: java based sdk that provide all lambda java events
* *aws-lambda-java-core*: java based sdk that provide all lambda interfaces

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-aws-lambda-handlers" % scalaMeshCommonsVersion
 )
```
