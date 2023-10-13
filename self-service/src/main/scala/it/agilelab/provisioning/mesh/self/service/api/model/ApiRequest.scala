package it.agilelab.provisioning.mesh.self.service.api.model

import cats.Show

/** ApiRequest sealed trait
  * A hierarchical representation of the Api requests
  */
sealed trait ApiRequest extends Product with Serializable

object ApiRequest {

  /** ProvisioningRequest
    *
    * A provision request on yaml format.
    *
    * @param descriptor: a Yaml [[String]] that define the provision request
    */
  final case class ProvisioningRequest(
    descriptor: String
  ) extends ApiRequest

  implicit def showProvisioningRequest: Show[ProvisioningRequest] = Show.fromToString[ProvisioningRequest]
}
