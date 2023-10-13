package it.agilelab.provisioning.mesh.self.service.api.handler.validation

import cats.implicits.showInterpolator
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.mesh.self.service.api.model.{ ApiError, ApiResponse, ProvisionRequest }

/** Default implementation of a [[ValidationHandler]] with [[Audit]] enabled
  *
  * @param validationHandler: A [[ValidationHandler]] that execute the validation logic
  * @param audit: An [[Audit]] instance thas is used for audit
  * @tparam DP_SPEC: DataProduct type parameter
  * @tparam COMPONENT_SPEC: Component type parameter
  */
class DefaultValidationHandlerWithAudit[DP_SPEC, COMPONENT_SPEC](
  validationHandler: ValidationHandler[DP_SPEC, COMPONENT_SPEC],
  audit: Audit
) extends ValidationHandler[DP_SPEC, COMPONENT_SPEC] {

  /** Execute a validation of the incoming [[ProvisionRequest]]
    *
    * @param provisionRequest  : A [[ProvisionRequest]] instance
    * @return Right(ValidationResult) if the validate process complete without side effect
    *         * A Right(ValidationResult(valid=true,None)) if the request is valid
    *         * A Right(ValidationResult(valid=false,Some(ValidationError(Seq(error))))) if the request is invalid
    *         Left(SystemError) in case of side effect during the validation process
    */
  override def validate(
    provisionRequest: ProvisionRequest[DP_SPEC, COMPONENT_SPEC]
  ): Either[ApiError.SystemError, ApiResponse.ValidationResult] = {
    val result = validationHandler.validate(provisionRequest)
    result match {
      case Left(e)  => audit.error(show"validate failed. Details $e")
      case Right(r) => audit.info(show"validate completed successfully")
    }
    result
  }
}
