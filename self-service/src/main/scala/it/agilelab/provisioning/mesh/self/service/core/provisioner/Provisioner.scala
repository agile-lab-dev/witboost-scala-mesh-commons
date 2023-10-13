package it.agilelab.provisioning.mesh.self.service.core.provisioner

import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.core.gateway.ComponentGateway
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import io.circe.Encoder

/** Provisioner instance that execute the provision logic
  * @tparam DP_SPEC: DataProduct type parameters
  * @tparam COMPONENT_SPEC: Component type parameters
  */
trait Provisioner[DP_SPEC, COMPONENT_SPEC] {

  /** Execute the provision logic
    * @param provisionCommand: A [[ProvisionCommand]] instance
    * @return Right(ProvisioningStatus)
    *         Left(ProvisionerError)
    */
  def provision(
    provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]
  ): Either[ProvisionerError, ProvisioningStatus]

  /** Execute the unprovision logic
    * @param provisionCommand: A [[ProvisionCommand]] instance
    * @return Right(ProvisioningStatus)
    *         Left(ProvisionerError)
    */
  def unprovision(
    provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]
  ): Either[ProvisionerError, ProvisioningStatus]

}

object Provisioner {

  /** Crate a default sync provisioner
    * @param componentGateway: An instance of [[ComponentGateway]] that execute logic for the specific component
    * @tparam DP_SPEC: DataProduct type parameter
    * @tparam COMPONENT_SPEC: Component type parameter
    * @tparam RESOURCE: Resource created by the component gateway
    * @return
    */
  def defaultSync[DP_SPEC, COMPONENT_SPEC, RESOURCE](
    componentGateway: ComponentGateway[DP_SPEC, COMPONENT_SPEC, RESOURCE]
  )(implicit encoder: Encoder[RESOURCE]): Provisioner[DP_SPEC, COMPONENT_SPEC] =
    new DefaultSyncProvisioner[DP_SPEC, COMPONENT_SPEC, RESOURCE](componentGateway)

  /** Create a default async provisioenr
    * @param provisioningStatusRepo: A [[Repository]] instance of [[ProvisioningStatus]] that is used to mangae the state during the async call
    * @param componentGateway: an instance of [[ComponentGateway]] that will be used to execute the provision logic for the specific component
    * @tparam DP_SPEC: DataProduct type parameter
    * @tparam COMPONENT_SPEC: Component type parameter
    * @return
    */
  def defaultAsync[DP_SPEC, COMPONENT_SPEC, RESOURCE](
    provisioningStatusRepo: Repository[ProvisioningStatus, String, Unit],
    componentGateway: ComponentGateway[DP_SPEC, COMPONENT_SPEC, RESOURCE]
  ): Provisioner[DP_SPEC, COMPONENT_SPEC] =
    new DefaultAsyncProvisioner[DP_SPEC, COMPONENT_SPEC, RESOURCE](provisioningStatusRepo, componentGateway)

}
