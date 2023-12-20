package it.agilelab.provisioning.mesh.self.service.api.model

import it.agilelab.provisioning.mesh.self.service.api.model.Component.Workload
import io.circe.{ Json, JsonObject }
import org.scalatest.funsuite.AnyFunSuite
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.support.ParserError.DecodeErr
import it.agilelab.provisioning.commons.support.ParserSupport
import org.scalatest.EitherValues._

class ProvisioningDescriptorTest extends AnyFunSuite with ParserSupport {
  test("decode ProvisioningDescriptor") {
    val request: String =
      """
        |dataProduct:
        |  id: my-dp-id
        |  name: my-dp-name
        |  fullyQualifiedName: my-dp-fully-qualified-name
        |  domain: my-dp-domain
        |  description: my-dp-description
        |  environment: my-dp-environment
        |  version: my-dp-version
        |  kind: my-dp-kind
        |  dataProductOwner: my-dp-owner
        |  devGroup: dev-group
        |  ownerGroup: owner-group
        |  dataProductOwnerDisplayName: my-dp-owner-display-name
        |  email: email
        |  informationSLA: my-dp-info-sla
        |  status: my-dp-status
        |  maturity: my-dp-maturity
        |  billing: {}
        |  tags: []
        |  specific: my-dp-specific
        |  components:
        |    - id: my-dp-component-id-1
        |      name: my-dp-comonent-name-1
        |      fullyQualifiedName: my-dp-component-fully-qualified-name
        |      description: my-dp-component-description
        |      kind: my-dp-component-kind
        |      workloadType: my-dp-component-type
        |      connectionType: my-dp-component-connection-type
        |      technology: my-dp-component-technology
        |      platform: my-dp-platform
        |      version: my-dp-component-version
        |      infrastructureTemplateId: my-dp-component-infra-template-id
        |      useCaseTemplateId: my-dp-component-infra-use-case-id
        |      tags: []
        |      readsFrom: []
        |      specific: update
        |    - id: my-dp-component-id-2
        |      name: my-dp-comonent-name-2
        |      fullyQualifiedName: my-dp-component-fully-qualified-name
        |      description: my-dp-component-description
        |      kind: my-dp-component-kind
        |      workloadType: my-dp-component-type
        |      connectionType: my-dp-component-connection-type
        |      technology: my-dp-component-technology
        |      platform: my-dp-platform
        |      version: my-dp-component-version
        |      infrastructureTemplateId: my-dp-component-infra-template-id
        |      useCaseTemplateId: my-dp-component-infra-use-case-id
        |      tags: []
        |      readsFrom: []
        |      specific: update
        |    - id: my-dp-component-id-3
        |      name: my-dp-comonent-name-3
        |      fullyQualifiedName: my-dp-component-fully-qualified-name
        |      description: my-dp-component-description
        |      kind: my-dp-component-kind
        |      workloadType: my-dp-component-type
        |      connectionType: my-dp-component-connection-type
        |      technology: my-dp-component-technology
        |      platform: my-dp-platform
        |      version: my-dp-component-version
        |      infrastructureTemplateId: my-dp-component-infra-template-id
        |      useCaseTemplateId: my-dp-component-infra-use-case-id
        |      tags: []
        |      readsFrom: []
        |      specific: update
        |componentIdToProvision: my-dp-component-id-2
        |""".stripMargin
    val actual          = fromYml[ProvisioningDescriptor[String]](request)
    val expected        = Right(
      ProvisioningDescriptor(
        dataProduct = DataProduct[String](
          id = "my-dp-id",
          name = "my-dp-name",
          domain = "my-dp-domain",
          environment = "my-dp-environment",
          version = "my-dp-version",
          dataProductOwner = "my-dp-owner",
          devGroup = "dev-group",
          ownerGroup = "owner-group",
          specific = "my-dp-specific",
          components = Seq(
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-1")),
                ("name", Json.fromString("my-dp-comonent-name-1")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            ),
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-2")),
                ("name", Json.fromString("my-dp-comonent-name-2")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            ),
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-3")),
                ("name", Json.fromString("my-dp-comonent-name-3")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            )
          )
        ),
        componentIdToProvision = Some("my-dp-component-id-2")
      )
    )
    assert(actual == expected)
  }

