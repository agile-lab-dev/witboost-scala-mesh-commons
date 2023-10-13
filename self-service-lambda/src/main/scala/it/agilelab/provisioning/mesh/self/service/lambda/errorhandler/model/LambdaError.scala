package it.agilelab.provisioning.mesh.self.service.lambda.errorhandler.model

import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand

final case class LambdaError[A, B](
  version: String,
  timestamp: String,
  requestContext: RequestContext,
  requestPayload: ProvisionCommand[A, B],
  responseContext: ResponseContext,
  responsePayload: ResponsePayload
)
