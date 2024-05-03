# scala-mesh-aws-lambda-handlers

`scala-mesh-aws-lambda-handlers` is a module that provides some lambda handler trait in scala idiomatic way. Through the gateway you can:

* Create a lambda that handles StreamEvent (Read inputStream to a String, then call the handle method and write the output to the OutputStream)
* Create a Lambda RequestHandler based on ApiGatewayProxy integration (to call a lambda through AWS ApiGateway)

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* *aws-lambda-java-events*: Java based SDK that provide all lambda Java events
* *aws-lambda-java-core*: Java based SDK that provide all lambda interfaces

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-aws-lambda-handlers" % scalaMeshCommonsVersion
 )
```
