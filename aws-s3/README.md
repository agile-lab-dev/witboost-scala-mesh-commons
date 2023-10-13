# scala-mesh-aws-s3

## Description
A module that provide a gateway to easily interact with S3.
This library has two main gateways: the first allows to interact with S3 buckets/objects, while the second with S3 Batch Operations.

Through the first gateway you can:
* create a folder
* create a file
* retrieve an object given a specific key
* list S3 Objects
* list S3 Object Versions
* list S3 Object Delete Markers

Through the second gateway you can:
* create an S3 Batch Job (either a Copy job or a Invoke Lambda job)
* retrieve the status for a specific job
* run a job

Note: all the list operations handle pagination

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* *s3*: java based sdk to interact with AWS S3
* *s3control*: java based sdk to interact with AWS S3 Batch

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala


## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-aws-s3" % scalaMeshCommonsVersion
 )
```
