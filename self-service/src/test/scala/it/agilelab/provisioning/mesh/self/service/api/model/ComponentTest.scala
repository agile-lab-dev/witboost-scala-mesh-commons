package it.agilelab.provisioning.mesh.self.service.api.model

import it.agilelab.provisioning.mesh.self.service.api.model.Component.{
  DataContract,
  OutputPort,
  StorageArea,
  Workload
}
import it.agilelab.provisioning.mesh.self.service.api.model.openmetadata.ColumnConstraint._
import it.agilelab.provisioning.mesh.self.service.api.model.openmetadata.ColumnDataType._
import it.agilelab.provisioning.mesh.self.service.api.model.openmetadata.{ Column, ColumnConstraint, ColumnDataType }
import io.circe.Json
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.support.ParserSupport
import org.scalatest.funsuite.AnyFunSuite

class ComponentTest extends AnyFunSuite with ParserSupport {

  Seq(
    ("\"NULL\"", NULL),
    ("\"NOT_NULL\"", NOT_NULL),
    ("\"UNIQUE\"", UNIQUE),
    ("\"PRIMARY_KEY\"", PRIMARY_KEY)
  ) foreach { case (json: String, columnConstraint: ColumnConstraint) =>
    test(s"toJson with $columnConstraint return $json") {
      assert(toJson(columnConstraint) == json)
    }

    test(s"fromJson with $json return $columnConstraint") {
      assert(fromJson[ColumnConstraint](json) == Right(columnConstraint))
    }
  }

  Seq(
    ("\"null\"", NULL),
    ("\"not_null\"", NOT_NULL),
    ("\"unique\"", UNIQUE),
    ("\"primary_key\"", PRIMARY_KEY)
  ) foreach { case (json: String, columnConstraint: ColumnConstraint) =>
    test(s"fromJson with $json return $columnConstraint") {
      assert(fromJson[ColumnConstraint](json) == Right(columnConstraint))
    }
  }

  Seq(
    ("\"NUMBER\"", NUMBER),
    ("\"TINYINT\"", TINYINT),
    ("\"SMALLINT\"", SMALLINT),
    ("\"INT\"", INT),
    ("\"BIGINT\"", BIGINT),
    ("\"BYTEINT\"", BYTEINT),
    ("\"BYTES\"", BYTES),
    ("\"FLOAT\"", FLOAT),
    ("\"DOUBLE\"", DOUBLE),
    ("\"DECIMAL\"", DECIMAL),
    ("\"NUMERIC\"", NUMERIC),
    ("\"TIMESTAMP\"", TIMESTAMP),
    ("\"TIME\"", TIME),
    ("\"DATE\"", DATE),
    ("\"DATETIME\"", DATETIME),
    ("\"INTERVAL\"", INTERVAL),
    ("\"STRING\"", STRING),
    ("\"MEDIUMTEXT\"", MEDIUMTEXT),
    ("\"TEXT\"", TEXT),
    ("\"CHAR\"", CHAR),
    ("\"VARCHAR\"", VARCHAR),
    ("\"BOOLEAN\"", BOOLEAN),
    ("\"BINARY\"", BINARY),
    ("\"VARBINARY\"", VARBINARY),
    ("\"ARRAY\"", ARRAY),
    ("\"BLOB\"", BLOB),
    ("\"LONGBLOB\"", LONGBLOB),
    ("\"MEDIUMBLOB\"", MEDIUMBLOB),
    ("\"MAP\"", MAP),
    ("\"STRUCT\"", STRUCT),
    ("\"UNION\"", UNION),
    ("\"SET\"", SET),
    ("\"GEOGRAPHY\"", GEOGRAPHY),
    ("\"ENUM\"", ENUM),
    ("\"JSON\"", JSON)
  ) foreach { case (json: String, columnDataType: ColumnDataType) =>
    test(s"toJson with $columnDataType return $json") {
      assert(toJson(columnDataType) == json)
    }

    test(s"fromJson with $json return $columnDataType") {
      assert(fromJson[ColumnDataType](json) == Right(columnDataType))
    }
  }

  Seq(
    ("\"number\"", NUMBER),
    ("\"tinyint\"", TINYINT),
    ("\"smallint\"", SMALLINT),
    ("\"int\"", INT),
    ("\"bigint\"", BIGINT),
    ("\"byteint\"", BYTEINT),
    ("\"bytes\"", BYTES),
    ("\"float\"", FLOAT),
    ("\"double\"", DOUBLE),
    ("\"decimal\"", DECIMAL),
    ("\"numeric\"", NUMERIC),
    ("\"timestamp\"", TIMESTAMP),
    ("\"time\"", TIME),
    ("\"date\"", DATE),
    ("\"datetime\"", DATETIME),
    ("\"interval\"", INTERVAL),
    ("\"string\"", STRING),
    ("\"mediumtext\"", MEDIUMTEXT),
    ("\"text\"", TEXT),
    ("\"char\"", CHAR),
    ("\"varchar\"", VARCHAR),
    ("\"boolean\"", BOOLEAN),
    ("\"binary\"", BINARY),
    ("\"varbinary\"", VARBINARY),
    ("\"array\"", ARRAY),
    ("\"blob\"", BLOB),
    ("\"longblob\"", LONGBLOB),
    ("\"mediumblob\"", MEDIUMBLOB),
    ("\"map\"", MAP),
    ("\"struct\"", STRUCT),
    ("\"union\"", UNION),
    ("\"set\"", SET),
    ("\"geography\"", GEOGRAPHY),
    ("\"enum\"", ENUM),
    ("\"json\"", JSON)
  ) foreach { case (json: String, columnDataType: ColumnDataType) =>
    test(s"fromJson with $json return $columnDataType") {
      assert(fromJson[ColumnDataType](json) == Right(columnDataType))
    }
  }

