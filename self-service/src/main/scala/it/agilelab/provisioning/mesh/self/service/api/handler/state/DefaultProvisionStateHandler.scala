package it.agilelab.provisioning.mesh.self.service.api.handler.state

import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.repository.RepositoryError.FindEntityByIdErr
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError._
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ ProvisioningStatus, Status }

class DefaultProvisionStateHandler(
  repository: Repository[ProvisioningStatus, String, Unit]
) extends ProvisionStateHandler {

  override def get(id: String): Either[ApiError, ProvisioningStatus] =
    repository.findById(id) match {
      case Right(Some(p))                => Right(p)
      case Right(None)                   => Left(validErr(s"Provision state with id: $id not found."))
      case Left(FindEntityByIdErr(_, e)) => Left(sysErr(s"Get provision $id fail. An exception was raised: ${str(e)}"))
      case Left(_)                       => Left(sysErr(s"Get provision $id fail. An exception was raised"))
    }

  private def str(throwable: Throwable): String =
    s"${throwable.getClass.getCanonicalName} ${throwable.getLocalizedMessage}"
}
