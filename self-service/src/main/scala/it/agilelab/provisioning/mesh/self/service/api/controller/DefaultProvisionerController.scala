package it.agilelab.provisioning.mesh.self.service.api.controller

import cats.data.NonEmptyList
import cats.implicits._
import io.circe.Decoder
import it.agilelab.provisioning.commons.principalsmapping.PrincipalsMapperError.{
  PrincipalMappingError,
  PrincipalMappingSystemError
}
import it.agilelab.provisioning.commons.principalsmapping.{ CdpIamPrincipals, PrincipalsMapper }
import it.agilelab.provisioning.commons.support.ParserSupport
import it.agilelab.provisioning.mesh.self.service.api.handler.provision.ProvisionHandler
import it.agilelab.provisioning.mesh.self.service.api.handler.state.ProvisionStateHandler
import it.agilelab.provisioning.mesh.self.service.api.handler.validation.ValidationHandler
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError._
import it.agilelab.provisioning.mesh.self.service.api.model.ApiRequest.ProvisioningRequest
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ ProvisioningStatus, ValidationResult }
import it.agilelab.provisioning.mesh.self.service.api.model._

/** Default ProvisionerController implementation
  * @param validationHandler: An instance of [[ValidationHandler]] that will be used to handle validation actions
  * @param provisionHandler: An instance of [[ProvisionHandler]] that will be used to handle provision actions
  * @param provisionStateHandler: An instance of [[ProvisionStateHandler]] that will be used to handle provision state
  * @tparam DP_SPEC: DataProduct specific type parameter
  * @tparam COMPONENT_SPEC: Component specific type parameter
  */
