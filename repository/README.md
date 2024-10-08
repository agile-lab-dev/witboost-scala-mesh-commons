# scala-mesh-repository

## Description
This library contains a set of scala_2.13 mesh commons classes to interact with repositories.

Through a repository trait you can:
* Find an entity by id
* Find all entities inside a repository (with an optional filter)
* Create an entity
* Delete an entity
* Update an entity

Inside this project there are some implementation of Repository:
* `DataProductRepository`: to handle data products in a generic repository 
* `DomainDynamoDBRepository`: to handle data mesh domains in a DynamoDB table
* `DynamoDBRepository`: generic DynamoDB implementation
* `ProvisionStateDynamoDBRepository`: to handle provisioner requests state in a DynamoDB table
* `RoleDynamoDBRepository`: to handle roles in a DynamoDB table

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* *dynamodb*: Java based SDK to interact with AWS DynamoDB

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "com.witboost.provisioning" %% "scala-mesh-repository" % scalaMeshCommonsVersion
 )
```
