package it.agilelab.provisioning.mesh.self.service.core.provisioner

import it.agilelab.provisioning.commons.principalsmapping.CdpIamPrincipals
import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.core.gateway.ComponentGateway
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class ProvisionerTest extends AnyFunSuite with MockFactory {

  val componentGatewayMock = mock[ComponentGateway[String, String, String, CdpIamPrincipals]]
  val stateRepoMock        = mock[Repository[ProvisioningStatus, String, Unit]]

  test("defaultSync") {
    val client = Provisioner.defaultSync(componentGatewayMock)
    assert(client.isInstanceOf[DefaultSyncProvisioner[_, _, _, _]])
  }

  test("defaultAsync") {
    val client = Provisioner.defaultAsync(stateRepoMock, componentGatewayMock)
    assert(client.isInstanceOf[DefaultAsyncProvisioner[_, _, _, _]])
  }

}
