package it.agilelab.provisioning.mesh.self.service.api.handler.provision

import cats.implicits.toBifunctorOps
import it.agilelab.provisioning.commons.identifier.IDGenerator
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ sysErr, SystemError }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ProvisionRequest
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import it.agilelab.provisioning.mesh.self.service.core.provisioner.{ Provisioner, ProvisionerError }

/** Default ProvisionHandler implementation
  * @param idGenerator: An instance of [[IDGenerator]] that will be used to random generate and attach to the provision request a unique id
  * @param provisioner: An instance of [[Provisioner]] that will be used to execute the provision logic
  * @tparam DP_SPECIFIC: DataProduct specific type parameter
  * @tparam COMPONENT_SPEC: Component specific type parameter
  */
class DefaultProvisionHandler[DP_SPECIFIC, COMPONENT_SPEC](
  idGenerator: IDGenerator,
  provisioner: Provisioner[DP_SPECIFIC, COMPONENT_SPEC]
) extends ProvisionHandler[DP_SPECIFIC, COMPONENT_SPEC] {

  private val ERROR             = "Unable to execute provision service: %s"
  private val UNPROVISION_ERROR = "Unable to execute unprovision service: %s"

  /** Handle the provision process
    * Generate [[ProvisionCommand]] request with a random generated id that unique identify the request and execute the provision logic
    * @param provisionRequest: Incoming [[ProvisionRequest]]
    * @return Right(ProvisioningStatus)
    *         Left(SystemError)
    */
  override def provision(
    provisionRequest: ProvisionRequest[DP_SPECIFIC, COMPONENT_SPEC]
  ): Either[SystemError, ProvisioningStatus] =
    provisioner
      .provision(ProvisionCommand(idGenerator.random(), provisionRequest))
      .leftMap(e => sysErr(ERROR.format(e.error)))

  /** Handle the unprovision process
    * Generate [[ProvisionCommand]] request with a random generated id that unique identify the request and execute the unprovision logic
    * @param provisionRequest: Incoming [[ProvisionRequest]]
    * @return Right(ProvisioningStatus)
    *         Left(SystemError)
    */
  override def unprovision(
    provisionRequest: ProvisionRequest[DP_SPECIFIC, COMPONENT_SPEC]
  ): Either[SystemError, ProvisioningStatus] =
    provisioner
      .unprovision(ProvisionCommand(idGenerator.random(), provisionRequest))
      .leftMap(e => sysErr(UNPROVISION_ERROR.format(e.error)))
}
