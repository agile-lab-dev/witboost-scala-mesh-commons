# scala-mesh-core

## Description
A set of scala_2.13 core utility classes.

## Dependencies

Project Dependencies:

* **sbt** installed on your machine

Production code dependency:

* *typesafe-config*: Used for functional style on configuration
* *circe-parser*, *circe-generic*, *circe-yaml*: Used for serializing and deserializing objects into and from json/yml
* *cats-core*: Used for functional programming effects
* *scala-logging*, *logback-classic*: Used as logging system

Test code dependency:

* scalatest: framework for unittest in scala
* scalamock: framework for mock and stub in scala

## Usage

Add the library to your sbt `libraryDependencies`:

```
 libraryDependencies ++= Seq(
    "com.witboost.provisioning" %% "scala-mesh-core" % scalaMeshCommonsVersion
 )
```

### Audit

A simple facade to Logging stuff
```scala
val audit = Audit.default("my-logger-name")
audit.info("my info message")
audit.error("my error message")
audit.warning("my warning message")
audit.debug("my debug message")
audit.trace("my trace message")
```

### Conf
A simple facade Configuration.
`Conf.get` method return an `Either[ConfErr,String]` to manage side effect like configuration not found.

```scala
case class MyCredential(username:String, password:String)
val conf = Conf.env()
//Use Conf.envWithAudit() to enable logging on all Conf process
//val conf = Conf.envWithAudit()
val myCredential: Either[ConfError, MyCredential] = for {
  username <- conf.get("USR")
  password <- conf.get("PWD")
} yield MyCredential(username,password)
```

### DateTimeProvider

A simple facade to DateTime
```scala
val dateTimeProvider = DateTimeProvider.utc()
val dateTime = dateTimeProvider.get()
```

### IDGenerator

```scala 
val idGenerator = IDGenerator.uuid()
val id1 = idGenerator.random()
val id2 = idGenerator.randomFromStr("my-seed-str")
```

### ShowableOps

Implicit definition of Throwable show method, which can be used to easily print a throwable.
The show definition will include the throwable stack trace  (differently from the toString method).

```scala 

val throwable = new Exception("message")
val throwableStringWithStackTrace = show"$throwable"
```


### ParserSupport

```scala
case class MyModel(id:String, name:String, description:String)
class MyHighLevelService extends MyServiceTrait with ParserSupport{
  def myMethod(myModel:MyModel): String =
    toJson(myModel)
}
```

### Validator

```scala
case class MyModel(id:Int, name:String, description:String)

val validator:Validator[MyModel] = 
  Validator[MyModel]
    .rule(_.id>0, "Id should be gt 0")
    .rule(_.name.length > 4, "name should be gt 4 chars")

validator.validate(MyModel(-1,"x","y"))
```