  test("dataProduct from yaml") {
    val actual = fromYml[DataProduct[String]](
      """id: id
        |name: name
        |fullyQualifiedName: fullyQualifiedName
        |domain: domain
        |description: description
        |environment: environment
        |version: version
        |kind: kind
        |dataProductOwner: dataProductOwner
        |devGroup: dev-group
        |ownerGroup: owner-group
        |dataProductOwnerDisplayName: dataProductOwnerDisplayName
        |email: email
        |informationSLA: informationSLA
        |status: status
        |maturity: maturity
        |billing: {}
        |tags: []
        |specific: specific
        |components: []""".stripMargin
    )

    val expected = DataProduct[String](
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
    )

    assert(actual == Right(expected))
  }

  test("workload component from yaml") {
    val actual = fromYml[Component[String]](
      """id: id
        |name: name
        |fullyQualifiedName: fullyQualifiedName
        |description: description
        |kind: kind
        |workloadType: workloadType
        |connectionType: connectionType
        |technology: technology
        |platform: platform
        |version: version
        |infrastructureTemplateId: infrastructureTemplateId
        |useCaseTemplateId: useCaseTemplateId
        |tags: []
        |readsFrom: []
        |specific: specific""".stripMargin
    )

    val expected = Workload[String](
      id = "id",
      name = "name",
      version = "version",
      description = "description",
      specific = "specific"
    )

    assert(actual == Right(expected))
  }

  test("outputPort component from yaml") {
    val actual = fromYml[Component[String]](
      """id: id
        |name: name
        |fullyQualifiedName: fullyQualifiedName
        |description: description
        |kind: kind
        |version: version
        |infrastructureTemplateId: infrastructureTemplateId
        |useCaseTemplateId: useCaseTemplateId
        |dependsOn: []
        |platform: platform
        |technology: technology
        |outputPortType: outputPortType
        |creationDate: creationDate
        |startDate: startDate
        |processDescription: processDescription
        |dataContract:
        |   schema:
        |     - name: column1
        |       dataType: int
        |       constraint: PRIMARY_KEY
        |     - name: column2
        |       dataType: BIGINT
        |       constraint: NOT_NULL
        |   SLA:
        |      intervalOfChange: 1 hours
        |      timeliness: 1 minutes
        |      upTime: 99.9%
        |   termsAndConditions: only usable in development environment
        |   endpoint: https://myurl/development/my_domain/my_data_product/1.0.0/my_raw_s3_port
        |dataSharingAgreements:
        |   purpose: purpose
        |   billing: billing
        |   security: security
        |   intendedUsage: intendedUsage
        |   limitations: limitations
        |   lifeCycle: lifeCycle
        |   confidentiality: confidentiality
        |tags: []
        |sampleData:
        |  columns:
        |    - col1
        |    - col2
        |    - col3
        |  rows:
        |    -
        |      - r1c1
        |      - r1c2
        |      - r1c3
        |    -
        |      - r2c1
        |      - r2c2
        |      - r2c3
        |semanticLinking: []
        |specific: specific""".stripMargin
    )

    val expected = OutputPort[String](
      id = "id",
      name = "name",
      description = "description",
      version = "version",
      dataContract = DataContract(
        schema = Seq(
          Column("column1", INT, None, None, None, None, None, None, Some(PRIMARY_KEY), None, None, None),
          Column("column2", BIGINT, None, None, None, None, None, None, Some(NOT_NULL), None, None, None)
        )
      ),
      specific = "specific"
    )

    assert(actual == Right(expected))
  }

  test("storageArea component from yaml") {
    val actual = fromYml[Component[String]](
      """id: id
        |name: name
        |fullyQualifiedName: fullyQualifiedName
        |description: description
        |kind: kind
        |owners: []
        |infrastructureTemplateId: infrastructureTemplateId
        |useCaseTemplateId: useCaseTemplateId
        |technology: technology
        |platform: platform
        |storageType: storageType
        |tags: []
        |specific: specific""".stripMargin
    )

    val expected = StorageArea[String](
      id = "id",
      name = "name",
      description = "description",
      owners = Seq.empty,
      specific = "specific"
    )

    assert(actual == Right(expected))
  }

}
