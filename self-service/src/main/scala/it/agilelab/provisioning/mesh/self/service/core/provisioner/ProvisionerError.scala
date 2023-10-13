package it.agilelab.provisioning.mesh.self.service.core.provisioner

final case class ProvisionerError(error: String) extends Exception with Product with Serializable
