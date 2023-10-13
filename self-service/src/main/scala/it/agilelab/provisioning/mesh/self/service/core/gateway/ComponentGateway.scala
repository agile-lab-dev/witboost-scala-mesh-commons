package it.agilelab.provisioning.mesh.self.service.core.gateway

import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand

trait ComponentGateway[DP_SPEC, COMPONENT_SPEC, RESOURCE] {
  def create(provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]): Either[ComponentGatewayError, RESOURCE]

  def destroy(provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]): Either[ComponentGatewayError, RESOURCE]
}
