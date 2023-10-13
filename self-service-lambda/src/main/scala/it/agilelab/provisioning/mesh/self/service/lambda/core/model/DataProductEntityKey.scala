package it.agilelab.provisioning.mesh.self.service.lambda.core.model

/** DataProductKey model
  * @param domain: domain Id
  * @param dataProduct: data product Id
  */
final case class DataProductEntityKey(domain: String, dataProduct: String)
