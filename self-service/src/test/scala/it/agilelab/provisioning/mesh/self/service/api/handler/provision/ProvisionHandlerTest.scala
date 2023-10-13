package it.agilelab.provisioning.mesh.self.service.api.handler.provision

import it.agilelab.provisioning.mesh.self.service.core.provisioner.Provisioner
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class ProvisionHandlerTest extends AnyFunSuite with MockFactory {

  test("default") {
    val provisioner = mock[Provisioner[String, String]]
    val actual      = ProvisionHandler.default[String, String](provisioner)
    assert(actual.isInstanceOf[DefaultProvisionHandler[String, String]])
  }

  test("defaultWithAudit") {
    val provisioner = mock[Provisioner[String, String]]
    val actual      = ProvisionHandler.defaultWithAudit[String, String](provisioner)
    assert(actual.isInstanceOf[DefaultProvisionHandlerWithAudit[String, String]])
  }
}
