package it.agilelab.provisioning.mesh.self.service.api.handler.state

import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ ProvisioningStatus, Status }

/** ProvisionStateHandler
  *
  * Handle the provision state and contains all logic for managing the provision state
  */
trait ProvisionStateHandler {

  /** Retrieve a [[Status]] starting from a specific provision id
    * @param id: The id of the provision request
    * @return Right(ProvisioningStatus)
    *         Left(SystemError)
    *         Left(ValidationError)
    */
  def get(id: String): Either[ApiError, ProvisioningStatus]
}

object ProvisionStateHandler {

  /** Create a default ProvisionStateHandler mainly based to a provisioning status repository
    * @param repository: a [[Repository]] instance for [[ProvisioningStatus]]
    * @return [[ProvisionStateHandler]]
    */
  def default(repository: Repository[ProvisioningStatus, String, Unit]): ProvisionStateHandler =
    new DefaultProvisionStateHandler(repository)
}
