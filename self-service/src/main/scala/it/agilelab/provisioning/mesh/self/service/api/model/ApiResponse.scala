package it.agilelab.provisioning.mesh.self.service.api.model

import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ validErr, ValidationError }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.{ COMPLETED, FAILED, RUNNING }
import io.circe._

/** ApiResponse sealed trait
  * A hierarchical representation of ApiResponse
  */
sealed trait ApiResponse extends Product with Serializable

object ApiResponse {

  sealed trait Status extends ApiResponse

  object Status {

    case object RUNNING   extends Status
    case object COMPLETED extends Status
    case object FAILED    extends Status

    /** Implicits custom encoder for Status sum type
      */
    implicit def statusEncoder: Encoder[Status] = {
      case Status.RUNNING   => Json.fromString("RUNNING")
      case Status.COMPLETED => Json.fromString("COMPLETED")
      case Status.FAILED    => Json.fromString("FAILED")
    }

    /** Implicits custom decoder for Status sum type
      */
    implicit val statusDecoder: Decoder[Status] = Decoder[String].emap {
      case "RUNNING" | "running"     => Right(RUNNING)
      case "COMPLETED" | "completed" => Right(COMPLETED)
      case "FAILED" | "failed"       => Right(FAILED)
      case other                     => Left(s"Invalid status: $other")
    }
  }

  /** ProvisioningStatus
    * @param id: the unique id of the provision request
    * @param status: [[Status]] of the provision process.
    * @param result: Optional result of the provision process in string format
    */
  final case class ProvisioningStatus(id: String, status: Status, result: Option[String]) extends ApiResponse

  /** ValidationResult
    * @param valid: Boolean that represent the validity of the result
    * @param error: Optional [[ValidationError]]
    */
  final case class ValidationResult(valid: Boolean, error: Option[ValidationError]) extends ApiResponse

  /** Create a valid [[ValidationResult]].
    * @return [[ValidationResult]]
    */
  def valid(): ValidationResult =
    ValidationResult(valid = true, None)

  /** Create an invalid [[ValidationResult]]
    * @param errors: Validation errors on string format
    * @return [[ValidationResult]]
    */
  def invalid(errors: String*): ValidationResult =
    ValidationResult(valid = false, Some(validErr(errors: _*)))

  /** Crate a RUNNING [[ProvisioningStatus]]
    * @param id: identifier of the [[ProvisioningStatus]]
    * @return [[ProvisioningStatus]]
    */
  def running(id: String): ProvisioningStatus =
    ProvisioningStatus(id: String, RUNNING, None)

  /** Create a COMPLETED [[ProvisioningStatus]]
    * @param id: identifier of the [[ProvisioningStatus]]
    * @param result: provision result
    * @return [[ProvisioningStatus]]
    */
  def completed(id: String, result: Option[String]): ProvisioningStatus =
    ProvisioningStatus(id, COMPLETED, result)

  /** Create a FAILED [[ProvisioningStatus]]
    * @param id: identifier of the [[ProvisioningStatus]]
    * @param error: provisioning error
    * @return [[ProvisioningStatus]]
    */
  def failed(id: String, error: Option[String]): ProvisioningStatus =
    ProvisioningStatus(id, FAILED, error)

  /** [[ProvisioningStatus]] operations
    * @param instance: [[ProvisioningStatus]]
    */
  implicit class ProvisioningStatusOps(instance: ProvisioningStatus) {
    def completed(detail: String): ProvisioningStatus =
      instance.copy(status = COMPLETED, result = Some(detail))
    def failed(detail: String): ProvisioningStatus    =
      instance.copy(status = FAILED, result = Some(detail))
  }

}
