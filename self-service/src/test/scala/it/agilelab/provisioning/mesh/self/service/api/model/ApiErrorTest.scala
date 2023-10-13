package it.agilelab.provisioning.mesh.self.service.api.model

import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ SystemError, ValidationError }
import org.scalatest.funsuite.AnyFunSuite

class ApiErrorTest extends AnyFunSuite {

  test("sysErr return SystemError") {
    assert(ApiError.sysErr("x") == SystemError("x"))
  }

  test("validErr return SystemError") {
    assert(ApiError.validErr("x", "y") == ValidationError(Seq("x", "y")))
  }
}
