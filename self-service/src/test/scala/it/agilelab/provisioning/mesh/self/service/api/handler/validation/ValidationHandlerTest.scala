package it.agilelab.provisioning.mesh.self.service.api.handler.validation

import it.agilelab.provisioning.commons.validator.Validator
import it.agilelab.provisioning.mesh.self.service.api.model.ProvisionRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class ValidationHandlerTest extends AnyFunSuite with MockFactory {

  test("default") {
    val validator = mock[Validator[ProvisionRequest[String, String]]]
    val actual    = ValidationHandler.default[String, String](validator)
    assert(actual.isInstanceOf[DefaultValidationHandler[String, String]])
  }

  test("defaultWithAudit") {
    val validator = mock[Validator[ProvisionRequest[String, String]]]
    val actual    = ValidationHandler.defaultWithAudit[String, String](validator)
    assert(actual.isInstanceOf[DefaultValidationHandlerWithAudit[String, String]])
  }
}
