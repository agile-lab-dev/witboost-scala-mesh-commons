# scala-mesh-cdp-iam

## Description
A module that provide a gateway to easily interact with Cdp Iam functionalities.

Through this gateway you can:
* retrieve a machine user
* create a machine user
* set the workload password of a machine user
* add a machine user to a group
* check if a machine user belongs to a group
* retrieve a group
* create a group
* list the available CDP resource roles
* list the resource roles assignments belonging to a group
* assign a resource role to group
* unassing a resource role from a group 


## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* *cdp-sdk-java*: java based sdk to interact with CDP

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-cdp-iam" % scalaMeshCommonsVersion
 )
```
