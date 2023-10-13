package it.agilelab.provisioning.mesh.self.service.api.handler.validation

import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ sysErr, SystemError }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.valid
import it.agilelab.provisioning.mesh.self.service.api.model.Component.Workload
import it.agilelab.provisioning.mesh.self.service.api.model.{ DataProduct, ProvisionRequest }
import io.circe.Json
import it.agilelab.provisioning.commons.audit.Audit
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultValidationHandlerWithAuditTest extends AnyFunSuite with MockFactory {
  val baseValidationHandler: ValidationHandler[String, String] = mock[ValidationHandler[String, String]]
  val audit: Audit                                             = mock[Audit]

  val validationHandler = new DefaultValidationHandlerWithAudit[String, String](
    baseValidationHandler,
    audit
  )

  private val request: ProvisionRequest[String, String] = ProvisionRequest(
    DataProduct[String](
      id = "my-dp-id",
      name = "my-dp-name",
      domain = "my-dp-domain",
      environment = "my-dp-environment",
      version = "my-dp-version",
      dataProductOwner = "my-dp-owner",
      specific = "my-dp-specific",
      components = Seq.empty[Json]
    ),
    Some(
      Workload[String](
        id = "my-dp-workload-id",
        name = "my-dp-workload-name",
        description = "my-dp-workload-description",
        version = "my-dp-workload-version",
        specific = "x"
      )
    )
  )

  test("validate call info on success") {
    (baseValidationHandler.validate _).expects(*).returns(Right(valid()))
    (audit.info _)
      .expects("validate completed successfully")
      .once()
    val actual   = validationHandler.validate(request)
    val expected = Right(valid())
    assert(actual == expected)
  }

  test("validate call error on failure") {
    (baseValidationHandler.validate _).expects(*).returns(Left(sysErr("x")))
    (audit.error _)
      .expects("validate failed. Details SystemError(x)")
      .once()
    val actual   = validationHandler.validate(request)
    val expected = Left(SystemError("x"))
    assert(actual == expected)
  }
}
