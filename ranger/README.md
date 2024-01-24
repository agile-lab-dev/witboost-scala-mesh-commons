# scala-mesh-ranger

## Description

This library provides a gateway to interact with Apache Ranger APIs using the [ranger-intg](https://github.com/apache/ranger/tree/master/intg/src/main/java) library provided by Apache Ranger.

Through this gateway you can:
* Find a policy by id
* Find a policy by name
* Create a policy
* Update a policy
* Find a security zone by name
* Update a security zone
* Create a security zone
* Find all services
* Find a role by id
* Find a role by name
* Create a role
* Update a role

It supports both Basic authentication with username and password, and Kerberos authentication with principal and keytab file.

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **ranger-intg 2.4.0**: Ranger integration Java client.
* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* **scala-mesh-http**: internal core library that provide useful method to interact with any http api

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-ranger" % scalaMeshCommonsVersion
 )
```
