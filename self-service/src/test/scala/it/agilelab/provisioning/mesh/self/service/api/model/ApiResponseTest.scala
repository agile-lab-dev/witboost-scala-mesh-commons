package it.agilelab.provisioning.mesh.self.service.api.model

import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.ValidationError
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.{ COMPLETED, FAILED, RUNNING }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse._
import org.scalatest.funsuite.AnyFunSuite

class ApiResponseTest extends AnyFunSuite {

  test("valid return ValidationResult(true,None)") {
    val actual   = ApiResponse.valid()
    val expected = ValidationResult(valid = true, None)
    assert(actual == expected)
  }

  test("invalid return ValidationResult(false,my error)") {
    val actual   = ApiResponse.invalid("my error")
    val expected = ValidationResult(valid = false, Some(ValidationError(Seq("my error"))))
    assert(actual == expected)
  }

  test(s"running return ProvisioningStatus(RUNNING,None)") {
    assert(running("x") == ProvisioningStatus("x", RUNNING, None))
  }

  test(s"failed return ProvisioningStatus(FAILED,my-err)") {
    assert(failed("x", Some("my-err")) == ProvisioningStatus("x", FAILED, Some("my-err")))
  }

  test(s"completed return ProvisioningStatus(COMPLETED,my-res)") {
    assert(completed("x", Some("my-res")) == ProvisioningStatus("x", COMPLETED, Some("my-res")))
  }
}
