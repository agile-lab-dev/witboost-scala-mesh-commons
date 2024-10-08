# scala-mesh-self-service

## Description
This library contains a set of scala_2.13 mesh commons classes to create specific provisioners.
The library provides the following main classes/traits:
* `ProvisionerController`: trait which defines the main interface of a specific provisioner. The controller is in charge of starting the provisioner process by decoding the incoming request and calling the configured `Provisioner` and `ValidatorHandler` interfaces to follow the validate/provisioning/unprovisioning/updateacl task workflows, as well as retrieving the current state of asynchronous /v2 provisioner requests.
* `Provisioner`: trait which defines the main logic to execute a specific provisioner process. It works using the following traits:
  * `ComponentGateway`: a resource gateway is the component in charge of creating specific provisioner resources.
    This is the interface you need to implement on your specific provisioner to provide the business logic.
  * `ProvisionStateHandler`: trait which defines two main operations (get and upsert) to interact with the underlying repository that will handle the state for asynchronous /v2 provisioner requests.

For each trait there are also the respective basic implementations.

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* [**scala-mesh-repository**](../repository): This library contains a set of scala_2.13 mesh commons classes to interact with repositories.
* [**scala-mesh-principals-mapping**](../principals-mapping): library with the trait definition of the mapper interface
* [**scala-mesh-principals-mapping-samples**](../principals-mapping-samples): This library contains a set of scala_2.13 mesh classes with basic implementations of the principals mapper trait

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "com.witboost.provisioning" %% "scala-mesh-self-service" % scalaMeshCommonsVersion
 )
```

### Microservice integration

When developing a tech adapter based on a microservice architecture, follow this steps to configure the Provisioner to work with this tech stack:

1. Implement the `ComponentGateway` (and the `ProvisionStateHandler` if you need to support /v2 asynchronous tasks) with your business logic. For components that don't support ACL tasks, we provide the `PermissionlessComponentGateway`.
2. Implement the `it.agilelab.provisioning.commons.validator.Validator` for descriptor validation. 
3. Configure the needed `PrincipalsMapper` for the mapping Witboost identities to the service principals (either one provided by the `principals-mapping-samples` library or a custom-implemented one).
4. Instantiate a `Provisioner` using `Provisioner.defaultSync` or `Provisioner.defaultAsync` method, hooking it up with your business logic.
5. Instantiate a `it.agilelab.provisioning.mesh.repository.Repository` class to be used to track state when necessary.
6. Instantiate the `ProvisionerController` using the `ProvisionerController.defaultXXX` and passing the already created methods.

The `ProvisionerController` will be the ingress point to leverage all the functionalities of the stack, receiving the string descriptor and taking care of:

- Parsing and decoding inputs given the appropriate implicit decoder for the `specific` field for both the Data Product and the managed component.
- Takes care of the "validation before provisioning" workflow.
- Takes care of retrieving the asynchronous tasks states.
