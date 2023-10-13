# scala-mesh-cdp-de

## Description
A module that provide a gateway to easily interact with Cdp Data Engineering (CDE) experience.
This library has two main gateways: the first allows to interact CDE at service level, while the second with CDE at cluster level.

Through the first gateway you can:
* find all services in CDE
* find a service by name
* describe a service by id
* describe a service by name
* find all virtual clusters
* find a virtual cluster by name
* describe a virtual cluster
* describe a virtual cluster by name

Through the second gateway you can:
* get a resource 
* create a resource
* upload a file
* list jobs
* get a specific job
* create a job
* upsert a job
* list job runs


## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* **scala-mesh-http**: internal core library that provide useful method to interact with any http api
* *cdp-sdk-java*: java based sdk to interact with CDP

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-cdp-de" % scalaMeshCommonsVersion
 )
```
