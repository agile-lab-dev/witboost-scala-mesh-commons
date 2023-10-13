package it.agilelab.provisioning.mesh.self.service.api.handler.validation

import cats.implicits._
import it.agilelab.provisioning.commons.validator.Validator
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError._
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ValidationResult
import it.agilelab.provisioning.mesh.self.service.api.model.{ ApiResponse, ProvisionRequest }

/** Default implementation of a [[ValidationHandler]]
  *
  * It is based on a [[Validator]] instance that will be used to validate the
  * [[ProvisionRequest]] based on a set of pre configured rules.
  *
  * @param validator: validator instance
  * @tparam DP_SPEC: DataProduct type parameter
  * @tparam COMPONENT_SPEC: Component type parameter
  */
class DefaultValidationHandler[DP_SPEC, COMPONENT_SPEC](
  validator: Validator[ProvisionRequest[DP_SPEC, COMPONENT_SPEC]]
) extends ValidationHandler[DP_SPEC, COMPONENT_SPEC] {

  /** Execute a validation of the incoming [[ProvisionRequest]] using the validator provided.
    *
    * Run the validator and map a successful result to a valid [[ValidationResult]] otherwise map
    * all the relative validation failure to an invalid [[ValidationResult]]
    *
    * @param provisionRequest: A [[ProvisionRequest]] instance
    * @return Right(ValidationResult) if the validate process complete without side effect
    *         * A Right(ValidationResult(valid=true,None)) if the request is valid
    *         * A Right(ValidationResult(valid=false,Some(ValidationError(Seq(error))))) if the request is invalid
    *         Left(SystemError) in case of side effect during the validation process
    */
  override def validate(
    provisionRequest: ProvisionRequest[DP_SPEC, COMPONENT_SPEC]
  ): Either[SystemError, ValidationResult] =
    validator
      .validate(provisionRequest)
      .map(result =>
        result.toEither
          .map(_ => ApiResponse.valid())
          .leftMap(e => ApiResponse.invalid(e.toList.map(_.message): _*))
          .merge
      )
      .leftMap(e =>
        sysErr(
          s"Validation fail: An exception was raised during request validation process. Exception: ${str(e.throwable)}"
        )
      )

  private def str(throwable: Throwable): String =
    s"${throwable.getClass.getCanonicalName}: ${throwable.getMessage}"

}
