package it.agilelab.provisioning.mesh.self.service.core.gateway

final case class ComponentGatewayError(error: String) extends Exception with Product with Serializable