  test(
    "toProvisionRequest ProvisioningDescriptor return Right(ProvisioningDescriptor) without componentIdToProvision"
  ) {
    val pd       = ProvisioningDescriptor(
      dataProduct = DataProduct[String](
        id = "my-dp-id",
        name = "my-dp-name",
        domain = "my-dp-domain",
        environment = "my-dp-environment",
        version = "my-dp-version",
        dataProductOwner = "my-dp-owner",
        devGroup = "dev-group",
        ownerGroup = "owner-group",
        specific = "my-dp-specific",
        components = Seq(
          Json.fromJsonObject(
            JsonObject(
              ("id", Json.fromString("my-dp-component-id-1")),
              ("name", Json.fromString("my-dp-comonent-name-1")),
              ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
              ("description", Json.fromString("my-dp-component-description")),
              ("kind", Json.fromString("my-dp-component-kind")),
              ("workloadType", Json.fromString("my-dp-component-type")),
              ("connectionType", Json.fromString("my-dp-component-connection-type")),
              ("technology", Json.fromString("my-dp-component-technology")),
              ("platform", Json.fromString("my-dp-platform")),
              ("version", Json.fromString("my-dp-component-version")),
              ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
              ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
              ("tags", Json.fromValues(Seq.empty)),
              ("readsFrom", Json.fromValues(Seq.empty)),
              ("specific", Json.fromString("update"))
            )
          ),
          Json.fromJsonObject(
            JsonObject(
              ("id", Json.fromString("my-dp-component-id-2")),
              ("name", Json.fromString("my-dp-comonent-name-2")),
              ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
              ("description", Json.fromString("my-dp-component-description")),
              ("kind", Json.fromString("my-dp-component-kind")),
              ("workloadType", Json.fromString("my-dp-component-type")),
              ("connectionType", Json.fromString("my-dp-component-connection-type")),
              ("technology", Json.fromString("my-dp-component-technology")),
              ("platform", Json.fromString("my-dp-platform")),
              ("version", Json.fromString("my-dp-component-version")),
              ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
              ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
              ("tags", Json.fromValues(Seq.empty)),
              ("readsFrom", Json.fromValues(Seq.empty)),
              ("specific", Json.fromString("update"))
            )
          ),
          Json.fromJsonObject(
            JsonObject(
              ("id", Json.fromString("my-dp-component-id-3")),
              ("name", Json.fromString("my-dp-comonent-name-3")),
              ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
              ("description", Json.fromString("my-dp-component-description")),
              ("kind", Json.fromString("my-dp-component-kind")),
              ("workloadType", Json.fromString("my-dp-component-type")),
              ("connectionType", Json.fromString("my-dp-component-connection-type")),
              ("technology", Json.fromString("my-dp-component-technology")),
              ("platform", Json.fromString("my-dp-platform")),
              ("version", Json.fromString("my-dp-component-version")),
              ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
              ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
              ("tags", Json.fromValues(Seq.empty)),
              ("readsFrom", Json.fromValues(Seq.empty)),
              ("specific", Json.fromString("update"))
            )
          )
        )
      ),
      componentIdToProvision = None
    )
    val actual   = pd.toProvisionRequest[Unit]
    val expected = Right(
      ProvisionRequest[String, Unit](
        dataProduct = DataProduct[String](
          id = "my-dp-id",
          name = "my-dp-name",
          domain = "my-dp-domain",
          environment = "my-dp-environment",
          version = "my-dp-version",
          dataProductOwner = "my-dp-owner",
          devGroup = "dev-group",
          ownerGroup = "owner-group",
          specific = "my-dp-specific",
          components = Seq(
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-1")),
                ("name", Json.fromString("my-dp-comonent-name-1")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            ),
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-2")),
                ("name", Json.fromString("my-dp-comonent-name-2")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            ),
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-3")),
                ("name", Json.fromString("my-dp-comonent-name-3")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            )
          )
        ),
        component = None
      )
    )
    assert(actual == expected)

  }

