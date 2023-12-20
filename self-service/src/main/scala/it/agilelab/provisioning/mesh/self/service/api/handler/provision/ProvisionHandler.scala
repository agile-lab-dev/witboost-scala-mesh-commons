package it.agilelab.provisioning.mesh.self.service.api.handler.provision

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.identifier.IDGenerator
import it.agilelab.provisioning.commons.principalsmapping.CdpIamPrincipals
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.SystemError
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ProvisionRequest
import it.agilelab.provisioning.mesh.self.service.core.provisioner.Provisioner

/** ProvisionHandler
  *
  * Handle the provision process for a specific [[ProvisionRequest]]
  *
  * @tparam DP_SPEC: DataProduct type parameter
  * @tparam COMPONENT_SPEC: Component type parameter
  */
trait ProvisionHandler[DP_SPEC, COMPONENT_SPEC, PRINCIPAL <: CdpIamPrincipals] {

  /** Handle the provision process
    * @param provisionRequest: Incoming [[ProvisionRequest]]
    * @return Right(ProvisioningStatus)
    *         Left(SystemError)
    */
  def provision(
    provisionRequest: ProvisionRequest[DP_SPEC, COMPONENT_SPEC]
  ): Either[SystemError, ProvisioningStatus]

  /** Handle the unprovision process
    * @param provisionRequest: Incoming [[ProvisionRequest]]
    * @return Right(ProvisioningStatus)
    *         Left(SystemError)
    */
  def unprovision(
    provisionRequest: ProvisionRequest[DP_SPEC, COMPONENT_SPEC]
  ): Either[SystemError, ProvisioningStatus]

  /** Handles the update ACL process
    * @param provisionRequest Incoming [[ProvisionRequest]]
    * @param refs List of refs to be granted access
    * @return Right(ProvisioningStatus)
    *         Left(SystemError)
    */
  def updateAcl(
    provisionRequest: ProvisionRequest[DP_SPEC, COMPONENT_SPEC],
    refs: Set[PRINCIPAL]
  ): Either[SystemError, ProvisioningStatus]
}

object ProvisionHandler {

  /** Create a [[DefaultProvisionHandler]]
    *
    * @param provisioner: a [[Provisioner]] instance
    * @tparam DP_SPEC: DataProduct type parameter
    * @tparam COMPONENT_SPEC: Component type parameter
    * @return
    */
  def default[DP_SPEC, COMPONENT_SPEC, PRINCIPAL <: CdpIamPrincipals](
    provisioner: Provisioner[DP_SPEC, COMPONENT_SPEC, PRINCIPAL]
  ): ProvisionHandler[DP_SPEC, COMPONENT_SPEC, PRINCIPAL] =
    new DefaultProvisionHandler[DP_SPEC, COMPONENT_SPEC, PRINCIPAL](
      IDGenerator.defaultWithTimestamp(),
      provisioner
    )

  /** Create a [[DefaultProvisionHandlerWithAudit]]
    *
    * @param provisioner: a [[Provisioner]] instance
    * @tparam DP_SPEC: DataProduct type parameter
    * @tparam COMPONENT_SPEC: Component type parameter
    * @return
    */
  def defaultWithAudit[DP_SPEC, COMPONENT_SPEC, PRINCIPAL <: CdpIamPrincipals](
    provisioner: Provisioner[DP_SPEC, COMPONENT_SPEC, PRINCIPAL]
  ): ProvisionHandler[DP_SPEC, COMPONENT_SPEC, PRINCIPAL] =
    new DefaultProvisionHandlerWithAudit[DP_SPEC, COMPONENT_SPEC, PRINCIPAL](
      default[DP_SPEC, COMPONENT_SPEC, PRINCIPAL](provisioner),
      Audit.default("ProvisionHandler")
    )
}
