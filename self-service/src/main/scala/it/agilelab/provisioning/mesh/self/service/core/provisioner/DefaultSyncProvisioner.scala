package it.agilelab.provisioning.mesh.self.service.core.provisioner

import cats.implicits.toBifunctorOps
import io.circe.Encoder
import it.agilelab.provisioning.commons.principalsmapping.CdpIamPrincipals
import it.agilelab.provisioning.commons.support.ParserSupport
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ completed, ProvisioningStatus }
import it.agilelab.provisioning.mesh.self.service.core.gateway.ComponentGateway
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand

/** Execute a sync provision
  * @param componentGateway: an instance of [[ComponentGateway]] that execute the sync provision
  * @tparam DP_SPEC: DataProduct type parameter
  * @tparam COMPONENT_SPEC: Component type parameter
  */
class DefaultSyncProvisioner[DP_SPEC, COMPONENT_SPEC, RESOURCE, PRINCIPAL <: CdpIamPrincipals](
  componentGateway: ComponentGateway[DP_SPEC, COMPONENT_SPEC, RESOURCE, PRINCIPAL]
)(implicit encoder: Encoder[RESOURCE])
    extends Provisioner[DP_SPEC, COMPONENT_SPEC, PRINCIPAL]
    with ParserSupport {

  private val COMPLETE_PROVISION_ERROR   = "Unable to complete provision. Component gateway error: %s"
  private val COMPLETE_UNPROVISION_ERROR = "Unable to complete unprovision. Component gateway error: %s"
  private val COMPLETE_UPDATE_ACL_ERROR  = "Unable to complete update ACL. Component gateway error: %s"

  /** Execute the provision logic calling the create method of the component gateway.
    * It basically wrap the component gateway and map the result as completed and any left as provisioner error.
    *
    * @param provisionCommand: A [[ProvisionCommand]] instance
    * @return Right(ProvisioningStatus)
    *         Left(ProvisionerError)
    */
  override def provision(
    provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]
  ): Either[ProvisionerError, ProvisioningStatus] =
    componentGateway
      .create(provisionCommand)
      .map(resource => completed(provisionCommand.requestId, Some(toJson[RESOURCE](resource))))
      .leftMap(e => ProvisionerError(COMPLETE_PROVISION_ERROR.format(e.error)))

  /** Execute the unprovision logic calling the destroy method of the component gateway.
    * It basically wrap the component gateway and map the result as completed and any left as provisioner error.
    *
    * @param provisionCommand: A [[ProvisionCommand]] instance
    * @return Right(ProvisioningStatus)
    *         Left(ProvisionerError)
    */
  override def unprovision(
    provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]
  ): Either[ProvisionerError, ProvisioningStatus] =
    componentGateway
      .destroy(provisionCommand)
      .map(resource => completed(provisionCommand.requestId, Some(toJson[RESOURCE](resource))))
      .leftMap(e => ProvisionerError(COMPLETE_UNPROVISION_ERROR.format(e.error)))

  /** Executes the update ACL logic calling the updateACL method of the component gateway
    *
    * @param provisionCommand A [[ProvisionCommand]] instance
    * @param refs             A list of refs to be granted access
    * @return Right(ProvisioningStatus)
    *         Left(ProvisionerError)
    */
  override def updateAcl(
    provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC],
    refs: Set[PRINCIPAL]
  ): Either[ProvisionerError, ProvisioningStatus] =
    componentGateway
      .updateAcl(provisionCommand, refs)
      .map(resource => completed(provisionCommand.requestId, Some(toJson[RESOURCE](resource))))
      .leftMap(e => ProvisionerError(COMPLETE_UPDATE_ACL_ERROR.format(e.error)))
}
