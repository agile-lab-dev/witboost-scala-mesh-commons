# Changelog

All notable changes to this project will be documented in this file.

## 1.0.0

### Commits


- **[WIT-1293] Publish commons lib to Maven Central**
  >
  > ##### New features and improvements
  > 
  > * Includes publishing information and step on CI to publish to Sonatype Central on tags
  > * Upgraded sbt version from 1.4.9 to 1.9.8
  > 
  > ##### Breaking changes
  > 
  > * Organization is now `com.witboost.provisioning`. All projects including this library must now migrate to the new correct organization
  > 
  > ##### Migration steps
  > 
  > * Projects adding scala-mesh-commons modules as dependencies must migrate to pull from `com.witboost.provisioning` organization rather than `it.agilelab.provisioning`. The change might look something like:
  > 
  >   - sbt
  >   ```sbt
  >   libraryDependencies ++= "it.agilelab.provisioning" %% "scala-mesh-aws-s3" % "1.0.0"
  >   ```
  >   to
  >   ```sbt
  >   libraryDependencies ++= "com.witboost.provisioning" %% "scala-mesh-aws-s3" % "1.0.0"
  >   ```
  > 
  >   - Maven
  >   ```xml
  >   <dependency>
  >     <groupId>it.agilelab.provisioning</groupId>
  >     <artifactId>scala-mesh-aws-s3_2.13</artifactId>
  >     <version>1.0.0</version>
  >   </dependency>
  >   ```
  >   to
  >   ```xml
  >   <dependency>
  >     <groupId>com.witboost.provisioning</groupId>
  >     <artifactId>scala-mesh-aws-s3_2.13</artifactId>
  >     <version>1.0.0</version>
  >   </dependency>
  >   ```
  > 
  > ##### Related issue
  > 
  > Closes WIT-1293
  > 
  > 
 

- **[WIT-2166] Clean documentation**
  > 
  > ##### New features and improvements
  > 
  > * Prepares the library documentation for release
  > * Introduces debug and trace to the Audit interface
  > 
  > ##### Related issue
  > 
  > Closes WIT-2166
  > 
  > 

