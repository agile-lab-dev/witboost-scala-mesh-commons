# scala-mesh-aws-secrets

`scala-mesh-aws-secrets` is a module that provides a gateway to easily interact with AWS Secrets Manager. Through the gateway you can:

* Retrieve the value for a specific secret, given the associated key

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* *secretsmanager*: Java based SDK to interact with AWS Secrets Manager

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "com.witboost.provisioning" %% "scala-mesh-aws-secrets" % scalaMeshCommonsVersion
 )
```
