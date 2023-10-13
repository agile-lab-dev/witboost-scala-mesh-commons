package it.agilelab.provisioning.mesh.self.service.core.model

import it.agilelab.provisioning.mesh.self.service.api.model.ProvisionRequest

final case class ProvisionCommand[DP_SPEC, COMPONENT_SPEC](
  requestId: String,
  provisionRequest: ProvisionRequest[DP_SPEC, COMPONENT_SPEC]
)
