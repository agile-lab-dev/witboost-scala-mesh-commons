package it.agilelab.provisioning.mesh.self.service.api.handler.validation

import cats.data.Validated.Invalid
import cats.data.{ NonEmptyList, Validated }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ SystemError, ValidationError }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ValidationResult
import it.agilelab.provisioning.mesh.self.service.api.model.Component.Workload
import it.agilelab.provisioning.mesh.self.service.api.model.{ DataProduct, ProvisionRequest }
import io.circe.Json
import it.agilelab.provisioning.commons.validator.{ ValidationFail, Validator, ValidatorError }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultValidationHandlerTest extends AnyFunSuite with MockFactory {

  val validator: Validator[ProvisionRequest[String, String]] = mock[Validator[ProvisionRequest[String, String]]]
  val validationHandler                                      = new DefaultValidationHandler[String, String](validator)

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

  test("validate return Left(SystemErr) on ValidatorError") {
    (validator.validate _)
      .expects(*)
      .once()
      .returns(Left(ValidatorError(request, new IllegalArgumentException("x"))))

    val actual   = validationHandler.validate(request)
    val expected = Left(
      SystemError(
        "Validation fail: An exception was raised during request validation process. Exception: java.lang.IllegalArgumentException: x"
      )
    )
    assert(actual == expected)
  }

  test("validate return valid Right(ValidationResult)") {
    (validator.validate _)
      .expects(*)
      .once()
      .returns(Right(Validated.valid(request)))

    val actual   = validationHandler.validate(request)
    val expected = Right(ValidationResult(valid = true, None))

    assert(actual == expected)
  }

  test("validate return invalid Right(ValidationResult)") {
    (validator.validate _)
      .expects(*)
      .once()
      .returns(
        Right(
          Invalid(
            NonEmptyList(
              ValidationFail(request, "x"),
              List(ValidationFail(request, "y"))
            )
          )
        )
      )

    val actual   = validationHandler.validate(request)
    val expected = Right(ValidationResult(valid = false, Some(ValidationError(Seq("x", "y")))))

    assert(actual == expected)
  }

}
