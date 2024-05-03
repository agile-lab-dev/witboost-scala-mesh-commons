# scala-mesh-principals-mapping

`scala-mesh-principals-mapping` provides a trait definition to map principals between authentication providers

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-principals-mapping" % scalaMeshCommonsVersion
 )
```
