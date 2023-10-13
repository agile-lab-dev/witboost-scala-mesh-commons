# scala-mesh-cdp-dw

## Description
This library provides a gateway to interact with CDP DataWarehouse experience at service level.

Through this gateway you can:
* find all clusters
* find a cluster by environment crn
* find all virtual warehouses
* find a virtual by name


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
    "it.agilelab.provisioning" %% "scala-mesh-cdp-dw" % scalaMeshCommonsVersion
 )
```
