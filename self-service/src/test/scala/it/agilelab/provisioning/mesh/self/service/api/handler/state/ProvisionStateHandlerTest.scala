package it.agilelab.provisioning.mesh.self.service.api.handler.state

import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class ProvisionStateHandlerTest extends AnyFunSuite with MockFactory {

  test("default") {
    val repository = mock[Repository[ProvisioningStatus, String, Unit]]
    val actual     = ProvisionStateHandler.default(repository)
    assert(actual.isInstanceOf[DefaultProvisionStateHandler])
  }
}
