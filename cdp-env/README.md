# scala-mesh-cdp-env

## Description
This library provides a gateway to interact with CDP Environment experience at service level.

Through this gateway you can:
* list the available environments
* describe an environment by name


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
    "it.agilelab.provisioning" %% "scala-mesh-cdp-env" % scalaMeshCommonsVersion
 )
```
