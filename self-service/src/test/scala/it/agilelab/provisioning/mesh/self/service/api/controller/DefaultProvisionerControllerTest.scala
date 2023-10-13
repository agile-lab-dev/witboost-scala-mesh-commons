package it.agilelab.provisioning.mesh.self.service.api.controller

import it.agilelab.provisioning.mesh.self.service.api.handler.provision.ProvisionHandler
import it.agilelab.provisioning.mesh.self.service.api.handler.state.ProvisionStateHandler
import it.agilelab.provisioning.mesh.self.service.api.handler.validation.ValidationHandler
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ SystemError, ValidationError }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiRequest.ProvisioningRequest
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.{ COMPLETED, RUNNING }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse._
import io.circe.generic.auto._
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import io.circe.generic.auto._

class DefaultProvisionerControllerTest extends AnyFunSuite with MockFactory {
  val validationHandler: ValidationHandler[String, String] = mock[ValidationHandler[String, String]]
  val provisionHandler: ProvisionHandler[String, String]   = mock[ProvisionHandler[String, String]]
  val provisionStateHandler: ProvisionStateHandler         = mock[ProvisionStateHandler]

  val provisionerController = new DefaultProvisionerController[String, String](
    validationHandler,
    provisionHandler,
    provisionStateHandler
  )

  private val request: ProvisioningRequest = ProvisioningRequest(
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
  )

  test("validate return a valid Right(ValidationResult)") {
    (validationHandler.validate _).expects(*).once().returns(Right(valid()))
    val actual   = provisionerController.validate(request)
    val expected = Right(ValidationResult(valid = true, None))
    assert(actual == expected)
  }

  test("validate return an invalid Right(ValidationResult) on parsing failure") {
    val actual   = provisionerController.validate(ProvisioningRequest("""xxx""".stripMargin))
    val expected = Right(
      ValidationResult(
        valid = false,
        Some(
          ValidationError(Seq("DecodeErr(DecodingFailure at .dataProduct: Attempt to decode value on failed cursor)"))
        )
      )
    )
    assert(actual == expected)
  }

  test("validate return an invalid Right(ValidationResult)") {
    (validationHandler.validate _).expects(*).once().returns(Right(invalid("x")))
    val actual   = provisionerController.validate(request)
    val expected = Right(ValidationResult(valid = false, Some(ValidationError(Seq("x")))))
    assert(actual == expected)
  }

  test("validate return a Left(SystemError)") {
    (validationHandler.validate _).expects(*).once().returns(Left(SystemError("x")))
    val actual   = provisionerController.validate(request)
    val expected = Left(SystemError("x"))
    assert(actual == expected)
  }

  test("provision return Right(ProvisioningStatus)") {
    (validationHandler.validate _).expects(*).once().returns(Right(valid()))
    (provisionHandler.provision _).expects(*).once().returns(Right(ProvisioningStatus("x", RUNNING, None)))
    val actual   = provisionerController.provision(request)
    val expected = Right(ProvisioningStatus("x", RUNNING, None))
    assert(actual == expected)
  }

  test("provision return a Left(SystemError)") {
    (validationHandler.validate _).expects(*).once().returns(Right(valid()))
    (provisionHandler.provision _).expects(*).once().returns(Left(SystemError("x")))
    val actual   = provisionerController.provision(request)
    val expected = Left(SystemError("x"))
    assert(actual == expected)
  }

  test("provision return a Left(ValidationError) on parsing failure") {
    val actual   = provisionerController.provision(ProvisioningRequest("""xxx""".stripMargin))
    val expected = Left(
      ValidationError(
        Seq(
          "DecodeErr(DecodingFailure at .dataProduct: Attempt to decode value on failed cursor)"
        )
      )
    )
    assert(actual == expected)
  }

  test("provision return an Left(ValidationError) on invalid request") {
    (validationHandler.validate _).expects(*).once().returns(Right(invalid("x")))
    val actual   = provisionerController.provision(request)
    val expected = Left(ValidationError(Seq("x")))
    assert(actual == expected)
  }

  test("getProvisionStatus return Right(state)") {
    (provisionStateHandler.get _).expects("x").returns(Right(ProvisioningStatus("x", COMPLETED, Some("x"))))
    val actual   = provisionerController.getProvisionStatus("x")
    val expected = Right(ProvisioningStatus("x", COMPLETED, Some("x")))
    assert(actual == expected)
  }

  test("getProvisionStatus return Left(SystemError)") {
    (provisionStateHandler.get _).expects("x").returns(Left(SystemError("z")))
    val actual   = provisionerController.getProvisionStatus("x")
    val expected = Left(SystemError("z"))
    assert(actual == expected)
  }

  test("getProvisionStatus return Left(ValidationError)") {
    (provisionStateHandler.get _).expects("x").returns(Left(ValidationError(Seq("k"))))
    val actual   = provisionerController.getProvisionStatus("x")
    val expected = Left(ValidationError(Seq("k")))
    assert(actual == expected)
  }

  test("unprovision return Right(ProvisioningStatus)") {
    (validationHandler.validate _).expects(*).once().returns(Right(valid()))
    (provisionHandler.unprovision _).expects(*).once().returns(Right(ProvisioningStatus("x", RUNNING, None)))
    val actual   = provisionerController.unprovision(request)
    val expected = Right(ProvisioningStatus("x", RUNNING, None))
    assert(actual == expected)
  }

  test("unprovision return a Left(SystemError)") {
    (validationHandler.validate _).expects(*).once().returns(Right(valid()))
    (provisionHandler.unprovision _).expects(*).once().returns(Left(SystemError("x")))
    val actual   = provisionerController.unprovision(request)
    val expected = Left(SystemError("x"))
    assert(actual == expected)
  }

  test("unprovision return a Left(ValidationError) on parsing failure") {
    val actual   = provisionerController.unprovision(ProvisioningRequest("""xxx""".stripMargin))
    val expected = Left(
      ValidationError(
        Seq(
          "DecodeErr(DecodingFailure at .dataProduct: Attempt to decode value on failed cursor)"
        )
      )
    )
    assert(actual == expected)
  }

  test("unprovision return an Left(ValidationError) on invalid request") {
    (validationHandler.validate _).expects(*).once().returns(Right(invalid("x")))
    val actual   = provisionerController.unprovision(request)
    val expected = Left(ValidationError(Seq("x")))
    assert(actual == expected)
  }
}
