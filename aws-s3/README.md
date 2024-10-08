# scala-mesh-aws-s3

`scala-mesh-aws-s3` is a module that provides a gateway to easily interact with S3. This library has two main gateways: `S3Gateway` allows to interact with S3 buckets/objects, while `S3BatchOperationsGateway` with S3 Batch Operations.

Through `S3Gateway` you can:
* Create a folder
* Create a file
* Check for the existence of an object
* Retrieve an object given a specific key
* List S3 Objects
* List S3 Object Versions
* List S3 Object Delete Markers

Through `S3BatchOperationsGateway` you can:
* Create an S3 Batch Job (either a Copy job or an Invoke Lambda job)
* Retrieve the status for a specific job
* Run a job

Note: All the list operations handle pagination

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* [**scala-mesh-core**](../core): scala-mesh-commons core library which provides parsing functionalities, Scala cats extension among others.
* *s3*: Java based SDK to interact with AWS S3
* *s3control*: Java based SDK to interact with AWS S3 Batch

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "com.witboost.provisioning" %% "scala-mesh-aws-s3" % scalaMeshCommonsVersion
 )
```
