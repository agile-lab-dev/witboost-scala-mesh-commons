package it.agilelab.provisioning.mesh.self.service.api.model

import cats.data.NonEmptyList
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ SystemError, ValidationError }
import org.scalatest.funsuite.AnyFunSuite

class ApiErrorTest extends AnyFunSuite {

  test("sysErr return SystemError") {
    assert(ApiError.sysErr("x") == SystemError("x"))
  }

  test("validErr return ValidationError") {
    assert(ApiError.validErr("x", "y") == ValidationError(Seq("x", "y")))
  }

  test("validErr with Nel of objects return ValidationError") {
    def toListString(s: String): List[String] = List(s"Error: $s")

    assert(
      ApiError.validErrNel(NonEmptyList.of("err1", "err2"))(toListString) == ValidationError(
        Seq("Error: err1", "Error: err2")
      )
    )

  }
}
