package it.agilelab.provisioning.mesh.self.service.lambda.core.model

/** ProvisionResponse
  * Define a Provision response
  * @param details: Details generic entity
  * @tparam A: details type paramenters
  */
final case class ProvisionResponse[A](details: A)
