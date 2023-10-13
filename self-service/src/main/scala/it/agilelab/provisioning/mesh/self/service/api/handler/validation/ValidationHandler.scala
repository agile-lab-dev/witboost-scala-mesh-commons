package it.agilelab.provisioning.mesh.self.service.api.handler.validation

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.validator.Validator
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.SystemError
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ValidationResult
import it.agilelab.provisioning.mesh.self.service.api.model.ProvisionRequest

/** ValidationHandler
  *
  * Handle the validation process
  *
  * @tparam DP_SPEC: DataProduct type parameter
  * @tparam COMPONENT_SPEC: DataProduct type parameter
  */
trait ValidationHandler[DP_SPEC, COMPONENT_SPEC] {

  /** Execute a validation of the incoming [[ProvisionRequest]]
    *
    * @param provisionRequest: A [[ProvisionRequest]] instance
    * @return Right(ValidationResult) if the validate process complete without side effect
    *         * A Right(ValidationResult(valid=true,None)) if the request is valid
    *         * A Right(ValidationResult(valid=false,Some(ValidationError(Seq(error))))) if the request is invalid
    *         Left(SystemError) in case of side effect during the validation process
    */
  def validate(
    provisionRequest: ProvisionRequest[DP_SPEC, COMPONENT_SPEC]
  ): Either[SystemError, ValidationResult]
}

object ValidationHandler {

  /** Create a [[DefaultValidationHandler]] based on a [[Validator]]
    * @param validator: [[Validator]] instance
    * @tparam DP_SPEC: DataProduct type parameters
    * @tparam COMPONENT_SPEC: Component type parameters
    * @return [[ValidationHandler]]
    */
  def default[DP_SPEC, COMPONENT_SPEC](
    validator: Validator[ProvisionRequest[DP_SPEC, COMPONENT_SPEC]]
  ): ValidationHandler[DP_SPEC, COMPONENT_SPEC] =
    new DefaultValidationHandler[DP_SPEC, COMPONENT_SPEC](validator)

  def defaultWithAudit[DP_SPEC, COMPONENT_SPEC](
    validator: Validator[ProvisionRequest[DP_SPEC, COMPONENT_SPEC]]
  ): ValidationHandler[DP_SPEC, COMPONENT_SPEC] =
    new DefaultValidationHandlerWithAudit[DP_SPEC, COMPONENT_SPEC](
      default[DP_SPEC, COMPONENT_SPEC](validator),
      Audit.default("ValidationHandler")
    )
}
