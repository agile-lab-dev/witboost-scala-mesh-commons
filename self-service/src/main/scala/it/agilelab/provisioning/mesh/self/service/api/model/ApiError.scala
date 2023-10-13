package it.agilelab.provisioning.mesh.self.service.api.model

import cats.Show
import cats.Show.fromToString

/** ApiError sealed trait
  *
  * An ApiError hierarchy based on Coordinatoor open api specification
  */
sealed trait ApiError extends Product with Serializable

object ApiError {

  /** SystemError
    *
    * @param error: Error description
    */
  final case class SystemError(error: String) extends ApiError

  /** ValidationError
    * @param errors: Errors description
    */
  final case class ValidationError(errors: Seq[String]) extends ApiError

  /** Create a SystemError with the provided error
    * @param error: Error message
    * @return SystemError
    */
  def sysErr(error: String): SystemError = SystemError(error)

  /** Create a ValidationError with the provided errors
    * @param errors: Errors description
    * @return ValidationError
    */
  def validErr(errors: String*): ValidationError = ValidationError(errors)

  implicit def showApiError: Show[ApiError] = Show.show {
    case s: SystemError     => fromToString[SystemError].show(s)
    case v: ValidationError => fromToString[ValidationError].show(v)
  }
}
