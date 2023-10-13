# scala-mesh-self-service-lambda

## Description
This library contains a set of scala_2.13 mesh classes implementations to create specific provisioners using AWS Lambda.
The library provides the following main classes:
* **LambdaProvisionerDriverHandler**: driver lambda - call the service lambda and retrieve the service lambda status
* **LambdaAsyncWorkerInvoker**: used by driver lambda to invoke the service lambda function asynchronously
* **LambdaProvisionerServiceHandler**: service lambda - perform the provisioning
* **LambdaProvisionerErrorHandler**: error lambda - called in case of service lambda failures (not handled by the application)


## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* **scala-mesh-self-service**: This library contains a set of scala_2.13 mesh commons classes to create specific provisioners.
* **scala-mesh-aws-lambda**: A module that provide a gateway to easily interact with AWS Lambda.
* **scala-mesh-aws-lambda-handlers**: A module that provide some lambda handler trait in scala idiomatic way.

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-self-service-lambda" % scalaMeshCommonsVersion
 )
```
