package it.agilelab.provisioning.mesh.self.service.lambda.core.model

import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand

final case class ComponentServiceCommand[DP_SPEC, COMPONENT_SPEC](
  operation: ComponentOperation,
  command: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]
)
