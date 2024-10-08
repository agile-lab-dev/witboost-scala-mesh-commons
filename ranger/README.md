# scala-mesh-ranger

`scala-mesh-ranger` provides a gateway to interact with Apache Ranger APIs using the [ranger-intg](https://github.com/apache/ranger/tree/master/intg/src/main/java) library provided by Apache Ranger.

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

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* *ranger-intg 2.4.0*: Ranger integration Java client.

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "com.witboost.provisioning" %% "scala-mesh-ranger" % scalaMeshCommonsVersion
 )
```

## Limitations

The methods calls to create or update objects are synchronized so no two parallel calls are done to Apache Ranger, as it's been verified that the current Apache Ranger implementation locks the internal database at application level and concurrent write operations on the same task may fail.