package it.agilelab.provisioning.mesh.self.service.api.controller

import it.agilelab.provisioning.commons.principalsmapping.{ CdpIamPrincipals, PrincipalsMapper }
import it.agilelab.provisioning.commons.validator.Validator
import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ProvisionRequest
import it.agilelab.provisioning.mesh.self.service.core.provisioner.Provisioner
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class ProvisionerControllerTest extends AnyFunSuite with MockFactory {

  test("default") {
    val validator        = mock[Validator[ProvisionRequest[String, String]]]
    val provisioner      = mock[Provisioner[String, String, CdpIamPrincipals]]
    val repository       = mock[Repository[ProvisioningStatus, String, Unit]]
    val principalsMapper = mock[PrincipalsMapper[CdpIamPrincipals]]
    val actual           = ProvisionerController.defaultAcl[String, String, CdpIamPrincipals](
      validator,
      provisioner,
      repository,
      principalsMapper
    )
    assert(actual.isInstanceOf[DefaultProvisionerController[String, String, CdpIamPrincipals]])
  }

  test("defaultWithAudit") {
    val validator        = mock[Validator[ProvisionRequest[String, String]]]
    val provisioner      = mock[Provisioner[String, String, CdpIamPrincipals]]
    val repository       = mock[Repository[ProvisioningStatus, String, Unit]]
    val principalsMapper = mock[PrincipalsMapper[CdpIamPrincipals]]
    val actual           = ProvisionerController.defaultAclWithAudit[String, String, CdpIamPrincipals](
      validator,
      provisioner,
      repository,
      principalsMapper
    )
    assert(actual.isInstanceOf[DefaultProvisionerControllerWithAudit[String, String, CdpIamPrincipals]])
  }

  test("defaultNoAcl") {
    val validator   = mock[Validator[ProvisionRequest[String, String]]]
    val provisioner = mock[Provisioner[String, String, CdpIamPrincipals]]
    val repository  = mock[Repository[ProvisioningStatus, String, Unit]]
    val actual      = ProvisionerController.defaultNoAcl[String, String](
      validator,
      provisioner,
      repository
    )
    assert(actual.isInstanceOf[DefaultProvisionerController[String, String, CdpIamPrincipals]])
  }

  test("defaultNoAclWithAudit") {
    val validator   = mock[Validator[ProvisionRequest[String, String]]]
    val provisioner = mock[Provisioner[String, String, CdpIamPrincipals]]
    val repository  = mock[Repository[ProvisioningStatus, String, Unit]]
    val actual      = ProvisionerController.defaultNoAclWithAudit[String, String](
      validator,
      provisioner,
      repository
    )
    assert(actual.isInstanceOf[DefaultProvisionerControllerWithAudit[String, String, CdpIamPrincipals]])
  }

}
