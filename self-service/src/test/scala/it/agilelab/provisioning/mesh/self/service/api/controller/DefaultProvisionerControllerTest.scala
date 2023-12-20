package it.agilelab.provisioning.mesh.self.service.api.controller

import com.cloudera.cdp.CdpClientException
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.principalsmapping.PrincipalsMapperError.{
  PrincipalMappingError,
  PrincipalMappingSystemError
}
import it.agilelab.provisioning.commons.principalsmapping.{
  CdpIamPrincipals,
  CdpIamUser,
  ErrorMoreInfo,
  PrincipalsMapper
}
import it.agilelab.provisioning.mesh.self.service.api.handler.provision.ProvisionHandler
import it.agilelab.provisioning.mesh.self.service.api.handler.state.ProvisionStateHandler
import it.agilelab.provisioning.mesh.self.service.api.handler.validation.ValidationHandler
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ SystemError, ValidationError }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiRequest.{
  ProvisionInfo,
  ProvisioningRequest,
  UpdateAclRequest
}
import it.agilelab.provisioning.commons.client.cdp.iam.CdpIamClientError.GetUserErr
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.{ COMPLETED, RUNNING }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse._
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultProvisionerControllerTest extends AnyFunSuite with MockFactory {
  val validationHandler: ValidationHandler[String, String]                 = mock[ValidationHandler[String, String]]
  val provisionHandler: ProvisionHandler[String, String, CdpIamPrincipals] =
    mock[ProvisionHandler[String, String, CdpIamPrincipals]]
  val provisionStateHandler: ProvisionStateHandler                         = mock[ProvisionStateHandler]
  val principalsMapper: PrincipalsMapper[CdpIamPrincipals]                 = mock[PrincipalsMapper[CdpIamPrincipals]]

  val provisionerController = new DefaultProvisionerController[String, String, CdpIamPrincipals](
    validationHandler,
    provisionHandler,
    provisionStateHandler,
    principalsMapper
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
      |  devGroup: devGroup
      |  ownerGroup: ownerGroup
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

  val refs             = List("user:user1_agilelab.it", "user:user2_agilelab.it")
  val updateAclRequest = UpdateAclRequest(refs, ProvisionInfo(request.descriptor, "response"))

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

  test("update acl return a Right(ProvisioningStatus)") {
    val mappedRefs =
      Map(
        "user:user1_agilelab.it" -> Right(CdpIamUser("", "user1", "")),
        "user:user2_agilelab.it" -> Right(CdpIamUser("", "user2", ""))
      )

    (principalsMapper.map _).expects(*).once().returns(mappedRefs)
    (validationHandler.validate _).expects(*).once().returns(Right(valid()))
    (provisionHandler.updateAcl _)
      .expects(*, *)
      .once()
      .returns(Right(ProvisioningStatus("x", RUNNING, None)))
    val actual   = provisionerController.updateAcl(updateAclRequest)
    val expected = Right(ProvisioningStatus("x", RUNNING, None))
    assert(actual == expected)
  }

  test("updateAcl return a Left(SystemError)") {
    val mappedRefs =
      Map(
        "user:user1_agilelab.it" -> Right(CdpIamUser("", "user1", "")),
        "user:user2_agilelab.it" -> Right(CdpIamUser("", "user2", ""))
      )

    (principalsMapper.map _).expects(*).once().returns(mappedRefs)
    (validationHandler.validate _).expects(*).once().returns(Right(valid()))
    (provisionHandler.updateAcl _).expects(*, *).once().returns(Left(SystemError("x")))
    val actual   = provisionerController.updateAcl(updateAclRequest)
    val expected = Left(SystemError("x"))
    assert(actual == expected)
  }

  test("updateAcl return a Left(ValidationError) on parsing failure") {
    val actual   =
      provisionerController.updateAcl(UpdateAclRequest(refs, ProvisionInfo("""xxx""".stripMargin, "response")))
    val expected = Left(
      ValidationError(
        Seq(
          "DecodeErr(DecodingFailure at .dataProduct: Attempt to decode value on failed cursor)"
        )
      )
    )
    assert(actual == expected)
  }

  test("update acl return a Left(ValidationError) on validation error") {
    (validationHandler.validate _).expects(*).once().returns(Right(invalid("err1", "err2")))
    val actual   = provisionerController.updateAcl(updateAclRequest)
    val expected = Left(ValidationError(Seq("err1", "err2")))
    assert(actual == expected)
  }

  test("update acl return a Left(ValidationError) on mapping error") {
    val mappedRefs =
      Map(
        "user:user1_agilelab.it" -> Right(CdpIamUser("", "user1", "")),
        "user:user2_agilelab.it" -> Left(PrincipalMappingError(ErrorMoreInfo(List("error"), List.empty), None)),
        "user:user3_agilelab.it" -> Left(
          PrincipalMappingSystemError(
            ErrorMoreInfo(List("error2"), List("solution")),
            GetUserErr("user3", new CdpClientException("CdpError"))
          )
        )
      )

    (principalsMapper.map _).expects(*).once().returns(mappedRefs)
    (validationHandler.validate _).expects(*).once().returns(Right(valid()))
    (provisionHandler.updateAcl _)
      .expects(*, Set(CdpIamUser("", "user1", "")): Set[CdpIamPrincipals])
      .once()
      .returns(Right(ProvisioningStatus("x", RUNNING, None)))

    val actual   = provisionerController.updateAcl(updateAclRequest)
    println(actual.left.toOption.get.asInstanceOf[ValidationError].errors)
    val expected = Left(
      ValidationError(
        List("error", "error2", "GetUserErr(user3,com.cloudera.cdp.CdpClientException: CdpError")
      )
    )
    actual match {
      case Left(err: ValidationError) =>
        assert(err.errors.zip(expected.value.errors).forall(t => t._1.startsWith(t._2)))
    }
  }

}
