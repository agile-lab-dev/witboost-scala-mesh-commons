package it.agilelab.provisioning.mesh.self.service.api.handler.provision

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.identifier.IDGenerator
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
trait ProvisionHandler[DP_SPEC, COMPONENT_SPEC] {

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
}

object ProvisionHandler {

  /** Create a [[DefaultProvisionHandler]]
    *
    * @param provisioner: a [[Provisioner]] instance
    * @tparam DP_SPEC: DataProduct type parameter
    * @tparam COMPONENT_SPEC: Component type parameter
    * @return
    */
  def default[DP_SPEC, COMPONENT_SPEC](
    provisioner: Provisioner[DP_SPEC, COMPONENT_SPEC]
  ): ProvisionHandler[DP_SPEC, COMPONENT_SPEC] =
    new DefaultProvisionHandler[DP_SPEC, COMPONENT_SPEC](
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
  def defaultWithAudit[DP_SPEC, COMPONENT_SPEC](
    provisioner: Provisioner[DP_SPEC, COMPONENT_SPEC]
  ): ProvisionHandler[DP_SPEC, COMPONENT_SPEC] =
    new DefaultProvisionHandlerWithAudit[DP_SPEC, COMPONENT_SPEC](
      default[DP_SPEC, COMPONENT_SPEC](provisioner),
      Audit.default("ProvisionHandler")
    )
}
