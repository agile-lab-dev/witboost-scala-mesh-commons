package it.agilelab.provisioning.mesh.self.service.api.handler.state

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ SystemError, ValidationError }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.{ COMPLETED, RUNNING }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultProvisionStateHandlerWithAuditTest extends AnyFunSuite with MockFactory {

  val audit: Audit                                     = mock[Audit]
  val baseProvisionStateHandler: ProvisionStateHandler = mock[ProvisionStateHandler]
  val provisionStateHandler                            = new DefaultProvisionStateHandlerWithAudit(baseProvisionStateHandler, audit)

  test("get call info on success") {
    (baseProvisionStateHandler.get _).expects("x").returns(Right(ProvisioningStatus("x", COMPLETED, Some("x"))))
    (audit.info _).expects("Get provision status with id: x completed successfully").once()

    val actual   = provisionStateHandler.get("x")
    val expected = Right(ProvisioningStatus("x", COMPLETED, Some("x")))
    assert(actual == expected)
  }

  test("get call error on failure") {
    (baseProvisionStateHandler.get _).expects("x").returns(Left(ValidationError(Seq("x"))))
    (audit.error _).expects("Get provision status with id: x failed. Details: ValidationError(List(x))").once()

    val actual   = provisionStateHandler.get("x")
    val expected = Left(ValidationError(Seq("x")))
    assert(actual == expected)
  }
}
