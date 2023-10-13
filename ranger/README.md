# scala-mesh-ranger

## Description
This library provides a gateway to interact with Apache Ranger APIs.

Through this gateway you can:
* find a policy by id
* find a policy by name
* create a policy
* update a policy
* find a security zone by name
* update a security zone
* create a security zone
* find all services


## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* **scala-mesh-http**: internal core library that provide useful method to interact with any http api

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-ranger" % scalaMeshCommonsVersion
 )
```
