package it.agilelab.provisioning.mesh.self.service.api.controller

import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.handler.provision.DefaultProvisionHandler
import it.agilelab.provisioning.mesh.self.service.api.handler.state.DefaultProvisionStateHandler
import it.agilelab.provisioning.mesh.self.service.api.handler.validation.DefaultValidationHandler
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.SystemError
import it.agilelab.provisioning.mesh.self.service.api.model.ApiRequest.ProvisioningRequest
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ ProvisioningStatus, ValidationResult }
import it.agilelab.provisioning.mesh.self.service.api.model._
import it.agilelab.provisioning.mesh.self.service.core.provisioner.Provisioner
import io.circe.Decoder
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.identifier.IDGenerator
import it.agilelab.provisioning.commons.validator.Validator

/** ProvisionerController
  * Provide an high level interface for a specific provisioner.
  * It's based to the open api specification provided by the coordinator.
  */
trait ProvisionerController[DP_SPEC, COMPONENT_SPEC] {

  /** Validate a [[ProvisioningRequest]]
    *
    * Decode the yaml descriptor and execute a validation of the request.
    *
    * @param request: A [[ProvisioningRequest]] instance
    * @return A Right([[ValidationResult]]) if the validation process is completed without side effect.
    *           * A Right(ValidationResult(valid=true,error=None)) is returned in case of valid request
    *           * A Right(ValidationResult(valid=false,error=Some(ValidationError(Seq("my-err"))) is returned in case of invalid request
    *         Left([[SystemError]]) is returned in case of some side effect.
    */
  def validate(request: ProvisioningRequest)(implicit
    decoderPd: Decoder[ProvisioningDescriptor[DP_SPEC]],
    decoderCmp: Decoder[Component[COMPONENT_SPEC]]
  ): Either[SystemError, ValidationResult]

  /** Provision a [[ProvisionRequest]]
    *
    * Decode the yaml descriptor and execute the provision of the request
    *
    * @param request: an instance of [[ProvisioningRequest]]
    * @return Right([[ProvisioningStatus]]) if the provision process complete without any side effect
    *           * A Right(ProvisioningStatus(RUNNING,None)) is returned in case of async provision
    *           * A Right(ProvisioningStatus(COMPLETED,Some("res")) is returned in case of sync provision
    *           * A Right(ProvsiioningStatus(FAILED,Some("err")) is returned in case of sync provision failure
    *         Left([[ApiError]])
    *           * A Left(SystemError("errors")) is returned in case of side effect during the provision process
    *           * A Left(ValidationError(["my-errors"]) is returned in case of validation issue
    */
  def provision(request: ProvisioningRequest)(implicit
    decoderPd: Decoder[ProvisioningDescriptor[DP_SPEC]],
    decoderCmp: Decoder[Component[COMPONENT_SPEC]]
  ): Either[ApiError, ProvisioningStatus]

  /** Retrieve a provision status
    * @param id: a [[String]] that identify the provision
    * @return Right(Status)
    *         Left(ApiError)
    */
  def getProvisionStatus(id: String): Either[ApiError, ProvisioningStatus]

  /** Unrovision a [[ProvisionRequest]]
    *
    * Decode the yaml descriptor and execute the unprovision of the request
    *
    * @param request: an instance of [[ProvisioningRequest]]
    * @return Right([[ProvisioningStatus]]) if the unprovision process complete without any side effect
    *           * A Right(ProvisioningStatus(RUNNING,None)) is returned in case of async unprovision
    *           * A Right(ProvisioningStatus(COMPLETED,Some("res")) is returned in case of sync unprovision
    *           * A Right(ProvsiioningStatus(FAILED,Some("err")) is returned in case of sync unprovision failure
    *         Left([[ApiError]])
    *           * A Left(SystemError("errors")) is returned in case of side effect during the unprovision process
    *           * A Left(ValidationError(["my-errors"]) is returned in case of validation issue
    */
  def unprovision(request: ProvisioningRequest)(implicit
    decoderPd: Decoder[ProvisioningDescriptor[DP_SPEC]],
    decoderCmp: Decoder[Component[COMPONENT_SPEC]]
  ): Either[ApiError, ProvisioningStatus]

}

object ProvisionerController {

  /** Create a [[DefaultProvisionerController]] instance
    * @param validator: A [[Validator]] instance
    * @param repository: A [[Repository]] instance
    * @param provisioner: A [[Provisioner]] instance
    * @tparam DP_SPEC: DataProduct type parameter
    * @tparam COMPONENT_SPEC: Component type parameter
    * @return
    */
  def default[DP_SPEC, COMPONENT_SPEC](
    validator: Validator[ProvisionRequest[DP_SPEC, COMPONENT_SPEC]],
    provisioner: Provisioner[DP_SPEC, COMPONENT_SPEC],
    repository: Repository[ProvisioningStatus, String, Unit]
  ): ProvisionerController[DP_SPEC, COMPONENT_SPEC] =
    new DefaultProvisionerController[DP_SPEC, COMPONENT_SPEC](
      new DefaultValidationHandler(validator),
      new DefaultProvisionHandler(IDGenerator.defaultWithTimestamp(), provisioner),
      new DefaultProvisionStateHandler(repository)
    )

  /** Create a [[DefaultProvisionerControllerWithAudit]] instance
    * @param validator: A [[Validator]] instance
    * @param repository: A [[Repository]] instance
    * @param provisioner: A [[Provisioner]] instance
    * @tparam DP_SPEC: DataProduct type parameter
    * @tparam COMPONENT_SPEC: Component type parameter
    * @return
    */
  def defaultWithAudit[DP_SPEC, COMPONENT_SPEC](
    validator: Validator[ProvisionRequest[DP_SPEC, COMPONENT_SPEC]],
    provisioner: Provisioner[DP_SPEC, COMPONENT_SPEC],
    repository: Repository[ProvisioningStatus, String, Unit]
  ): ProvisionerController[DP_SPEC, COMPONENT_SPEC] =
    new DefaultProvisionerControllerWithAudit[DP_SPEC, COMPONENT_SPEC](
      default[DP_SPEC, COMPONENT_SPEC](validator, provisioner, repository),
      Audit.default("ProvisionerController")
    )
}