- **[WIT-2146] Parsing column tags fails as schema is incorrect**
  > 
  > ##### New features and improvements
  > 
  > * Added data contract schema column tag parsing according to the [OpenMetadata specification](https://docs.open-metadata.org/v1.3.x/main-concepts/metadata-standard/schemas/type/taglabel).
  > 
  > ##### Breaking changes
  > 
  > * Provisioners expecting tags on columns to be a list of strings now will receive a parsing error
  > 
  > ##### Migration
  > 
  > * Descriptors must have the correct schema as per the Data Product specification based on OpenMetadata
  > 
  > ##### Bug fixes
  > 
  > * Fixes a bug where correctly formatted descriptors containing OpenMetadata tags on columns would fail provisioning or validation because of a parsing error.
  > 
  > ##### Related issue
  > 
  > Closes WIT-2146
  > 
  > 

- **[WIT-1545] Scala provisioners Ranger locks on concurrent calls to its DB**
  > 
  > ##### New features and improvements
  > 
  > * Makes the call to Apache Ranger on create/update tasks synchronized to avoid concurrency issues.
  > 
  > ##### Related issue
  > 
  > Closes WIT-1545
  > 
  > 

- **[WIT-1487] scala-mesh commons decodes storage areas as workloads**
  > 
  > ##### New features and improvements
  > 
  > * Improved component decoding to discriminate between component types via the `kind` field rather than structurally as done before
  > 
  > ##### Bug fixes
  > 
  > * Fixed a bug where some storage areas component descriptors which defined a `version` field and no `owners` field would be interpreted as Workloads even if having the correct field `kind: storage`.
  > 
  > ##### Related issue
  > 
  > Closes WIT-1487
  > 
  > 

- **[WIT-1430] scala-mesh commons throws NPE on Ranger connection errors**
  > 
  > ##### New features and improvements
  > 
  > * Added new error to handle decode configuration errors
  > * Added Ranger authentication type enumeration
  > 
  > ##### Breaking changes
  > 
  > * `RangerClient.SIMPLE_AUTH` and `RangerClient.KERBEROS_AUTH` are replaced with the new enumeration `RangerAuthType`
  >  
  > ##### Migration
  > 
  > Replace your usages of `RangerClient.SIMPLE_AUTH` and `RangerClient.KERBEROS_AUTH` with `RangerAuthType.Simple` and `RangerAuthType.Kerberos` respectively.
  > 
  > ##### Bug fixes
  > 
  > * Fixed the occasions where connections errors on Ranger Client requests will throw an unhandled NullPointerException which in turn would return an empty error as a response to the user.
  > 
  > ##### Related issue
  > 
  > Closes WIT-1430
  > 
  > 

- **[WIT-1173] CDE services and clusters are not filtered by status in CDP Spark SP**
  > 
  > ##### Bug fixes
  > 
  > The findAllServices function of CdpCdeClient returns only services that have not been deleted.
  > 
  > Removed passcode field in BearerToken class for hhtp/Auth (not used and cause of bug WIT-994).
  > 
  > Fixed the "how to use it" section in README.md.
  > 
  > 
  > ##### Related issue
  > 
  > Closes WIT-1173
  > Closes WIT-994
  > 
  > 
  > 

- **[WIT-1224] Test the ranger intg package which includes kerberos auth**
  > 
  > ##### New features and improvements
  > 
  > * Adds support to use kerberos authentication by replacing the custom client with the ranger-intg Ranger Client 
  > 
  > ##### Breaking changes
  > 
  > * The host received by the RangerClient.default methods now requires protocol and trailing slash. (Before: `my-ranger-endpoint.cloudera.com/service/ranger`, Now: `https://my-ranger-endpoint.cloudera.com/service/ranger/`)
  > 
  > ##### Related issue
  > 
  > Closes WIT-1224
  > 
  > 

- **[WIT-1081] Commons library several fixes**
  > 
  > ##### New features and improvements
  > 
  > * Modified return type of the ComponentGateway.updateAcl method to return information related to the Update ACL operation rather than the same information of the provision/unprovision operations which is not meaningful to retrieve when performing ACL. On the current implementation, the provisioner will not return any information to the API layer, as this is still not supported by the current Specific Provisioner interface-specification (see WIT-1092).
  > * Added removeData flag support, making it available for ComponentGateway implementations through the ProvisionRequest class.
  > * Added the support to retrieve a Config object based on a key on Conf traits.
  > * Added support for `write` access on Ranger Policy
  > 
  > ##### Breaking changes
  > 
  > * ComponentGateway trait has changed, modifying the return type of the updateAcl method.
  > 
  > ##### Migration
  > 
  > * ComponentGateway implementations now must migrate to the new signature, returning the set of granted principals.
  > * Conf implementations now must implement the getConfig method.
  > 
  > ##### Bug fixes
  > 
  > * Fixes the PrincipalsMapper SPI to adequately expose the services for a ServiceLoader.
  > 
  > ##### Related issue
  > 
  > Closes WIT-1081
  > 
  > 

- **[WIT-969] Implement Update ACL support in commons**
  > 
  > ##### New features and improvements
  > 
  > - Added support for the updateAcl task on the Provisioner class chain, giving support for provisioners that don't need to implement the task.
  > - Added support for CRUD operations on Ranger roles and for access policy items to include roles.
  > - Added support for user query by id and by name on CdpIam client.
  > - Added PrincipalsMapper SPI for mapping between different authentication providers, used by the ProvisionerController on the updateAcl task to map received refs.
  > - Added support devGroup and ownerGroup fields to DataProduct class
  > 
  > ##### Breaking changes
  > 
  > - The ComponentGateway and ProvisionerController traits have changed so projects using these interface should migrate.
  > 
  > ##### Migration
  > 
  > - Migrating ComponetGateway children:
  >   - ComponentGateway now requires to implement the `updateAcl` method. If this is not needed, the PermissionlessComponentGateway trait has been defined
  > - Migrating ProvisionerController:
  >   - ProvisionerController.default has been replaced by ProvisionerController.defaultWithAcl and ProvisionerController.defaultNoAcl. The first one requires an instance of PrincipalsMapper. The second is the legacy ProvisionerController.default method.
  > 
  > ##### Bug fixes
  > 
  > 
  > ##### Related issue
  > 
  > Closes WIT-969
  > 
  > 

- **[WIT-967] Update CDP SDK in scala mesh commons**
  > 
  > Closes WIT-967

- **[WIT-555] Test coverage not shown in MR page**
  > 
  > ##### Bug fixes
  > 
  > Percentage of coverage is now shown on the MR page
  > 
  > ##### Related issue
  > 
  > Closes WIT-555
  > 
  > 

- **[WIT-365] CDP common library cleanup and refactoring**
