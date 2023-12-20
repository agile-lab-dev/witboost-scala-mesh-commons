package it.agilelab.provisioning.mesh.self.service.api.handler.provision

import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.SystemError
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.RUNNING
import it.agilelab.provisioning.mesh.self.service.api.model.Component.Workload
import it.agilelab.provisioning.mesh.self.service.api.model.{ DataProduct, ProvisionRequest }
import io.circe.Json
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.principalsmapping.{ CdpIamPrincipals, CdpIamUser }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultProvisionHandlerWithAuditTest extends AnyFunSuite with MockFactory {

  val audit: Audit                                                             = mock[Audit]
  val baseProvisionHandler: ProvisionHandler[String, String, CdpIamPrincipals] =
    mock[ProvisionHandler[String, String, CdpIamPrincipals]]
  val provisionHandler                                                         =
    new DefaultProvisionHandlerWithAudit[String, String, CdpIamPrincipals](baseProvisionHandler, audit)
  val request: ProvisionRequest[String, String]                                = ProvisionRequest(
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

  val refs: Set[CdpIamPrincipals] = Set(
    CdpIamUser("", "user1", ""),
    CdpIamUser("", "user2", "")
  )

  test("provision call info on success") {
    (baseProvisionHandler.provision _)
      .expects(request)
      .returns(Right(ProvisioningStatus("x", RUNNING, None)))
    (audit.info _)
      .expects("Provision request completed successfully")
      .once()

    val actual   = provisionHandler.provision(request)
    val expected = Right(ProvisioningStatus("x", RUNNING, None))
    assert(actual == expected)
  }

  test("provision call error on failure") {
    (baseProvisionHandler.provision _)
      .expects(request)
      .returns(Left(SystemError("error")))
    (audit.error _)
      .expects("Provision request failed. Details SystemError(error)")
      .once()

    val actual   = provisionHandler.provision(request)
    val expected = Left(SystemError("error"))
    assert(actual == expected)
  }

  test("unprovision call info on success") {
    (baseProvisionHandler.unprovision _)
      .expects(request)
      .returns(Right(ProvisioningStatus("x", RUNNING, None)))
    (audit.info _)
      .expects("Unprovision request completed successfully")
      .once()

    val actual   = provisionHandler.unprovision(request)
    val expected = Right(ProvisioningStatus("x", RUNNING, None))
    assert(actual == expected)
  }

  test("unprovision call error on failure") {
    (baseProvisionHandler.unprovision _)
      .expects(request)
      .returns(Left(SystemError("error")))
    (audit.error _)
      .expects("Unprovision request failed. Details SystemError(error)")
      .once()

    val actual   = provisionHandler.unprovision(request)
    val expected = Left(SystemError("error"))
    assert(actual == expected)
  }

  test("updateAcl call info on success") {
    (audit.info _)
      .expects("Received refs { CdpIamUser(,user1,), CdpIamUser(,user2,) }")
    (baseProvisionHandler.updateAcl _)
      .expects(request, refs)
      .returns(Right(ProvisioningStatus("x", RUNNING, None)))
    (audit.info _)
      .expects("Update ACL request completed successfully")
      .once()

    val actual   = provisionHandler.updateAcl(request, refs)
    val expected = Right(ProvisioningStatus("x", RUNNING, None))
    assert(actual == expected)
  }

  test("updateAcl call error on failure") {
    (baseProvisionHandler.updateAcl _)
      .expects(request, refs)
      .returns(Left(SystemError("error")))
    (audit.error _)
      .expects("Update ACL request failed. Details SystemError(error)")
      .once()
    (audit.info _)
      .expects("Received refs { CdpIamUser(,user1,), CdpIamUser(,user2,) }")

    val actual   = provisionHandler.updateAcl(request, refs)
    val expected = Left(SystemError("error"))
    assert(actual == expected)
  }

}
