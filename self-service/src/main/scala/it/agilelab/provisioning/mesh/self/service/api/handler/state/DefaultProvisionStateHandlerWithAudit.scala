package it.agilelab.provisioning.mesh.self.service.api.handler.state

import cats.implicits.showInterpolator
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ ProvisioningStatus, Status }

class DefaultProvisionStateHandlerWithAudit(
  provisionStateHandler: ProvisionStateHandler,
  audit: Audit
) extends ProvisionStateHandler {

  /** Retrieve a [[Status]] starting from a specific provision id
    *
    * @param id  : The id of the provision request
    * @return Right(Status)
    *         Left(SystemError)
    *         Left(ValidationError)
    */
  override def get(id: String): Either[ApiError, ProvisioningStatus] = {
    val result = provisionStateHandler.get(id)
    result match {
      case Right(_) => audit.info(show"Get provision status with id: $id completed successfully")
      case Left(e)  => audit.error(show"Get provision status with id: $id failed. Details: $e")
    }
    result
  }

}
