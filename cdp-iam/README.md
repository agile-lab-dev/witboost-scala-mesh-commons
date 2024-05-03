# scala-mesh-cdp-iam

`scala-mesh-cdp-iam` is a module that provides a gateway to easily interact with CDP IAM functionalities.

Through this gateway you can:
* Retrieve a machine user
* Create a machine user
* Set the workload password of a machine user
* Add a machine user to a group
* Check if a machine user belongs to a group
* Retrieve a group
* Create a group
* List the available CDP resource roles
* List the resource roles assignments belonging to a group
* Assign a resource role to group
* Unassign a resource role from a group 

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
    "it.agilelab.provisioning" %% "scala-mesh-cdp-iam" % scalaMeshCommonsVersion
 )
```
