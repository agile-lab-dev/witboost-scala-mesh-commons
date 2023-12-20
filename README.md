# scala-mesh-commons

[![pipeline status](https://gitlab.com/AgileFactory/Witboost.Mesh/Provisioning/CDP-refresh/witboost.mesh.provisioning.commons/badges/master/pipeline.svg)](https://gitlab.com/AgileFactory/Witboost.Mesh/Provisioning/CDP-refresh/witboost.mesh.provisioning.commons/-/commits/master) [![coverage report](https://gitlab.com/AgileFactory/Witboost.Mesh/Provisioning/CDP-refresh/witboost.mesh.provisioning.commons/badges/master/coverage.svg)](https://gitlab.com/AgileFactory/Witboost.Mesh/Provisioning/CDP-refresh/witboost.mesh.provisioning.commons/-/commits/master)

SBT multi-module project that provide a set of scala_2.13 mesh commons classes

## Description
In our scala microservices we use a number of core functionalities such as parsing a json or a yaml, auditing via logging interface, http calls to rest api, data mesh functionalities and many others.
We also use a set of services through the respective SDKs (like AWS services and CDP services)
This repository aims to provide all this core functionalities so that we don't repeat this logic within each specific provisioner.

### Library Life cycle
Project life cycle is managed through SBT and the library are delivered, after all CI/CD stage works fine to the internal Package Registry that allow us to checkout this dependency from any project that require this functionality).

CI/CD pipeline are executed within GitlabRunner and described on the `.gitlab-ci.yml`; inside the latter you will find the following stages:
1. **setup**: Calculates the release version based on the commit information
2. **check**: Execute a compilation of the file and some format checks.
3. **test**: Run unit test and generate coverage reports that are stored as pipeline artifacts
4. **publish**: Will publish all the modules with artifactory enabled to the package Registry

**NB**: There is a check against the code coverage. Only commit with more the 90% of coverage will pass the CI phase.

### Library modules
* [**scala-mesh-core**](./core/README.md): A set of scala_2.13 core utility classes.
* [**scala-mesh-http**](./http/README.md): An abstraction to http functionality. Provide useful method to interact with any http api
* [**scala-mesh-aws-s3**](./aws-s3/README.md): A module that provide a gateway to easily interact with AWS S3 and AWS S3 Batch Operations
* [**scala-mesh-aws-iam**](./aws-iam/README.md): A module that provide a gateway to easily interact with IAM and IAM Policy.
* [**scala-mesh-aws-lambda**](./aws-lambda/README.md): A module that provide a gateway to easily interact with AWS Lambda.
* [**scala-mesh-aws-secrets**](./aws-secrets/README.md): A module that provide a gateway to easily interact with AWS Secrets Manager.
* [**scala-mesh-aws-lambda-handlers**](./aws-lambda-handlers/README.md): A module that provide some lambda handler trait in scala idiomatic way.
* [**scala-mesh-cdp-iam**](./cdp-iam/README.md): A module that provide a gateway to easily interact with Cdp Iam functionalities.
* [**scala-mesh-cdp-de**](./cdp-de/README.md): A module that provide a gateway to easily interact with Cdp Data Engineering (CDE) experience.
* [**scala-mesh-cdp-dl**](./cdp-dl/README.md): This library provides a gateway to interact with CDP Datalake experience at service level.
* [**scala-mesh-cdp-dw**](./cdp-dw/README.md): This library provides a gateway to interact with CDP DataWarehouse experience at service level.
* [**scala-mesh-cdp-env**](./cdp-env/README.md): This library provides a gateway to interact with CDP Environment experience at service level.
* [**scala-mesh-ranger**](./ranger/README.md): This library provides a gateway to interact with Apache Ranger APIs.
* [**scala-mesh-repository**](./repository/README.md): This library contains a set of scala_2.13 mesh commons classes to interact with repositories.
* [**scala-mesh-self-service**](./self-service/README.md): This library contains a set of scala_2.13 mesh commons classes to create specific provisioners.
* [**scala-mesh-self-service-lambda**](./self-service-lambda/README.md): This library contains a set of scala_2.13 mesh classes implementations to create specific provisioners using AWS Lambda.
* [**scala-mesh-principals-mapping**](./principals-mapping/README.md): This library contains a set of scala_2.13 mesh
  classes to define a authentication principals mapper trait
* [**scala-mesh-principals-mapping-samples**](./principals-mapping-samples/README.md): This library contains a set of
  scala_2.13 mesh classes with basic implementations of the principals mapper trait

## Getting Started

### Local works

* Checkout the repository `https://gitlab.com/AgileFactory/Witboost.Mesh/Provisioning/CDP-refresh/witboost.mesh.provisioning.commons.git`
* Compile: `sbt compile`
* Test: `sbt test`
* Generate coverage report: `sbt coverage test coverageAggregate`
* Build: `sbt assembly`
* All in one: to avoid issue and wrong commit before commit try the `ci.sh` script, will execute all the ci phase

### Contribution

There are no strict rules on how to work on the library. 
By the way we are trying to follow a specific lifecycle to keep library safe and to avoid releasing issue.
1. clone the repository
2. create a new issue that describes your feature
3. create a merge request from the issue and the associated branch (prefix it with `feature/`)
4. fetch `git fetch origin` and checkout the branch `git checkout feature/my-awesome-feature`
5. add your work to the branch created
6. commit and push your work on the feature branch
7. once you have completed your work, put your Merge Request on ready and ask for a review
8. once your changed have been approved you can merge the feature on develop
9. create a new release branch or merge into the existing release branch to effectively release the package

**NB**: Before commit execute the `ci.sh` script, that will execute the **check**, **compile**, and **test** stage of the ci on your local machine


#### Versioning

When creating a new tag use the following convention: **vMAJOR.MINOR.PATCH**
The name of the tag will be used inside the CI/CD pipeline (deploy stage) from sbt to extract the library version.
If your branch name is not correct, a default version (*0.0.0*) will be used.

**IMPORTANT**
Tag should be created only from release branches. Release branches should be named using the following convention: **release/vMAJOR.MINOR**

## how to use it

SBT Dependencies reference
```
 libraryDependencies ++= Seq(    
    "it.agilelab.provisioning" %% "scala-mesh-core" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-http" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-aws-s3" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-aws-iam" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-aws-lambda" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-aws-secrets" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-aws-lambda-handlers" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-cdp-de" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-cdp-dl" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-cdp-dw" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-cdp-env" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-ranger" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-repository" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-self-service" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-self-service-lambda" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-self-principals-mapping" % scalaMeshCoreVersion
    "it.agilelab.provisioning" %% "scala-mesh-self-principals-mapping-samples" % scalaMeshCoreVersion
 )
```
