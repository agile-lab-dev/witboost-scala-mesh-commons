package it.agilelab.provisioning.mesh.self.service.api.controller

import io.circe.Decoder
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.principalsmapping.CdpIamPrincipals
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ sysErr, SystemError }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiRequest.{
  ProvisionInfo,
  ProvisioningRequest,
  UpdateAclRequest
}
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.{ COMPLETED, RUNNING }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ valid, ProvisioningStatus, ValidationResult }
import it.agilelab.provisioning.mesh.self.service.api.model.{ Component, ProvisioningDescriptor }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultProvisionerControllerWithAuditTest extends AnyFunSuite with MockFactory {

  val audit: Audit                                                                       = mock[Audit]
  val baseProvisionerController: ProvisionerController[String, String, CdpIamPrincipals] =
    mock[ProvisionerController[String, String, CdpIamPrincipals]]

  val provisionerController = new DefaultProvisionerControllerWithAudit[String, String, CdpIamPrincipals](
    baseProvisionerController,
    audit
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
      |component:
      |  id: my-dp-component-id
      |  name: my-dp-comonent-name
      |  fullyQualifiedName: my-dp-component-fully-qualified-name
      |  description: my-dp-component-description
      |  kind: my-dp-component-kind
      |  workloadType: my-dp-component-type
      |  connectionType: my-dp-component-connection-type
      |  technology: my-dp-component-technology
      |  platform: my-dp-platform
      |  version: my-dp-component-version
      |  infrastructureTemplateId: my-dp-component-infra-template-id
      |  useCaseTemplateId: my-dp-component-infra-use-case-id
      |  tags: []
      |  readsFrom: []
      |  specific: update
      |""".stripMargin
  )

  val refs             = List("user:user1_agilelab.it", "user:user2_agilelab.it")
  val updateAclRequest = UpdateAclRequest(refs, ProvisionInfo(request.descriptor, "response"))

  test("validate call info on success") {
    (baseProvisionerController
      .validate(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .expects(*, *, *)
      .once()
      .returns(Right(valid()))

    (audit.info _)
      .expects("Validate completed successfully")
      .once()

    val actual = provisionerController.validate(
      request
    )

    val expected = Right(ValidationResult(valid = true, None))
    assert(actual == expected)
  }

  test("validate call error on failure") {
    (baseProvisionerController
      .validate(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .expects(*, *, *)
      .once()
      .returns(Left(sysErr("error")))

    (audit.error _)
      .expects("Validate failed. Details: SystemError(error)")
      .once()

    val actual = provisionerController.validate(
      request
    )

    val expected = Left(sysErr("error"))
    assert(actual == expected)
  }

  test("provision call info on success") {
    (baseProvisionerController
      .provision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .expects(*, *, *)
      .once()
      .returns(Right(ProvisioningStatus("x", RUNNING, None)))

    (audit.info _)
      .expects("Provision completed successfully")
      .once()

    val actual   = provisionerController.provision(
      request
    )
    val expected = Right(ProvisioningStatus("x", RUNNING, None))
    assert(actual == expected)
  }

  test("provision call error on failure") {
    (baseProvisionerController
      .provision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .expects(*, *, *)
      .once()
      .returns(Left(sysErr("error")))

    (audit.error _)
      .expects("Provision failed. Details: SystemError(error)")
      .once()

    val actual   = provisionerController.provision(
      request
    )
    val expected = Left(sysErr("error"))
    assert(actual == expected)
  }

  test("getProvisionStatus call info on success") {
    (baseProvisionerController.getProvisionStatus _)
      .expects("x")
      .returns(Right(ProvisioningStatus("x", COMPLETED, Some("x"))))
    (audit.info _)
      .expects("Get provision state with id: x completed successfully")
      .once()

    val actual   = provisionerController.getProvisionStatus("x")
    val expected = Right(ProvisioningStatus("x", COMPLETED, Some("x")))
    assert(actual == expected)
  }

  test("getProvisionStatus call error on success") {
    (baseProvisionerController.getProvisionStatus _)
      .expects("x")
      .returns(Left(sysErr("error")))
    (audit.error _)
      .expects("Get provision state with id: x failed. Details SystemError(error)")
      .once()

    val actual   = provisionerController.getProvisionStatus("x")
    val expected = Left(SystemError("error"))
    assert(actual == expected)
  }

  test("unprovision call info on success") {
    (baseProvisionerController
      .unprovision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .expects(*, *, *)
      .once()
      .returns(Right(ProvisioningStatus("x", RUNNING, None)))

    (audit.info _)
      .expects("Unprovision completed successfully")
      .once()

    val actual   = provisionerController.unprovision(
      request
    )
    val expected = Right(ProvisioningStatus("x", RUNNING, None))
    assert(actual == expected)
  }

  test("unprovision call error on failure") {
    (baseProvisionerController
      .unprovision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .expects(*, *, *)
      .once()
      .returns(Left(sysErr("error")))

    (audit.error _)
      .expects("Unprovision failed. Details: SystemError(error)")
      .once()

    val actual   = provisionerController.unprovision(
      request
    )
    val expected = Left(sysErr("error"))
    assert(actual == expected)
  }

  test("updateAcl call info on success") {
    (baseProvisionerController
      .updateAcl(_: UpdateAclRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .expects(*, *, *)
      .once()
      .returns(Right(ProvisioningStatus("x", RUNNING, None)))

    (audit.info _)
      .expects("Update ACL completed successfully")
      .once()

    val actual   = provisionerController.updateAcl(
      updateAclRequest
    )
    val expected = Right(ProvisioningStatus("x", RUNNING, None))
    assert(actual == expected)
  }

  test("updateAcl call error on failure") {
    (baseProvisionerController
      .updateAcl(_: UpdateAclRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .expects(*, *, *)
      .once()
      .returns(Left(sysErr("error")))

    (audit.error _)
      .expects("Update ACL failed. Details: SystemError(error)")
      .once()

    val actual   = provisionerController.updateAcl(
      updateAclRequest
    )
    val expected = Left(sysErr("error"))
    assert(actual == expected)
  }

}