  test("toProvisionRequest ProvisioningDescriptor return Right(ProvisioningDescriptor) with componentIdToProvision") {
    val pd       = ProvisioningDescriptor(
      dataProduct = DataProduct[String](
        id = "my-dp-id",
        name = "my-dp-name",
        domain = "my-dp-domain",
        environment = "my-dp-environment",
        version = "my-dp-version",
        dataProductOwner = "my-dp-owner",
        devGroup = "dev-group",
        ownerGroup = "owner-group",
        specific = "my-dp-specific",
        components = Seq(
          Json.fromJsonObject(
            JsonObject(
              ("id", Json.fromString("my-dp-component-id-1")),
              ("name", Json.fromString("my-dp-comonent-name-1")),
              ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
              ("description", Json.fromString("my-dp-component-description")),
              ("kind", Json.fromString("my-dp-component-kind")),
              ("workloadType", Json.fromString("my-dp-component-type")),
              ("connectionType", Json.fromString("my-dp-component-connection-type")),
              ("technology", Json.fromString("my-dp-component-technology")),
              ("platform", Json.fromString("my-dp-platform")),
              ("version", Json.fromString("my-dp-component-version")),
              ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
              ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
              ("tags", Json.fromValues(Seq.empty)),
              ("readsFrom", Json.fromValues(Seq.empty)),
              ("specific", Json.fromString("update"))
            )
          ),
          Json.fromJsonObject(
            JsonObject(
              ("id", Json.fromString("my-dp-component-id-2")),
              ("name", Json.fromString("my-dp-comonent-name-2")),
              ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
              ("description", Json.fromString("my-dp-component-description")),
              ("kind", Json.fromString("my-dp-component-kind")),
              ("workloadType", Json.fromString("my-dp-component-type")),
              ("connectionType", Json.fromString("my-dp-component-connection-type")),
              ("technology", Json.fromString("my-dp-component-technology")),
              ("platform", Json.fromString("my-dp-platform")),
              ("version", Json.fromString("my-dp-component-version")),
              ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
              ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
              ("tags", Json.fromValues(Seq.empty)),
              ("readsFrom", Json.fromValues(Seq.empty)),
              ("specific", Json.fromString("update"))
            )
          ),
          Json.fromJsonObject(
            JsonObject(
              ("id", Json.fromString("my-dp-component-id-3")),
              ("name", Json.fromString("my-dp-comonent-name-3")),
              ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
              ("description", Json.fromString("my-dp-component-description")),
              ("kind", Json.fromString("my-dp-component-kind")),
              ("workloadType", Json.fromString("my-dp-component-type")),
              ("connectionType", Json.fromString("my-dp-component-connection-type")),
              ("technology", Json.fromString("my-dp-component-technology")),
              ("platform", Json.fromString("my-dp-platform")),
              ("version", Json.fromString("my-dp-component-version")),
              ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
              ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
              ("tags", Json.fromValues(Seq.empty)),
              ("readsFrom", Json.fromValues(Seq.empty)),
              ("specific", Json.fromString("update"))
            )
          )
        )
      ),
      componentIdToProvision = Some("my-dp-component-id-2")
    )
    val actual   = pd.toProvisionRequest[String]
    val expected = Right(
      ProvisionRequest[String, String](
        dataProduct = DataProduct[String](
          id = "my-dp-id",
          name = "my-dp-name",
          domain = "my-dp-domain",
          environment = "my-dp-environment",
          version = "my-dp-version",
          dataProductOwner = "my-dp-owner",
          devGroup = "dev-group",
          ownerGroup = "owner-group",
          specific = "my-dp-specific",
          components = Seq(
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-1")),
                ("name", Json.fromString("my-dp-comonent-name-1")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            ),
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-2")),
                ("name", Json.fromString("my-dp-comonent-name-2")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            ),
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-3")),
                ("name", Json.fromString("my-dp-comonent-name-3")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            )
          )
        ),
        component = Some(
          Workload[String](
            id = "my-dp-component-id-2",
            name = "my-dp-comonent-name-2",
            description = "my-dp-component-description",
            version = "my-dp-component-version",
            specific = "update"
          )
        )
      )
    )
    assert(actual == expected)
  }

