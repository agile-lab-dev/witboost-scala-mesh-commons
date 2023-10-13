# scala-mesh-http

## Description
An abstraction to http functionality. Provide useful method to interact with any http api

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* **scala-mesh-core**: internal core library that provide useful stuff, parsing, cats extension and so on.
* sttpClient3-core,sttpClient3-circe,sttpClient3-okhttp-backend: for managing http request response 

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala
* mockserver: framework to setup a rest server to local test http request and resposne

## How to use it

SBT Dependencies reference:

```
 libraryDependencies ++= Seq(
    "it.agilelab.provisioning" %% "scala-mesh-http" % scalaMeshCommonsVersion
 )
```

### Http

An abstraction layer that will avoid all boilerplate of code to manage http calls
```scala
case class MyApiModel(id:String,name:String,desc:String)

val http = Http.default()
//Use Http.defaultWithAudit() to enable logging on all http process
//val http = Http.defaultWithAudit()

//with empty headers and no auth method
http.get[MyApiModel]("my-endpoint",Map.empty,NoAuth()) match {
  case Right(r) => prinln(s"this is my model $r")
  case Left(ConnectionErr(details)) => prinln(s"Ops connection error")
  case Left(ClientErr(code,message)) => prinln(s"it's a client error")
  case Left(ServerErr(code,message)) => prinln(s"it's a server error")
  case Left(GenericErr(code,message)) => prinln(s"it's a generic error")
  case Left(UnexpectedBodyErr(b,parserErr)) => prinln(s"Unexpected body $b raise this parserError $parserErr")
}

//with empty headers and Basic Auth Method
http.get[MyApiModel]("my-endpoint",Map.empty,BasicCredential("usr","pwd")) match {
  case Right(r) => prinln(s"this is my model $r")
  case Left(ConnectionErr(details)) => prinln(s"Ops connection error")
  case Left(ClientErr(code,message)) => prinln(s"it's a client error")
  case Left(ServerErr(code,message)) => prinln(s"it's a server error")
  case Left(GenericErr(code,message)) => prinln(s"it's a generic error")
  case Left(UnexpectedBodyErr(b,parserErr)) => prinln(s"Unexpected body $b raise this parserError $parserErr")
}

//with empty headers and Basic Auth Method
http.get[MyApiModel]("my-endpoint",Map("Content-Type"->"application/json"),BasicCredential("usr","pwd")) match {
  case Right(r) => prinln(s"this is my model $r")
  case Left(ConnectionErr(details)) => prinln(s"Ops connection error")
  case Left(ClientErr(code,message)) => prinln(s"it's a client error")
  case Left(ServerErr(code,message)) => prinln(s"it's a server error")
  case Left(GenericErr(code,message)) => prinln(s"it's a generic error")
  case Left(UnexpectedBodyErr(b,parserErr)) => prinln(s"Unexpected body $b raise this parserError $parserErr")
}
```

Check scala docs for other methods details.
