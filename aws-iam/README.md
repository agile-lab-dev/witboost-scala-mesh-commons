# scala-mesh-aws-iam

`scala-mesh-aws-iam` is a module that provides a gateway to easily interact with AWS IAM and IAM Policies. Through the gateway you can:

* Create a policy for a specific role
* Delete a policy for a specific role
* Check if a specific policy exists for a specific role

Moreover, the library provides IAM policy model classes to convert policies in JSON-formatted strings.

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* *iam*: Java based SDK to interact with AWS IAM

Test code dependency:

* **scalatest**: Framework for unittest in scala
* **scalamock**: Framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "com.witboost.provisioning" %% "scala-mesh-aws-iam" % scalaMeshCommonsVersion
 )
```
