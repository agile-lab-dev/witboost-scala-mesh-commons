package it.agilelab.provisioning.mesh.self.service.api.controller

import io.circe.Decoder
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.identifier.IDGenerator
import it.agilelab.provisioning.commons.principalsmapping.impl.identity.ErrorPrincipalsMapper
import it.agilelab.provisioning.commons.principalsmapping.{ CdpIamPrincipals, PrincipalsMapper }
import it.agilelab.provisioning.commons.validator.Validator
import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.handler.provision.DefaultProvisionHandler
import it.agilelab.provisioning.mesh.self.service.api.handler.state.DefaultProvisionStateHandler
import it.agilelab.provisioning.mesh.self.service.api.handler.validation.DefaultValidationHandler
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.SystemError
import it.agilelab.provisioning.mesh.self.service.api.model.ApiRequest.{ ProvisioningRequest, UpdateAclRequest }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ ProvisioningStatus, ValidationResult }
import it.agilelab.provisioning.mesh.self.service.api.model._
import it.agilelab.provisioning.mesh.self.service.core.provisioner.Provisioner

/** ProvisionerController
  * Provide an high level interface for a specific provisioner.
  * It's based to the open api specification provided by the coordinator.
  */
trait ProvisionerController[DP_SPEC, COMPONENT_SPEC, PRINCIPAL <: CdpIamPrincipals] {

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

  /** Unprovisions a [[ProvisionRequest]]
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

  /** Updates the ACL of a component based on a [[UpdateAclRequest]]
    *
    * Decode the yaml descriptor and executes the update ACL of the received refs
    *
    * @param updateAclRequest : an instance of [[ProvisioningRequest]]
    * @return Right([[ProvisioningStatus]]) if the update ACL process complete without any side effect
    *         * A Right(ProvisioningStatus(RUNNING,None)) is returned in case of async update ACL
    *         * A Right(ProvisioningStatus(COMPLETED,Some("res")) is returned in case of sync update ACL
    *         * A Right(ProvisioningStatus(FAILED,Some("err")) is returned in case of sync update ACL failure
    *         Left([[ApiError]])
    *         * A Left(SystemError("errors")) is returned in case of side effect during the update ACL process
    *         * A Left(ValidationError(["my-errors"]) is returned in case of validation issue
    */
  def updateAcl(updateAclRequest: UpdateAclRequest)(implicit
    decoderPd: Decoder[ProvisioningDescriptor[DP_SPEC]],
    decoderCmp: Decoder[Component[COMPONENT_SPEC]]
  ): Either[ApiError, ProvisioningStatus]

}

object ProvisionerController {

  /** Create a [[DefaultProvisionerController]] instance without Acl mapping functionality
    * @param validator: A [[Validator]] instance
    * @param repository: A [[Repository]] instance
    * @param provisioner: A [[Provisioner]] instance
    * @tparam DP_SPEC: DataProduct type parameter
    * @tparam COMPONENT_SPEC: Component type parameter
    * @return
    */
  def defaultNoAcl[DP_SPEC, COMPONENT_SPEC](
    validator: Validator[ProvisionRequest[DP_SPEC, COMPONENT_SPEC]],
    provisioner: Provisioner[DP_SPEC, COMPONENT_SPEC, CdpIamPrincipals],
    repository: Repository[ProvisioningStatus, String, Unit]
  ): ProvisionerController[DP_SPEC, COMPONENT_SPEC, CdpIamPrincipals] =
    new DefaultProvisionerController[DP_SPEC, COMPONENT_SPEC, CdpIamPrincipals](
      new DefaultValidationHandler(validator),
      new DefaultProvisionHandler(IDGenerator.defaultWithTimestamp(), provisioner),
      new DefaultProvisionStateHandler(repository),
      new ErrorPrincipalsMapper
    )

  /** Create a [[DefaultProvisionerControllerWithAudit]] instance without Acl mapping functionality
    * @param validator: A [[Validator]] instance
    * @param repository: A [[Repository]] instance
    * @param provisioner: A [[Provisioner]] instance
    * @tparam DP_SPEC: DataProduct type parameter
    * @tparam COMPONENT_SPEC: Component type parameter
    * @return
    */
  def defaultNoAclWithAudit[DP_SPEC, COMPONENT_SPEC](
    validator: Validator[ProvisionRequest[DP_SPEC, COMPONENT_SPEC]],
    provisioner: Provisioner[DP_SPEC, COMPONENT_SPEC, CdpIamPrincipals],
    repository: Repository[ProvisioningStatus, String, Unit]
  ): ProvisionerController[DP_SPEC, COMPONENT_SPEC, CdpIamPrincipals] =
    new DefaultProvisionerControllerWithAudit[DP_SPEC, COMPONENT_SPEC, CdpIamPrincipals](
      defaultNoAcl[DP_SPEC, COMPONENT_SPEC](validator, provisioner, repository),
      Audit.default("ProvisionerControllerNoAcl")
    )

  /** Create a [[DefaultProvisionerController]] instance with Acl mapping functionality
    *
    * @param validator   : A [[Validator]] instance
    * @param repository  : A [[Repository]] instance
    * @param provisioner : A [[Provisioner]] instance
    * @tparam DP_SPEC        : DataProduct type parameter
    * @tparam COMPONENT_SPEC : Component type parameter
    * @return
    */
  def defaultAcl[DP_SPEC, COMPONENT_SPEC, PRINCIPAL <: CdpIamPrincipals](
    validator: Validator[ProvisionRequest[DP_SPEC, COMPONENT_SPEC]],
    provisioner: Provisioner[DP_SPEC, COMPONENT_SPEC, PRINCIPAL],
    repository: Repository[ProvisioningStatus, String, Unit],
    principalsMapper: PrincipalsMapper[PRINCIPAL]
  ): ProvisionerController[DP_SPEC, COMPONENT_SPEC, PRINCIPAL] =
    new DefaultProvisionerController[DP_SPEC, COMPONENT_SPEC, PRINCIPAL](
      new DefaultValidationHandler(validator),
      new DefaultProvisionHandler(IDGenerator.defaultWithTimestamp(), provisioner),
      new DefaultProvisionStateHandler(repository),
      principalsMapper
    )

  /** Create a [[DefaultProvisionerControllerWithAudit]] instance with Acl mapping functionality
    *
    * @param validator   : A [[Validator]] instance
    * @param repository  : A [[Repository]] instance
    * @param provisioner : A [[Provisioner]] instance
    * @tparam DP_SPEC        : DataProduct type parameter
    * @tparam COMPONENT_SPEC : Component type parameter
    * @return
    */
  def defaultAclWithAudit[DP_SPEC, COMPONENT_SPEC, PRINCIPAL <: CdpIamPrincipals](
    validator: Validator[ProvisionRequest[DP_SPEC, COMPONENT_SPEC]],
    provisioner: Provisioner[DP_SPEC, COMPONENT_SPEC, PRINCIPAL],
    repository: Repository[ProvisioningStatus, String, Unit],
    principalsMapper: PrincipalsMapper[PRINCIPAL]
  ): ProvisionerController[DP_SPEC, COMPONENT_SPEC, PRINCIPAL] =
    new DefaultProvisionerControllerWithAudit[DP_SPEC, COMPONENT_SPEC, PRINCIPAL](
      defaultAcl[DP_SPEC, COMPONENT_SPEC, PRINCIPAL](validator, provisioner, repository, principalsMapper),
      Audit.default("ProvisionerControllerAcl")
    )
}
