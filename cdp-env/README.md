# scala-mesh-cdp-env

`scala-mesh-cdp-env` provides a gateway to interact with CDP Environment experience at service level.

Through this gateway you can:
* List the available environments
* Describe an environment by name

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* *cdp-sdk-java*: Java based SDK to interact with CDP

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-cdp-env" % scalaMeshCommonsVersion
 )
```