  test(
    "toProvisionRequest ProvisioningDescriptor return Right(ProvisioningDescriptor) with componentIdToProvision and missing component from list"
  ) {
    val pd       = ProvisioningDescriptor(
      dataProduct = DataProduct[String](
        id = "my-dp-id",
        name = "my-dp-name",
        domain = "my-dp-domain",
        environment = "my-dp-environment",
        version = "my-dp-version",
        dataProductOwner = "my-dp-owner",
        devGroup = "dev-group",
        ownerGroup = "owner-group",
        specific = "my-dp-specific",
        components = Seq(
          Json.fromJsonObject(
            JsonObject(
              ("id", Json.fromString("my-dp-component-id-1")),
              ("name", Json.fromString("my-dp-comonent-name-1")),
              ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
              ("description", Json.fromString("my-dp-component-description")),
              ("kind", Json.fromString("my-dp-component-kind")),
              ("workloadType", Json.fromString("my-dp-component-type")),
              ("connectionType", Json.fromString("my-dp-component-connection-type")),
              ("technology", Json.fromString("my-dp-component-technology")),
              ("platform", Json.fromString("my-dp-platform")),
              ("version", Json.fromString("my-dp-component-version")),
              ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
              ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
              ("tags", Json.fromValues(Seq.empty)),
              ("readsFrom", Json.fromValues(Seq.empty)),
              ("specific", Json.fromString("update"))
            )
          ),
          Json.fromJsonObject(
            JsonObject(
              ("id", Json.fromString("my-dp-component-id-2")),
              ("name", Json.fromString("my-dp-comonent-name-2")),
              ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
              ("description", Json.fromString("my-dp-component-description")),
              ("kind", Json.fromString("my-dp-component-kind")),
              ("workloadType", Json.fromString("my-dp-component-type")),
              ("connectionType", Json.fromString("my-dp-component-connection-type")),
              ("technology", Json.fromString("my-dp-component-technology")),
              ("platform", Json.fromString("my-dp-platform")),
              ("version", Json.fromString("my-dp-component-version")),
              ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
              ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
              ("tags", Json.fromValues(Seq.empty)),
              ("readsFrom", Json.fromValues(Seq.empty)),
              ("specific", Json.fromString("update"))
            )
          ),
          Json.fromJsonObject(
            JsonObject(
              ("id", Json.fromString("my-dp-component-id-3")),
              ("name", Json.fromString("my-dp-comonent-name-3")),
              ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
              ("description", Json.fromString("my-dp-component-description")),
              ("kind", Json.fromString("my-dp-component-kind")),
              ("workloadType", Json.fromString("my-dp-component-type")),
              ("connectionType", Json.fromString("my-dp-component-connection-type")),
              ("technology", Json.fromString("my-dp-component-technology")),
              ("platform", Json.fromString("my-dp-platform")),
              ("version", Json.fromString("my-dp-component-version")),
              ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
              ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
              ("tags", Json.fromValues(Seq.empty)),
              ("readsFrom", Json.fromValues(Seq.empty)),
              ("specific", Json.fromString("update"))
            )
          )
        )
      ),
      componentIdToProvision = Some("my-dp-component-id-4")
    )
    val actual   = pd.toProvisionRequest[String]
    val expected = Right(
      ProvisionRequest[String, String](
        dataProduct = DataProduct[String](
          id = "my-dp-id",
          name = "my-dp-name",
          domain = "my-dp-domain",
          environment = "my-dp-environment",
          version = "my-dp-version",
          dataProductOwner = "my-dp-owner",
          devGroup = "dev-group",
          ownerGroup = "owner-group",
          specific = "my-dp-specific",
          components = Seq(
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-1")),
                ("name", Json.fromString("my-dp-comonent-name-1")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            ),
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-2")),
                ("name", Json.fromString("my-dp-comonent-name-2")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            ),
            Json.fromJsonObject(
              JsonObject(
                ("id", Json.fromString("my-dp-component-id-3")),
                ("name", Json.fromString("my-dp-comonent-name-3")),
                ("fullyQualifiedName", Json.fromString("my-dp-component-fully-qualified-name")),
                ("description", Json.fromString("my-dp-component-description")),
                ("kind", Json.fromString("my-dp-component-kind")),
                ("workloadType", Json.fromString("my-dp-component-type")),
                ("connectionType", Json.fromString("my-dp-component-connection-type")),
                ("technology", Json.fromString("my-dp-component-technology")),
                ("platform", Json.fromString("my-dp-platform")),
                ("version", Json.fromString("my-dp-component-version")),
                ("infrastructureTemplateId", Json.fromString("my-dp-component-infra-template-id")),
                ("useCaseTemplateId", Json.fromString("my-dp-component-infra-use-case-id")),
                ("tags", Json.fromValues(Seq.empty)),
                ("readsFrom", Json.fromValues(Seq.empty)),
                ("specific", Json.fromString("update"))
              )
            )
          )
        ),
        component = None
      )
    )
    assert(actual == expected)
  }

  test("toProvisionRequest ProvisioningDescriptor return Left(ParserError)") {
    val pd     = ProvisioningDescriptor(
      dataProduct = DataProduct[String](
        id = "my-dp-id",
        name = "my-dp-name",
        domain = "my-dp-domain",
        environment = "my-dp-environment",
        version = "my-dp-version",
        dataProductOwner = "my-dp-owner",
        devGroup = "dev-group",
        ownerGroup = "owner-group",
        specific = "my-dp-specific",
        components = Seq(
          Json.fromJsonObject(
            JsonObject(
              ("id", Json.fromString("my-dp-component-id-1")),
              ("key", Json.fromString("value"))
            )
          )
        )
      ),
      componentIdToProvision = Some("my-dp-component-id-1")
    )
    val actual = pd.toProvisionRequest[String]
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[DecodeErr])
    assert(
      actual.left.value
        .asInstanceOf[DecodeErr]
        .error
        .getMessage == "Attempt to decode value on failed cursor: DownField(name)"
    )
  }
}
