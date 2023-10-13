package it.agilelab.provisioning.mesh.self.service.lambda.api

import cats.implicits._
import it.agilelab.provisioning.aws.handlers.rest.model.Response._
import it.agilelab.provisioning.aws.handlers.rest.model._
import it.agilelab.provisioning.aws.lambda.gateway.LambdaGateway
import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.controller.ProvisionerController
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ validErr, SystemError, ValidationError }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiRequest.ProvisioningRequest
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.RUNNING
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ invalid, ProvisioningStatus }
import it.agilelab.provisioning.mesh.self.service.api.model.{ Component, ProvisionRequest, ProvisioningDescriptor }
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import it.agilelab.provisioning.mesh.self.service.core.provisioner.Provisioner
import it.agilelab.provisioning.mesh.self.service.lambda.core.gateway.AsyncCallLambdaComponentGateway
import io.circe.generic.auto._
import io.circe.{ Decoder, Encoder }
import it.agilelab.provisioning.commons.support.ParserSupport
import it.agilelab.provisioning.commons.validator.Validator

/** A Lambda Api implementation for the provisioner
  * @param provisioner: an instance of [[ProvisionerController]] that will be used to execute the logic
  * @param apiConfig:   ApiConfig
  * @tparam DP_SPEC: DataProduct type parameter
  * @tparam COMPONENT_SPEC: Component type parameter
  */
class LambdaProvisionerApi[DP_SPEC, COMPONENT_SPEC](
  provisioner: ProvisionerController[DP_SPEC, COMPONENT_SPEC],
  apiConfig: ApiConfig
) extends ParserSupport {

  /** Handle the incoming [[Request]]
    *
    * it handle 4 kinds of possible interactions, mainly based to the coordinator open api.
    *
    * * POST method with /validate as path:
    *   It will parse the json [[ProvisioningRequest]] and then execute the [[ProvisionerController]] validate method.
    *   Map the result of validation to an OK Response and a SystemError to an internalServerError
    *
    * * POST method with /provision as path:
    *   It will parse the json [[ProvisioningRequest]] and then execute the [[ProvisionerController]] provision method.
    *   Map the result of provision as follow:
    *     Running provisioning status to accepted response
    *     Failed provisioning status to ok with provisioning status as body
    *     Completed provisioning status to ok with provisioning status as body
    *     ValidationError as badRequest
    *     SystemError as internalServerError
    *
    *  * GET method with provision/${id}/status
    *   It will return the status of a specific provisioning status using the id provided.
    *   Map the result of get provision status as follow
    *     status to ok
    *     ValidationError to badRequest
    *     SystemError to internalServerError
    *
    *  * POST method with /unprovision as path:
    *   It will parse the json [[ProvisioningRequest]] and then execute the [[ProvisionerController]] unprovision method.
    *   Map the result of unprovision as follow:
    *     Running provisioning status to accepted response
    *     Failed provisioning status to ok with provisioning status as body
    *     Completed provisioning status to ok with provisioning status as body
    *     ValidationError as badRequest
    *     SystemError as internalServerError
    *
    * @param request: A [[Request]] instance
    * @param ev: implicit [[Decoder]] for [[ProvisionRequest]]
    * @return a [[Response]] instance
    */
  def handle(request: Request)(implicit
    decoderPd: Decoder[ProvisioningDescriptor[DP_SPEC]],
    decoderCmp: Decoder[Component[COMPONENT_SPEC]]
  ): Response =
    request match {
      case Request("POST", _, path, _, _, body) if path.matches(apiConfig.validatePath)             =>
        fromJson[ProvisioningRequest](body)
          .leftMap(e => ok(invalid(e.show)))
          .flatMap(req =>
            provisioner
              .validate(req)
              .map(r => ok(r))
              .leftMap(e => internalServerError(e))
          )
          .merge
      case Request("POST", _, path, _, _, b) if path.matches(apiConfig.provisionPath)               =>
        fromJson[ProvisioningRequest](b)
          .leftMap(e => badRequest(validErr(e.show)))
          .flatMap(req =>
            provisioner
              .provision(req)
              .map {
                case ProvisioningStatus(id, RUNNING, _) => accepted(id)
                case p: ProvisioningStatus              => ok(p)
              }
              .leftMap {
                case e: ValidationError => badRequest(e)
                case e: SystemError     => internalServerError(e)
              }
          )
          .merge
      case Request("GET", _, path, pathParameters, _, _) if path.matches(apiConfig.provisionStatus) =>
        pathParameters
          .get("id")
          .map { id =>
            provisioner
              .getProvisionStatus(id)
              .map(r => ok(r))
              .leftMap {
                case e: ValidationError => badRequest(e)
                case e: SystemError     => internalServerError(e)
              }
              .merge
          }
          .fold(badRequest(validErr("id not found")))(identity)
      case Request("POST", _, path, _, _, b) if path.matches(apiConfig.unprovisionPath)             =>
        fromJson[ProvisioningRequest](b)
          .leftMap(e => badRequest(validErr(e.show)))
          .flatMap(req =>
            provisioner
              .unprovision(req)
              .map {
                case ProvisioningStatus(id, RUNNING, _) => accepted(id)
                case p: ProvisioningStatus              => ok(p)
              }
              .leftMap {
                case e: ValidationError => badRequest(e)
                case e: SystemError     => internalServerError(e)
              }
          )
          .merge
      case Request(_, _, path, _, _, _)                                                             =>
        badRequest(validErr(s"Method not found for $path"))
    }

}

object LambdaProvisionerApi {
  def default[A, B](
    validator: Validator[ProvisionRequest[A, B]],
    repository: Repository[ProvisioningStatus, String, Unit],
    lambdaGateway: LambdaGateway,
    provisionLambdaFunction: String,
    unprovisionLambdaFunction: String,
    apiConfig: ApiConfig
  )(implicit encoder: Encoder[ProvisionCommand[A, B]]): LambdaProvisionerApi[A, B] =
    new LambdaProvisionerApi[A, B](
      ProvisionerController.default[A, B](
        validator,
        Provisioner.defaultAsync(
          repository,
          new AsyncCallLambdaComponentGateway[A, B](
            lambdaGateway = lambdaGateway,
            provisionLambdaFunction = provisionLambdaFunction,
            unprovisionLambdaFunction = unprovisionLambdaFunction
          )
        ),
        repository
      ),
      apiConfig
    )
}
