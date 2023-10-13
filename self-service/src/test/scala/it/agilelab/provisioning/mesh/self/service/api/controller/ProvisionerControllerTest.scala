package it.agilelab.provisioning.mesh.self.service.api.controller

import it.agilelab.provisioning.commons.validator.Validator
import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ProvisionRequest
import it.agilelab.provisioning.mesh.self.service.core.provisioner.Provisioner
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class ProvisionerControllerTest extends AnyFunSuite with MockFactory {

  test("default") {
    val validator   = mock[Validator[ProvisionRequest[String, String]]]
    val provisioner = mock[Provisioner[String, String]]
    val repository  = mock[Repository[ProvisioningStatus, String, Unit]]
    val actual      = ProvisionerController.default[String, String](
      validator,
      provisioner,
      repository
    )
    assert(actual.isInstanceOf[DefaultProvisionerController[String, String]])
  }

  test("defaultWithAudit") {
    val validator   = mock[Validator[ProvisionRequest[String, String]]]
    val provisioner = mock[Provisioner[String, String]]
    val repository  = mock[Repository[ProvisioningStatus, String, Unit]]
    val actual      = ProvisionerController.defaultWithAudit[String, String](
      validator,
      provisioner,
      repository
    )
    assert(actual.isInstanceOf[DefaultProvisionerControllerWithAudit[String, String]])
  }

}
