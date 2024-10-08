# scala-mesh-cdp-de

`scala-mesh-cdp-de` is a module that provides a gateway to easily interact with CDP Data Engineering (CDE) experience. This library has two main gateways: `CdpDeClient` allows to interact CDE at service level, while `CdeClusterClient` with CDE at cluster level.

Through `CdpDeClient` you can:
* Find all services in CDE
* Find a service by name
* Describe a service by id
* Describe a service by name
* Find all virtual clusters
* Find a virtual cluster by name
* Describe a virtual cluster
* Describe a virtual cluster by name

Through `CdeClusterClient` you can:
* Get a resource 
* Create a resource
* Upload a file
* List jobs
* Get a specific job
* Create a job
* Upsert a job
* List job runs

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* [**scala-mesh-http**](../http): internal core library that provide useful method to interact with any http api
* *cdp-sdk-java*: Java based SDK to interact with CDP

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "com.witboost.provisioning" %% "scala-mesh-cdp-de" % scalaMeshCommonsVersion
 )
```
