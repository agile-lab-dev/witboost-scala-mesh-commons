# scala-mesh-aws-lambda

`scala-mesh-aws-lambda` is a module that provides a gateway to easily interact with AWS Lambda. Through the gateway you can:

* Asynchronously call a lambda function

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* *lambda*: Java based SDK to interact with AWS Lambda

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "com.witboost.provisioning" %% "scala-mesh-aws-lambda" % scalaMeshCommonsVersion
 )
```
