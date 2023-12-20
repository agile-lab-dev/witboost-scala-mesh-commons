package it.agilelab.provisioning.mesh.self.service.api.model

import it.agilelab.provisioning.mesh.self.service.api.model.Component.{ DataContract, OutputPort, Workload }
import it.agilelab.provisioning.mesh.self.service.api.model.openmetadata.Column
import it.agilelab.provisioning.mesh.self.service.api.model.openmetadata.ColumnConstraint.{ NOT_NULL, PRIMARY_KEY }
import it.agilelab.provisioning.mesh.self.service.api.model.openmetadata.ColumnDataType.{ BIGINT, INT }
import io.circe.Json
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.support.ParserSupport
import org.scalatest.funsuite.AnyFunSuite

class ProvisionRequestTest extends AnyFunSuite with ParserSupport {

  test("decode ProvisionRequest with Workload component") {
    val actual   = fromYml[ProvisionRequest[String, String]](
      """
        |dataProduct:
        |   id: id
        |   name: name
        |   fullyQualifiedName: fullyQualifiedName
        |   domain: domain
        |   description: description
        |   environment: environment
        |   version: version
        |   kind: kind
        |   dataProductOwner: dataProductOwner
        |   devGroup: dev-group
        |   ownerGroup: owner-group
        |   dataProductOwnerDisplayName: dataProductOwnerDisplayName
        |   email: email
        |   informationSLA: informationSLA
        |   status: status
        |   maturity: maturity
        |   billing: {}
        |   tags: []
        |   specific: specific
        |   components: []
        |component:
        |   id: id
        |   name: name
        |   fullyQualifiedName: fullyQualifiedName
        |   description: description
        |   kind: kind
        |   workloadType: workloadType
        |   connectionType: connectionType
        |   technology: technology
        |   platform: platform
        |   version: version
        |   infrastructureTemplateId: infrastructureTemplateId
        |   useCaseTemplateId: useCaseTemplateId
        |   tags: []
        |   readsFrom: []
        |   specific: specific
        |""".stripMargin
    )
    val expected = Right(
      ProvisionRequest(
        DataProduct[String](
          id = "id",
          name = "name",
          domain = "domain",
          environment = "environment",
          version = "version",
          dataProductOwner = "dataProductOwner",
          devGroup = "dev-group",
          ownerGroup = "owner-group",
          specific = "specific",
          components = Seq.empty[Json]
        ),
        Some(
          Workload[String](
            id = "id",
            name = "name",
            description = "description",
            version = "version",
            specific = "specific"
          )
        )
      )
    )

    assert(actual == expected)
  }

  test("decode ProvisionRequest with OutputPort component") {
    val actual   = fromYml[ProvisionRequest[String, String]](
      """
        |dataProduct:
        |   id: id
        |   name: name
        |   fullyQualifiedName: fullyQualifiedName
        |   domain: domain
        |   description: description
        |   environment: environment
        |   version: version
        |   kind: kind
        |   dataProductOwner: dataProductOwner
        |   devGroup: dev-group
        |   ownerGroup: owner-group
        |   dataProductOwnerDisplayName: dataProductOwnerDisplayName
        |   email: email
        |   informationSLA: informationSLA
        |   status: status
        |   maturity: maturity
        |   billing: {}
        |   tags: []
        |   specific: specific
        |   components: []
        |component:
        |   id: id
        |   name: name
        |   fullyQualifiedName: fullyQualifiedName
        |   description: description
        |   kind: kind
        |   version: version
        |   infrastructureTemplateId: infrastructureTemplateId
        |   useCaseTemplateId: useCaseTemplateId
        |   dependsOn: []
        |   platform: platform
        |   technology: technology
        |   outputPortType: outputPortType
        |   creationDate: creationDate
        |   startDate: startDate
        |   processDescription: processDescription
        |   dataContract:
        |      schema:
        |        - name: column1
        |          dataType: INT
        |          constraint: PRIMARY_KEY
        |        - name: column2
        |          dataType: BIGINT
        |          constraint: NOT_NULL
        |      SLA:
        |         intervalOfChange: 1 hours
        |         timeliness: 1 minutes
        |         upTime: 99.9%
        |      termsAndConditions: only usable in development environment
        |      endpoint: https://myurl/development/my_domain/my_data_product/1.0.0/my_raw_s3_port
        |   dataSharingAgreements:
        |      purpose: purpose
        |      billing: billing
        |      security: security
        |      intendedUsage: intendedUsage
        |      limitations: limitations
        |      lifeCycle: lifeCycle
        |      confidentiality: confidentiality
        |   tags: []
        |   sampleData:
        |     columns:
        |       - col1
        |       - col2
        |       - col3
        |     rows:
        |       -
        |         - r1c1
        |         - r1c2
        |         - r1c3
        |       -
        |         - r2c1
        |         - r2c2
        |         - r2c3
        |   semanticLinking: []
        |   specific: specific
        |""".stripMargin
    )
    val expected = Right(
      ProvisionRequest(
        DataProduct[String](
          id = "id",
          name = "name",
          domain = "domain",
          environment = "environment",
          version = "version",
          dataProductOwner = "dataProductOwner",
          devGroup = "dev-group",
          ownerGroup = "owner-group",
          specific = "specific",
          components = Seq.empty[Json]
        ),
        Some(
          OutputPort[String](
            id = "id",
            name = "name",
            version = "version",
            description = "description",
            dataContract = DataContract(
              schema = Seq(
                Column("column1", INT, None, None, None, None, None, None, Some(PRIMARY_KEY), None, None, None),
                Column("column2", BIGINT, None, None, None, None, None, None, Some(NOT_NULL), None, None, None)
              )
            ),
            specific = "specific"
          )
        )
      )
    )
    assert(actual == expected)

  }

  test("decode ProvisionRequest without component") {
    val actual   = fromYml[ProvisionRequest[String, String]](
      """
        |dataProduct:
        |   id: id
        |   name: name
        |   fullyQualifiedName: fullyQualifiedName
        |   domain: domain
        |   description: description
        |   environment: environment
        |   version: version
        |   kind: kind
        |   dataProductOwner: dataProductOwner
        |   devGroup: dev-group
        |   ownerGroup: owner-group
        |   dataProductOwnerDisplayName: dataProductOwnerDisplayName
        |   email: email
        |   informationSLA: informationSLA
        |   status: status
        |   maturity: maturity
        |   billing: {}
        |   tags: []
        |   specific: specific
        |   components: []
        |""".stripMargin
    )
    val expected = Right(
      ProvisionRequest(
        DataProduct[String](
          id = "id",
          name = "name",
          domain = "domain",
          environment = "environment",
          version = "version",
          dataProductOwner = "dataProductOwner",
          devGroup = "dev-group",
          ownerGroup = "owner-group",
          specific = "specific",
          components = Seq.empty[Json]
        ),
        None
      )
    )
    assert(actual == expected)

  }

}
