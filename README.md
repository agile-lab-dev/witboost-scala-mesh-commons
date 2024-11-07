# scala-mesh-commons

[![pipeline status](https://gitlab.com/AgileFactory/Witboost.Mesh/Provisioning/CDP-refresh/witboost.mesh.provisioning.commons/badges/master/pipeline.svg)](https://gitlab.com/AgileFactory/Witboost.Mesh/Provisioning/CDP-refresh/witboost.mesh.provisioning.commons/-/commits/master) [![coverage report](https://gitlab.com/AgileFactory/Witboost.Mesh/Provisioning/CDP-refresh/witboost.mesh.provisioning.commons/badges/master/coverage.svg)](https://gitlab.com/AgileFactory/Witboost.Mesh/Provisioning/CDP-refresh/witboost.mesh.provisioning.commons/-/commits/master)

SBT multi-module project that provide a set of scala_2.13 mesh commons classes, for interacting with Amazon Web Services (AWS) services, Cloudera Data Platform (CDP) Public services, Apache Ranger and common functionalities to implement Scala Witboost Tech Adapters (formerly Specific Provisioners).

<p align="center">
    <a href="https://www.witboost.com">
        <img src="img/witboost_logo.svg" alt="witboost" width=600 >
    </a>
</p>

Designed by [Agile Lab](https://www.agilelab.it/), Witboost is a versatile platform that addresses a wide range of sophisticated data engineering challenges. It enables businesses to discover, enhance, and productize their data, fostering the creation of automated data platforms that adhere to the highest standards of data governance. Want to know more about Witboost? Check it out [here](https://www.witboost.com) or [contact us!](https://witboost.com/contact-us).

- [Overview](#overview)
- [Usage](#usage)
- [Getting started](#getting-started)

## Overview

All Scala Tech Adapters use a number of core functionalities to manage provisioning request, such as parsing JSON or YAML inputs, auditing via logging interfaces, HTTP calls to REST API servers, data mesh functionalities and many others.  This library multi-module library offers some modules for common self-service functionalities. Furthermore, it also provides a common interface to communicate with a set of services through the respective SDKs (specifically AWS services and CDP services).

### Library lifecycle

Project lifecycle is managed through sbt and delivered via CI/CD. CI/CD pipeline is executed internally within described by the `.gitlab-ci.yml` file. The following stages are defined:
1. **setup**: Calculates the release version based on the commit information
2. **check**: Execute a compilation of the file and some format checks.
3. **test**: Run unit test and generate coverage reports that are stored as pipeline artifacts
4. **publish**: Will publish all the modules with artifactory enabled to the package Registry

### Library modules

* [**scala-mesh-core**](./core/README.md): A set of scala_2.13 core utility classes.
* [**scala-mesh-http**](./http/README.md): An abstraction to http functionality. Provide useful method to interact with any http api
* [**scala-mesh-aws-s3**](./aws-s3/README.md): A module that provide a gateway to easily interact with AWS S3 and AWS S3 Batch Operations
* [**scala-mesh-aws-iam**](./aws-iam/README.md): A module that provide a gateway to easily interact with IAM and IAM Policy.
* [**scala-mesh-aws-lambda**](./aws-lambda/README.md): A module that provide a gateway to easily interact with AWS Lambda.
* [**scala-mesh-aws-secrets**](./aws-secrets/README.md): A module that provide a gateway to easily interact with AWS Secrets Manager.
* [**scala-mesh-aws-lambda-handlers**](./aws-lambda-handlers/README.md): A module that provide some lambda handler trait in scala idiomatic way.
* [**scala-mesh-cdp-iam**](./cdp-iam/README.md): A module that provide a gateway to easily interact with CDP Iam functionalities.
* [**scala-mesh-cdp-de**](./cdp-de/README.md): A module that provide a gateway to easily interact with CDP Data Engineering (CDE) experience.
* [**scala-mesh-cdp-dl**](./cdp-dl/README.md): This library provides a gateway to interact with CDP Datalake experience at service level.
* [**scala-mesh-cdp-dw**](./cdp-dw/README.md): This library provides a gateway to interact with CDP DataWarehouse experience at service level.
* [**scala-mesh-cdp-env**](./cdp-env/README.md): This library provides a gateway to interact with CDP Environment experience at service level.
* [**scala-mesh-ranger**](./ranger/README.md): This library provides a gateway to interact with Apache Ranger APIs.
* [**scala-mesh-repository**](./repository/README.md): This library contains a set of scala_2.13 mesh commons classes to interact with repositories.
* [**scala-mesh-self-service**](./self-service/README.md): This library contains a set of scala_2.13 mesh commons classes to create specific provisioners.
* [**scala-mesh-self-service-lambda**](./self-service-lambda/README.md): This library contains a set of scala_2.13 mesh classes implementations to create specific provisioners using AWS Lambda.
* [**scala-mesh-principals-mapping**](./principals-mapping/README.md): This library contains a set of scala_2.13 mesh classes to define a authentication principals mapper trait
* [**scala-mesh-principals-mapping-samples**](./principals-mapping-samples/README.md): This library contains a set of scala_2.13 mesh classes with basic implementations of the principals mapper trait

## Usage

To use the scala-mesh-commons libraries, add the required libraries to the sbt `libraryDependencies`.

```
 libraryDependencies ++= Seq(    
    "com.witboost.provisioning" %% "scala-mesh-core" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-http" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-aws-s3" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-aws-iam" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-aws-lambda" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-aws-secrets" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-aws-lambda-handlers" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-cdp-de" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-cdp-dl" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-cdp-dw" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-cdp-env" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-ranger" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-repository" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-self-service" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-self-service-lambda" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-principals-mapping" % scalaMeshCoreVersion
    "com.witboost.provisioning" %% "scala-mesh-principals-mapping-samples" % scalaMeshCoreVersion
 )
```

## Getting Started

### Local development

* Checkout this repository.
* Compile: `sbt compile`
* Test: `sbt test`
* Generate coverage report: `sbt coverage test coverageAggregate`
* Build: `sbt assembly`
* Before committing, it is possible to execute the provided `ci.sh` script, which will run locally the processes performed on the **check**, **compile**, and **test** stages of the CI pipeline to verify if the pipeline will be successful. 

There is a check against the code coverage. Only commit with more the 90% of coverage will pass the CI phase.

#### Versioning

Versioning is handled using semantic versioning, so new tags must follow the convention `vMAJOR.MINOR.PATCH`. The name of the tag will be used inside the CI/CD pipeline (deploy stage) from sbt to extract the library version.
If not provided, a default version (`0.0.0`) will be used.

**IMPORTANT:** Tag should be created only from release branches. Release branches should be named using the following convention: **release/vMAJOR.MINOR**.


## About Witboost

[Witboost](https://witboost.com/) is a cutting-edge Data Experience platform, that streamlines complex data projects across various platforms, enabling seamless data production and consumption. This unified approach empowers you to fully utilize your data without platform-specific hurdles, fostering smoother collaboration across teams.

It seamlessly blends business-relevant information, data governance processes, and IT delivery, ensuring technically sound data projects aligned with strategic objectives. Witboost facilitates data-driven decision-making while maintaining data security, ethics, and regulatory compliance.

Moreover, Witboost maximizes data potential through automation, freeing resources for strategic initiatives. Apply your data for growth, innovation and competitive advantage.

[Contact us](https://witboost.com/contact-us) or follow us on:

- [LinkedIn](https://www.linkedin.com/showcase/witboost/)
- [YouTube](https://www.youtube.com/@witboost-platform)
