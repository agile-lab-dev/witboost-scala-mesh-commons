# scala-mesh-self-service

## Description
This library contains a set of scala_2.13 mesh commons classes to create specific provisioners.
The library provides the following main classes/traits:
* **ProvisionerDriver**: trait which defines the driver component of a specific provisioner.
  The driver component of a specific provisioner is in charge of start the provisioner process and retrieve the state of it.
* **WorkerInvoker**: trait which defines a worker invoker. A worker invoker is a component used by the *ProvisionerDriver* to 
  asynchronously invoke the specific provisioner.
* **ProvisionerWorker**: trait which defines the main logic to execute a specific provisioner process. It works using the following traits:
  * **ResourceGateway**: a resource gateway is the component in charge of creating specific provisioner resources.
    It will be extended by specific provisioners to provide their creation logic
  * **ProvisionStateHandler**: trait which defines two main operations (get and upsert) to interact with the underlying repository
    that will handle the state of the provisioner requests.

For each trait there are also the respective implementations.

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* **scala-mesh-repository**: This library contains a set of scala_2.13 mesh commons classes to interact with repositories.
* **scala-mesh-principals-mapping**: library with the trait definition of the mapper interface
* **scala-mesh-principals-mapping-samples** This library contains a set of scala_2.13 mesh classes with basic implementations of the principals mapper trait

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-self-service" % scalaMeshCommonsVersion
 )
```
