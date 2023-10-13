package it.agilelab.provisioning.mesh.self.service.lambda.errorhandler

import cats.implicits.{ toBifunctorOps, toShow }
import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import it.agilelab.provisioning.mesh.self.service.lambda.errorhandler.model.LambdaError
import io.circe.Decoder
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.support.ParserSupport

class LambdaErrorHandler[DP_SPEC, COMPONENT_SPEC](provisioningStatusRepo: Repository[ProvisioningStatus, String, Unit])
    extends ParserSupport {

  def handle(
    event: String
  )(implicit decoder: Decoder[ProvisionCommand[DP_SPEC, COMPONENT_SPEC]]): Either[String, String] =
    for {
      err       <- fromJson[LambdaError[DP_SPEC, COMPONENT_SPEC]](event).leftMap(e => e.show)
      optStatus <- provisioningStatusRepo
                     .findById(err.requestPayload.requestId)
                     .leftMap(_ => "Unable to fetch Provisioning Status information with provided repository.")
      status    <- optStatus.toRight(s"Provisioning Status with id: ${err.requestPayload.requestId} Not found")
      _         <- provisioningStatusRepo
                     .update(status.failed(err.responsePayload.errorMessage))
                     .leftMap(_ => s"Unable to update provisioning status to failed.")
    } yield event

}
