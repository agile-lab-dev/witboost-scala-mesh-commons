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

  /** Information related to the provisioning workflow of a data product component
    * @param request Provisioning descriptor of type `COMPONENT_DESCRIPTOR` in YAML format. It had been used to provision the data product component
    * @param result Result message (e.g. a provisiong error or a success message returned by the specific provisioner in the ProvisioningStatus
    */
  final case class ProvisionInfo(
    request: String,
    result: String
  )

  /** An update acl request containing the list of refs and the provisioning info
    * @param refs Identities (i.e. users and groups) involved in the ACL update request
    * @param provisionInfo Information related to the provisioning workflow
    */
  final case class UpdateAclRequest(
    refs: Seq[String],
    provisionInfo: ProvisionInfo
  ) extends ApiRequest

}
