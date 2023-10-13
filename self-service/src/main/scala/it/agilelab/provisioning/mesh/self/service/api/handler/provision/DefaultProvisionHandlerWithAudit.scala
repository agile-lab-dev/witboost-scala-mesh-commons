package it.agilelab.provisioning.mesh.self.service.api.handler.provision

import cats.implicits.showInterpolator
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.mesh.self.service.api.model.{ ApiError, ApiResponse, ProvisionRequest }

class DefaultProvisionHandlerWithAudit[DP_SPEC, COMPONENT_SPEC](
  provisionHandler: ProvisionHandler[DP_SPEC, COMPONENT_SPEC],
  audit: Audit
) extends ProvisionHandler[DP_SPEC, COMPONENT_SPEC] {

  /** Handle the provision process
    *
    * @param provisionRequest  : Incoming [[ProvisionRequest]]
    * @return Right(ProvisioningStatus)
    *         Left(SystemError)
    */
  override def provision(
    provisionRequest: ProvisionRequest[DP_SPEC, COMPONENT_SPEC]
  ): Either[ApiError.SystemError, ApiResponse.ProvisioningStatus] = {
    val result = provisionHandler.provision(provisionRequest)
    result match {
      case Right(_) => audit.info(show"Provision request completed successfully")
      case Left(e)  => audit.error(show"Provision request failed. Details $e")
    }
    result
  }

  /** Handle the unprovision process
    *
    * @param provisionRequest  : Incoming [[ProvisionRequest]]
    * @return Right(ProvisioningStatus)
    *         Left(SystemError)
    */
  override def unprovision(
    provisionRequest: ProvisionRequest[DP_SPEC, COMPONENT_SPEC]
  ): Either[ApiError.SystemError, ApiResponse.ProvisioningStatus] = {
    val result = provisionHandler.unprovision(provisionRequest)
    result match {
      case Right(_) => audit.info(show"Unprovision request completed successfully")
      case Left(e)  => audit.error(show"Unprovision request failed. Details $e")
    }
    result
  }
}
