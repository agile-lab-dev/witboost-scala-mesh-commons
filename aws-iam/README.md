# scala-mesh-aws-iam

## Description
A module that provide a gateway to easily interact with IAM and IAM Policy.
Through the gateway you can:
* create a policy for a specific role
* delete a policy for a specific role
* check if a specific policy exists for a specific role

Moreover, the library provides IAM policy model classes to convert policies in JSON-formatted strings.

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* *iam*: java based sdk to interact with AWS IAM

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-aws-iam" % scalaMeshCommonsVersion
 )
```
