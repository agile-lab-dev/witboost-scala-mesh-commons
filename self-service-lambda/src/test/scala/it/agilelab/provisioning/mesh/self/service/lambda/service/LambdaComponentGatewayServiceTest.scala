package it.agilelab.provisioning.mesh.self.service.lambda.service

import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.repository.RepositoryError.FindEntityByIdErr
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.{ COMPLETED, FAILED, RUNNING }
import it.agilelab.provisioning.mesh.self.service.api.model.Component.Workload
import it.agilelab.provisioning.mesh.self.service.api.model.{ DataProduct, ProvisionRequest }
import it.agilelab.provisioning.mesh.self.service.core.gateway.{ ComponentGateway, ComponentGatewayError }
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import io.circe.Json
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.principalsmapping.CdpIamPrincipals
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class LambdaComponentGatewayServiceTest extends AnyFunSuite with MockFactory {

  val componentGateway: ComponentGateway[String, String, String, CdpIamPrincipals] =
    mock[ComponentGateway[String, String, String, CdpIamPrincipals]]
  val repository: Repository[ProvisioningStatus, String, Unit]                     =
    mock[Repository[ProvisioningStatus, String, Unit]]
  val asyncLambdaComponentGateway                                                  =
    new LambdaComponentGatewayService[String, String, String, CdpIamPrincipals](
      repository,
      componentGateway
    )

  val jsonProvisionCommand: String =
    """
      |{
      |  "operation": "CREATE",
      |  "command": {
      |    "requestId" : "my-id",
      |    "provisionRequest" : {
      |      "dataProduct" : {
      |        "id" : "my-dp-id",
      |        "name" : "my-dp-name",
      |        "fullyQualifiedName" : "my-dp-fully-qualified-name",
      |        "domain" : "my-dp-domain",
      |        "description" : "my-dp-description",
      |        "environment" : "my-dp-environment",
      |        "version" : "my-dp-version",
      |        "kind" : "my-dp-kind",
      |        "dataProductOwner" : "my-dp-owner",
      |        "devGroup": "dev-group",
      |        "ownerGroup": "owner-group",
      |        "dataProductOwnerDisplayName" : "my-dp-owner-display-name",
      |        "email" : "my-dp-email",
      |        "informationSLA" : "my-dp-information-SLA",
      |        "status" : "my-dp-status",
      |        "maturity" : "my-dp-maturity",
      |        "billing" : {
      |
      |        },
      |        "tags" : [
      |        ],
      |        "specific" : "my-dp-specific",
      |        "components" : []
      |      },
      |      "component" : {
      |        "id" : "my-dp-workload-id",
      |        "name" : "my-dp-workload-name",
      |        "fullyQualifiedName" : "my-dp-workload-fully-qualified-name",
      |        "description" : "my-dp-description",
      |        "kind" : "workload",
      |        "workloadType" : "my-dp-workload-type",
      |        "connectionType" : "my-dp-workload-connection-type",
      |        "technology" : "my-dp-workload-technology",
      |        "platform" : "my-dp-workload-platform",
      |        "version" : "my-dp-workload-version",
      |        "infrastructureTemplateId" : "my-dp-workload-infrastructure-template-id",
      |        "useCaseTemplateId" : "my-dp-workload-use-case-template-id",
      |        "tags" : [
      |        ],
      |        "readsFrom" : [
      |        ],
      |        "specific" : "x"
      |      }
      |    }
      |  }
      |}
      |""".stripMargin

  val jsonUnprovisionCommand: String =
    """
      |{
      |  "operation": "DESTROY",
      |  "command": {
      |    "requestId" : "my-id",
      |    "provisionRequest" : {
      |      "dataProduct" : {
      |        "id" : "my-dp-id",
      |        "name" : "my-dp-name",
      |        "fullyQualifiedName" : "my-dp-fully-qualified-name",
      |        "domain" : "my-dp-domain",
      |        "description" : "my-dp-description",
      |        "environment" : "my-dp-environment",
      |        "version" : "my-dp-version",
      |        "kind" : "my-dp-kind",
      |        "dataProductOwner" : "my-dp-owner",
      |        "devGroup": "dev-group",
      |        "ownerGroup": "owner-group",
      |        "dataProductOwnerDisplayName" : "my-dp-owner-display-name",
      |        "email" : "my-dp-email",
      |        "informationSLA" : "my-dp-information-SLA",
      |        "status" : "my-dp-status",
      |        "maturity" : "my-dp-maturity",
      |        "billing" : {
      |
      |        },
      |        "tags" : [
      |        ],
      |        "specific" : "my-dp-specific",
      |        "components" : []
      |      },
      |      "component" : {
      |        "id" : "my-dp-workload-id",
      |        "name" : "my-dp-workload-name",
      |        "fullyQualifiedName" : "my-dp-workload-fully-qualified-name",
      |        "description" : "my-dp-description",
      |        "kind" : "workload",
      |        "workloadType" : "my-dp-workload-type",
      |        "connectionType" : "my-dp-workload-connection-type",
      |        "technology" : "my-dp-workload-technology",
      |        "platform" : "my-dp-workload-platform",
      |        "version" : "my-dp-workload-version",
      |        "infrastructureTemplateId" : "my-dp-workload-infrastructure-template-id",
      |        "useCaseTemplateId" : "my-dp-workload-use-case-template-id",
      |        "tags" : [
      |        ],
      |        "readsFrom" : [
      |        ],
      |        "specific" : "x"
      |      }
      |    }
      |  }
      |}
      |""".stripMargin

  val provisionCommand: ProvisionCommand[String, String] =
    ProvisionCommand(
      "my-id",
      ProvisionRequest(
        DataProduct[String](
          id = "my-dp-id",
          name = "my-dp-name",
          domain = "my-dp-domain",
          environment = "my-dp-environment",
          version = "my-dp-version",
          dataProductOwner = "my-dp-owner",
          devGroup = "dev-group",
          ownerGroup = "owner-group",
          specific = "my-dp-specific",
          components = Seq.empty[Json]
        ),
        Some(
          Workload[String](
            id = "my-dp-workload-id",
            name = "my-dp-workload-name",
            version = "my-dp-workload-version",
            description = "my-dp-description",
            specific = "x"
          )
        )
      )
    )

  test("default") {
    val repository       = mock[Repository[ProvisioningStatus, String, Unit]]
    val componentGateway = mock[ComponentGateway[String, String, String, CdpIamPrincipals]]
    val actual           =
      LambdaComponentGatewayService.default[String, String, String, CdpIamPrincipals](repository, componentGateway)
    assert(actual.isInstanceOf[LambdaComponentGatewayService[String, String, String, CdpIamPrincipals]])
  }

  test("handle CREATE OP success create and update provision information") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (componentGateway.create _)
      .expects(provisionCommand)
      .once()
      .returns(Right("my-result"))
    (repository.update _)
      .expects(ProvisioningStatus("my-id", COMPLETED, Some("\"my-result\"")))
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    assert(asyncLambdaComponentGateway.handle(jsonProvisionCommand) == Right("\"my-result\""))
  }

  test("handle CREATE OP on repository findById error") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Left(FindEntityByIdErr("x", new IllegalArgumentException("x"))))

    val expected = Left("Unable to fetch Provisioning Status information with provided repository.")
    val actual   = asyncLambdaComponentGateway.handle(jsonProvisionCommand)
    assert(actual == expected)
  }

  test("handle CREATE OP on repository findById None") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Right(None))

    val expected = Left("Provisioning Status with id: my-id Not found")
    val actual   = asyncLambdaComponentGateway.handle(jsonProvisionCommand)
    assert(actual == expected)
  }

  test("handle CREATE OP on create error") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (componentGateway.create _)
      .expects(provisionCommand)
      .once()
      .returns(Left(ComponentGatewayError("my-error")))
    (repository.update _)
      .expects(ProvisioningStatus("my-id", FAILED, Some("Unable to create requested component. Details my-error")))
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    val actual   = asyncLambdaComponentGateway.handle(jsonProvisionCommand)
    val expected = Left("Unable to create requested component. Details my-error")
    assert(actual == expected)
  }

  test("handle CREATE OP on repository update error") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (componentGateway.create _)
      .expects(provisionCommand)
      .once()
      .returns(Right("my-result"))
    (repository.update _)
      .expects(ProvisioningStatus("my-id", COMPLETED, Some("\"my-result\"")))
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    val actual = asyncLambdaComponentGateway.handle(jsonProvisionCommand)
    assert(actual == Right("\"my-result\""))
  }

  test("handle CREATE OP on bad json event") {
    val actual = Left("DecodeErr(DecodingFailure at .operation: Attempt to decode value on failed cursor)")
    assert(asyncLambdaComponentGateway.handle("""{"id":"x"}""") == actual)
  }

  test("handle DESTROY OP success create and update provision information") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (componentGateway.destroy _)
      .expects(provisionCommand)
      .once()
      .returns(Right("my-result"))
    (repository.update _)
      .expects(ProvisioningStatus("my-id", COMPLETED, Some("\"my-result\"")))
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    assert(asyncLambdaComponentGateway.handle(jsonUnprovisionCommand) == Right("\"my-result\""))
  }

  test("handle on repository findById error") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Left(FindEntityByIdErr("x", new IllegalArgumentException("x"))))

    val expected = Left("Unable to fetch Provisioning Status information with provided repository.")
    val actual   = asyncLambdaComponentGateway.handle(jsonUnprovisionCommand)
    assert(actual == expected)
  }

  test("handle on repository findById None") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Right(None))

    val expected = Left("Provisioning Status with id: my-id Not found")
    val actual   = asyncLambdaComponentGateway.handle(jsonUnprovisionCommand)
    assert(actual == expected)
  }

  test("handle on create error") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (componentGateway.destroy _)
      .expects(provisionCommand)
      .once()
      .returns(Left(ComponentGatewayError("my-error")))
    (repository.update _)
      .expects(ProvisioningStatus("my-id", FAILED, Some("Unable to create requested component. Details my-error")))
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    val actual   = asyncLambdaComponentGateway.handle(jsonUnprovisionCommand)
    val expected = Left("Unable to create requested component. Details my-error")
    assert(actual == expected)
  }

  test("handle on repository update error") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (componentGateway.destroy _)
      .expects(provisionCommand)
      .once()
      .returns(Right("my-result"))
    (repository.update _)
      .expects(ProvisioningStatus("my-id", COMPLETED, Some("\"my-result\"")))
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    val actual = asyncLambdaComponentGateway.handle(jsonUnprovisionCommand)
    assert(actual == Right("\"my-result\""))
  }

  test("handle on bad json event") {
    val actual = Left("DecodeErr(DecodingFailure at .operation: Attempt to decode value on failed cursor)")
    assert(asyncLambdaComponentGateway.handle("""{"id":"x"}""") == actual)
  }
}
