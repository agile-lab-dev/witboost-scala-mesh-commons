# scala-mesh-self-service-lambda

## Description
This library contains a set of scala_2.13 mesh classes implementations to create specific provisioners using AWS Lambda.
The library provides the following main classes:
* **LambdaProvisionerApi**: Lambda support wrapper for `ProvisionerController`. Calls the service lambda and retrieve the service lambda status.
* **AsyncCallLambdaComponentGateway**: Used by LambdaProvisionerApi to invoke the service lambda function asynchronously
* **LambdaComponentGatewayService**: Lambda support wrapper for the `ComponentGateway` trait. Performs the provisioning tasks.
* **LambdaErrorHandler**: Error lambda handler. Called in case of service lambda failures (not handled by the application).

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* [**scala-mesh-self-service**](../self-service): This library contains a set of scala_2.13 mesh commons classes to create specific provisioners.
* [**scala-mesh-aws-lambda**](../aws-lambda): A module that provide a gateway to easily interact with AWS Lambda.
* [**scala-mesh-aws-lambda-handlers**](../aws-lambda-handlers): A module that provide some lambda handler trait in scala idiomatic way.

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-self-service-lambda" % scalaMeshCommonsVersion
 )
```
