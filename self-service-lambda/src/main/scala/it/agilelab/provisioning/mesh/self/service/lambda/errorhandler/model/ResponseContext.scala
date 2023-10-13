package it.agilelab.provisioning.mesh.self.service.lambda.errorhandler.model

final case class ResponseContext(statusCode: Int, executedVersion: String, functionError: String)