class DefaultProvisionerController[DP_SPEC, COMPONENT_SPEC, PRINCIPAL <: CdpIamPrincipals](
  validationHandler: ValidationHandler[DP_SPEC, COMPONENT_SPEC],
  provisionHandler: ProvisionHandler[DP_SPEC, COMPONENT_SPEC, PRINCIPAL],
  provisionStateHandler: ProvisionStateHandler,
  principalsMapper: PrincipalsMapper[PRINCIPAL]
) extends ProvisionerController[DP_SPEC, COMPONENT_SPEC, PRINCIPAL]
    with ParserSupport {

  /** Validate a [[ProvisioningRequest]]
    *
    * Decode the yaml descriptor and execute a validation of the request via a [[ValidationHandler]]
    *
    * A Bad yaml input it is mapped to an invalid results.
    *
    * @param request  : A [[ProvisioningRequest]] instance
    * @return A Right([[ValidationResult]]) if the validation process is completed without side effect.
    *         * A Right(ValidationResult(valid=true,error=None)) is returned in case of valid request
    *         * A Right(ValidationResult(valid=false,error=Some(ValidationError(Seq("my-err"))) is returned in case of invalid request
    *         Left([[SystemError]]) is returned in case of some side effect.
    */
  override def validate(request: ProvisioningRequest)(implicit
    decoderPd: Decoder[ProvisioningDescriptor[DP_SPEC]],
    decoderCmp: Decoder[Component[COMPONENT_SPEC]]
  ): Either[SystemError, ValidationResult] =
    fromYml[ProvisioningDescriptor[DP_SPEC]](request.descriptor)
      .flatMap(_.toProvisionRequest[COMPONENT_SPEC])
      .map(validationHandler.validate)
      .leftMap(e => ApiResponse.invalid(e.show))
      .sequence
      .map(_.merge)

  /** Provision a [[ProvisionRequest]]
    *
    * Decode the yaml descriptor, execute a validation of the request via a [[ValidationHandler]]
    * and, in case of valid request execute the provision via a [[ProvisionHandler]].
    *
    * It will return a [[ValidationError]] in case of invalid request and a [[SystemError]]
    * in case of side effect during the provision
    *
    * @param request : an instance of [[ProvisioningRequest]]
    * @return Right([[ProvisioningStatus]]) if the provision process complete without any side effect
    *         * A Right(ProvisioningStatus(RUNNING,None)) is returned in case of async provision
    *         * A Right(ProvisioningStatus(COMPLETED,Some("res")) is returned in case of sync provision
    *         * A Right(ProvsiioningStatus(FAILED,Some("err")) is returned in case of sync provision failure
    *         Left([[ApiError]])
    *         * A Left(SystemError("errors")) is returned in case of side effect during the provision process
    *         * A Left(ValidationError(["my-errors"]) is returned in case of validation issue
    */
  override def provision(request: ProvisioningRequest)(implicit
    decoderPd: Decoder[ProvisioningDescriptor[DP_SPEC]],
    decoderCmp: Decoder[Component[COMPONENT_SPEC]]
  ): Either[ApiError, ProvisioningStatus] =
    for {
      provisioningDesc   <- fromYml[ProvisioningDescriptor[DP_SPEC]](request.descriptor)
                              .leftMap(e => validErr(e.show))
      provisionRequest   <- provisioningDesc.toProvisionRequest[COMPONENT_SPEC].leftMap(e => validErr(e.show))
      validationResult   <- validationHandler.validate(provisionRequest)
      provisioningStatus <- if (validationResult.valid) provisionHandler.provision(provisionRequest)
                            else Left(validationResult.error.getOrElse(validErr()))
    } yield provisioningStatus

  /** Retrieve a provision status
    *
    * @param id : a [[String]] that identify the provision
    * @return Right(ProvisioningStatus)
    *         Left(ApiError)
    */
  override def getProvisionStatus(id: String): Either[ApiError, ProvisioningStatus] =
    provisionStateHandler.get(id)

  /** Unprovision a [[ProvisionRequest]]
    *
    * Decode the yaml descriptor, execute a validation of the request via a [[ValidationHandler]]
    * and, in case of valid request execute the unprovision via a [[ProvisionHandler]].
    *
    * It will return a [[ValidationError]] in case of invalid request and a [[SystemError]]
    * in case of side effect during the unprovision
    *
    * @param request : an instance of [[ProvisioningRequest]]
    * @return Right([[ProvisioningStatus]]) if the unprovision process complete without any side effect
    *         * A Right(ProvisioningStatus(RUNNING,None)) is returned in case of async unprovision
    *         * A Right(ProvisioningStatus(COMPLETED,Some("res")) is returned in case of sync unprovision
    *         * A Right(ProvsiioningStatus(FAILED,Some("err")) is returned in case of sync unprovision failure
    *         Left([[ApiError]])
    *         * A Left(SystemError("errors")) is returned in case of side effect during the unprovision process
    *         * A Left(ValidationError(["my-errors"]) is returned in case of validation issue
    */
  override def unprovision(request: ProvisioningRequest)(implicit
    decoderPd: Decoder[ProvisioningDescriptor[DP_SPEC]],
    decoderCmp: Decoder[Component[COMPONENT_SPEC]]
  ): Either[ApiError, ProvisioningStatus] =
    for {
      provisioningDesc   <- fromYml[ProvisioningDescriptor[DP_SPEC]](request.descriptor)
                              .leftMap(e => validErr(e.show))
      provisionRequest   <- provisioningDesc.toProvisionRequest[COMPONENT_SPEC].leftMap(e => validErr(e.show))
      validationResult   <- validationHandler.validate(provisionRequest)
      provisioningStatus <- if (validationResult.valid) provisionHandler.unprovision(provisionRequest)
                            else Left(validationResult.error.getOrElse(validErr()))
    } yield provisioningStatus

  /** Updates the ACL of a component based on a [[it.agilelab.provisioning.mesh.self.service.api.model.ApiRequest.UpdateAclRequest]]
    *
    * Decode the yaml descriptor and executes the update ACL of the received refs
    *
    * @param updateAclRequest : an instance of [[ProvisioningRequest]]
    * @return Right([[ProvisioningStatus]]) if the update ACL process complete without any side effect
    *         * A Right(ProvisioningStatus(RUNNING,None)) is returned in case of async update ACL
    *         * A Right(ProvisioningStatus(COMPLETED,Some("res")) is returned in case of sync update ACL
    *         * A Right(ProvsiioningStatus(FAILED,Some("err")) is returned in case of sync update ACL failure
    *         Left([[ApiError]])
    *         * A Left(SystemError("errors")) is returned in case of side effect during the update ACL process
    *         * A Left(ValidationError(["my-errors"]) is returned in case of validation issue
    */
  override def updateAcl(updateAclRequest: ApiRequest.UpdateAclRequest)(implicit
    decoderPd: Decoder[ProvisioningDescriptor[DP_SPEC]],
    decoderCmp: Decoder[Component[COMPONENT_SPEC]]
  ): Either[ApiError, ProvisioningStatus] = for {
    provisioningDesc   <- fromYml[ProvisioningDescriptor[DP_SPEC]](updateAclRequest.provisionInfo.request)
                            .leftMap(e => validErr(e.show))
    provisionRequest   <- provisioningDesc.toProvisionRequest[COMPONENT_SPEC].leftMap(e => validErr(e.show))
    validationResult   <- validationHandler.validate(provisionRequest)
    provisioningStatus <- if (validationResult.valid)
                            for {
                              refsMapping        <- Right {
                                                      principalsMapper
                                                        .map(updateAclRequest.refs.toSet)
                                                        .values
                                                        .partitionMap(identity)
                                                    }
                              provisioningStatus <- provisionHandler.updateAcl(provisionRequest, refsMapping._2.toSet)
                              _                  <- NonEmptyList
                                                      .fromList(refsMapping._1.toList)
                                                      .map(ApiError.validErrNel(_) {
                                                        case PrincipalMappingError(error, _)       => error.problems
                                                        case PrincipalMappingSystemError(error, _) => error.problems
                                                      })
                                                      .toLeft(())
                            } yield provisioningStatus
                          else Left(validationResult.error.getOrElse(validErr()))
  } yield provisioningStatus

}
