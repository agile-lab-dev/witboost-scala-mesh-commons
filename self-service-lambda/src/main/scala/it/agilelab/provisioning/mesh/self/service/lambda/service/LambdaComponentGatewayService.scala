package it.agilelab.provisioning.mesh.self.service.lambda.service

import cats.implicits.{ toBifunctorOps, toShow }
import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.core.gateway.ComponentGateway
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.{ ComponentOperation, ComponentServiceCommand }
import io.circe.{ Decoder, Encoder }
import it.agilelab.provisioning.commons.support.ParserSupport

/** A LambdaComponentGatewayService
  * Execute within a lambda handle method the logic provided by the component gateway and manage the highlevel status
  * @param provisioningStatusRepo: [[Repository]] of [[ProvisioningStatus]]
  * @param componentGateway: [[ComponentGateway]] that generate the resources
  * @tparam DP_SPEC: DataProduct type parameter
  * @tparam COMPONENT_SPEC: Component type parameter
  */
class LambdaComponentGatewayService[DP_SPEC, COMPONENT_SPEC, RESOURCE](
  provisioningStatusRepo: Repository[ProvisioningStatus, String, Unit],
  componentGateway: ComponentGateway[DP_SPEC, COMPONENT_SPEC, RESOURCE]
) extends ParserSupport {

  private val FETCH_STATUS_ERROR        = "Unable to fetch Provisioning Status information with provided repository."
  private val STATUS_NOT_FOUND_ERROR    = "Provisioning Status with id: %s Not found"
  private val CREATE_COMPONENT_ERROR    = "Unable to create requested component. Details %s"
  private val UPDATE_TO_FAILED_ERROR    = "Unable to update provisioning status to failed."
  private val UPDATE_TO_COMPLETED_ERROR = "Unable to update provisioning status to completed."

  /** Parse the incoming [[ComponentServiceCommand]] event
    * @param event: incoming [[[ComponentServiceCommand]] in JSON format
    * @param decoder: implicit [[Decoder]] of [[ComponentServiceCommand]]
    * @return Right(String) with result
    *         Left(String) with error
    */
  def handle(
    event: String
  )(implicit
    decoder: Decoder[ComponentServiceCommand[DP_SPEC, COMPONENT_SPEC]],
    encoder: Encoder[RESOURCE]
  ): Either[String, String] =
    for {
      cmpServiceCommand <- fromJson[ComponentServiceCommand[DP_SPEC, COMPONENT_SPEC]](event).leftMap(e => e.show)
      optStatus         <- provisioningStatusRepo.findById(cmpServiceCommand.command.requestId).leftMap(_ => FETCH_STATUS_ERROR)
      status            <- optStatus.toRight(STATUS_NOT_FOUND_ERROR.format(cmpServiceCommand.command.requestId))
      result            <- handleOperation(cmpServiceCommand, status)
      jsonResult         = toJson[RESOURCE](result)
      _                 <- provisioningStatusRepo.update(status.completed(jsonResult)).leftMap(_ => UPDATE_TO_COMPLETED_ERROR)
    } yield jsonResult

  private def handleOperation(
    cmpServiceCommand: ComponentServiceCommand[DP_SPEC, COMPONENT_SPEC],
    status: ProvisioningStatus
  ) = cmpServiceCommand.operation match {
    case ComponentOperation.Create  =>
      componentGateway
        .create(cmpServiceCommand.command)
        .leftMap(e => recoveryStatus(status, e.error))
    case ComponentOperation.Destroy =>
      componentGateway
        .destroy(cmpServiceCommand.command)
        .leftMap(e => recoveryStatus(status, e.error))
  }

  private def recoveryStatus(status: ProvisioningStatus, error: String) =
    provisioningStatusRepo
      .update(status.failed(CREATE_COMPONENT_ERROR.format(error)))
      .map(_ => CREATE_COMPONENT_ERROR.format(error))
      .leftMap(_ => UPDATE_TO_FAILED_ERROR)
      .merge
}

object LambdaComponentGatewayService {

  /** Create a LambdaComponentGatewayService
    * @param repository: a [[Repository]] for [[ProvisioningStatus]]
    * @param componentGateway: a [[ComponentGateway]] for [[RESOURCE]]
    * @tparam DP_SPEC: DataProduct type parameter
    * @tparam COMPONENT_SPEC: Component type parameter
    * @tparam RESOURCE: Resource type paramenter
    * @return
    */
  def default[DP_SPEC, COMPONENT_SPEC, RESOURCE](
    repository: Repository[ProvisioningStatus, String, Unit],
    componentGateway: ComponentGateway[DP_SPEC, COMPONENT_SPEC, RESOURCE]
  ): LambdaComponentGatewayService[DP_SPEC, COMPONENT_SPEC, RESOURCE] =
    new LambdaComponentGatewayService[DP_SPEC, COMPONENT_SPEC, RESOURCE](repository, componentGateway)

}
