package it.agilelab.provisioning.mesh.self.service.core.provisioner

import cats.implicits.toBifunctorOps
import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ running, ProvisioningStatus }
import it.agilelab.provisioning.mesh.self.service.core.gateway.ComponentGateway
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import io.circe.Encoder

/** Execute an async provision
  * @param provisioningStatusRepo: A [[Repository]] of [[ProvisioningStatus]] that will be used to manage the async state of the provision
  * @param componentGateway: A [[ComponentGateway]] instance that will be used to create the component(s)
  * @tparam DP_SPEC: DataProduct type parameter
  * @tparam COMPONENT_SPEC: Component type parameter
  */
class DefaultAsyncProvisioner[DP_SPEC, COMPONENT_SPEC, RESOURCE](
  provisioningStatusRepo: Repository[ProvisioningStatus, String, Unit],
  componentGateway: ComponentGateway[DP_SPEC, COMPONENT_SPEC, RESOURCE]
) extends Provisioner[DP_SPEC, COMPONENT_SPEC] {

  /** Execute the provision as follow:
    * Set the provision logic to running.
    * Call the componentGateway
    * and return a running provision command
    * @param provisionCommand: A [[ProvisionCommand]] instance
    * @return Right(ProvisioningStatus)
    *         Left(ProvisionerError)
    */
  override def provision(
    provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]
  ): Either[ProvisionerError, ProvisioningStatus] =
    for {
      status <- upsertStatus(running(provisionCommand.requestId))
      _      <- componentGateway
                  .create(provisionCommand)
                  .leftMap { e =>
                    upsertStatus(
                      status.failed(s"Unable to execute component gateway for provided request. Details: ${e.error}")
                    )
                    ProvisionerError(s"Unable to execute component gateway for provided request. Details: ${e.error}")
                  }
    } yield status

  /** Execute the unprovision as follow:
    * Set the provision logic to running.
    * Call the componentGateway
    * and return a running provision command
    * @param provisionCommand: A [[ProvisionCommand]] instance
    * @return Right(ProvisioningStatus)
    *         Left(ProvisionerError)
    */
  override def unprovision(
    provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]
  ): Either[ProvisionerError, ProvisioningStatus] =
    for {
      status <- upsertStatus(running(provisionCommand.requestId))
      _      <- componentGateway
                  .destroy(provisionCommand)
                  .leftMap { e =>
                    upsertStatus(
                      status.failed(s"Unable to execute component gateway for provided request. Details: ${e.error}")
                    )
                    ProvisionerError(s"Unable to execute component gateway for provided request. Details: ${e.error}")
                  }
    } yield status

  private def upsertStatus(provisioningStatus: ProvisioningStatus): Either[ProvisionerError, ProvisioningStatus] =
    provisioningStatusRepo.findById(provisioningStatus.id) match {
      case Right(Some(_)) =>
        provisioningStatusRepo
          .update(provisioningStatus)
          .map(_ => provisioningStatus)
          .leftMap(_ => ProvisionerError("Unable to update provisioning status with provided repository"))
      case _              =>
        provisioningStatusRepo
          .create(provisioningStatus)
          .map(_ => provisioningStatus)
          .leftMap(_ => ProvisionerError("Unable to create provisioning status with provided repository"))
    }

}
