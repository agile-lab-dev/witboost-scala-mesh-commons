package it.agilelab.provisioning.mesh.self.service.api.controller

import cats.implicits.showInterpolator
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.SystemError
import it.agilelab.provisioning.mesh.self.service.api.model.ApiRequest.ProvisioningRequest
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ ProvisioningStatus, ValidationResult }
import it.agilelab.provisioning.mesh.self.service.api.model.{
  ApiError,
  ApiResponse,
  Component,
  ProvisionRequest,
  ProvisioningDescriptor
}
import io.circe.Decoder
import it.agilelab.provisioning.commons.audit.Audit

/** Default ProvisionerController with Audit enabled
  * @param provisionerController: A [[ProvisionerController]] that execute the logic
  * @param audit: An [[Audit]] instance used to log
  * @tparam DP_SPEC: DataProduct type parameter
  * @tparam COMPONENT_SPEC: Component type parameter
  */
class DefaultProvisionerControllerWithAudit[DP_SPEC, COMPONENT_SPEC](
  provisionerController: ProvisionerController[DP_SPEC, COMPONENT_SPEC],
  audit: Audit
) extends ProvisionerController[DP_SPEC, COMPONENT_SPEC] {

  /** Validate a [[ProvisioningRequest]]
    *
    * Decode the yaml descriptor and execute a validation of the request.
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
  ): Either[SystemError, ValidationResult] = {
    val result = provisionerController.validate(request)
    result match {
      case Right(_) => audit.info("Validate completed successfully")
      case Left(e)  => audit.error(show"Validate failed. Details: $e")
    }
    result
  }

  /** Provision a [[ProvisionRequest]]
    *
    * Decode the yaml descriptor and execute the provision of the request
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
  ): Either[ApiError, ProvisioningStatus] = {
    val result = provisionerController.provision(request)
    result match {
      case Right(_) => audit.info("Provision completed successfully")
      case Left(e)  => audit.error(show"Provision failed. Details: $e")
    }
    result
  }

  /** Retrieve a provision status
    *
    * @param id : a [[String]] that identify the provision
    * @return Right(Status)
    *         Left(ApiError)
    */
  override def getProvisionStatus(id: String): Either[ApiError, ApiResponse.ProvisioningStatus] = {
    val result = provisionerController.getProvisionStatus(id)
    result match {
      case Right(_) => audit.info(show"Get provision state with id: $id completed successfully")
      case Left(e)  => audit.error(show"Get provision state with id: $id failed. Details $e")
    }
    result
  }

  /** Unprovision a [[ProvisionRequest]]
    *
    * Decode the yaml descriptor and execute the unprovision of the request
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
  ): Either[ApiError, ProvisioningStatus] = {
    val result = provisionerController.unprovision(request)
    result match {
      case Right(_) => audit.info("Unprovision completed successfully")
      case Left(e)  => audit.error(show"Unprovision failed. Details: $e")
    }
    result
  }
}
