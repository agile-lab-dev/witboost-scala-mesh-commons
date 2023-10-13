# scala-mesh-repository

## Description
This library contains a set of scala_2.13 mesh commons classes to interact with repositories.

Through a repository trait you can:
* find an entity by id
* find all entities inside a repository (with an optional filter)
* create an entity
* delete an entity
* update an entity

Inside this project there are some implementation of Repository:
* DataProductRepository: to handle data products in a generic repository 
* DomainDynamoDBRepository: to handle data mesh domains in a DynamoDB table
* DynamoDBRepository: generic DynamoDB implementation
* ProvisionStateDynamoDBRepository: to handle provisioner requests state in a DynamoDB table
* RoleDynamoDBRepository: to handle mesh roles in a DynamoDB table


## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* *dynamodb*: java based sdk to interact with AWS DynamoDB

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-repository" % scalaMeshCommonsVersion
 )
```
