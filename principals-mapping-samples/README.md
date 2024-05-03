# scala-mesh-principals-mapping-samples

## Description

This library provides a set of basic implementations of the PrincipalsMapper trait. This library is defined as a Service Provider Interface (SPI).

- `IdentityPrincipalsMapper`: Mapping from String subjects to CdpIamUser without performing any extra operations.
- `FreeIpaIdentityPrincipalsMapper`: Mapping from String subjects to either CdpIamUser or CdpIamGroup based on prefix (`user:<workloadUsername>_<domain>.<ext>` for users and `group:<groupName>` for groups) and validating the existence of said principal against the FreeIPA instance of the CDP instance.

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* [**scala-mesh-principals-mapping**](../principals-mapping): library with the trait definition of the mapper interface


Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-principals-mapping-samples" % scalaMeshCommonsVersion
 )
```
