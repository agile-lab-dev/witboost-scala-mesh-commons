package it.agilelab.provisioning.mesh.self.service.core.gateway

import it.agilelab.provisioning.commons.principalsmapping.CdpIamPrincipals
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand

trait ComponentGateway[DP_SPEC, COMPONENT_SPEC, RESOURCE, PRINCIPAL <: CdpIamPrincipals] {
  def create(provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]): Either[ComponentGatewayError, RESOURCE]

  def destroy(provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]): Either[ComponentGatewayError, RESOURCE]

  def updateAcl(
    provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC],
    refs: Set[PRINCIPAL]
  ): Either[ComponentGatewayError, RESOURCE]
}

trait PermissionlessComponentGateway[DP_SPEC, COMPONENT_SPEC, RESOURCE]
    extends ComponentGateway[DP_SPEC, COMPONENT_SPEC, RESOURCE, CdpIamPrincipals] {

  override def updateAcl(
    provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC],
    refs: Set[CdpIamPrincipals]
  ): Either[ComponentGatewayError, RESOURCE] = Left(ComponentGatewayError("Update ACL is not a supported operation"))
}
