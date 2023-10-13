package it.agilelab.provisioning.mesh.self.service.lambda.errorhandler.model

final case class RequestContext(
  requestId: String,
  functionArn: String,
  condition: String,
  approximateInvokeCount: Int
)
