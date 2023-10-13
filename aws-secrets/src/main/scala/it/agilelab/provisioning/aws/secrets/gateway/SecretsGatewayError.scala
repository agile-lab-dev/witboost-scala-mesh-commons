package it.agilelab.provisioning.aws.secrets.gateway

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.showable.ShowableOps._

/** SecretsGatewayError
  * A sealed trait for SecretsGatewayError(s)
  */
sealed trait SecretsGatewayError extends Exception with Product with Serializable

object SecretsGatewayError {

  /** SecretsGatewayInitErr
    * Define an error during the initialization of the SecretsGateway
    * @param error: Throwable instance that generate this error
    */
  final case class SecretsGatewayInitErr(error: Throwable) extends SecretsGatewayError

  /** GetSecretErr
    * Define an error during the get secret process
    * @param key: Secret key used on the
    * @param error: Throwable instance that generate this error
    */
  final case class GetSecretErr(key: String, error: Throwable) extends SecretsGatewayError

  /** CreateSecretErr
    * Define an error during the create secret process
    * @param secretName: The name of the secret
    * @param error: Throwable instance that generate this error
    */
  final case class CreateSecretErr(secretName: String, error: Throwable) extends SecretsGatewayError

  /** UpdateSecretErr
    * Define an error during the update secret process
    * @param secretArn: The arn of the secret
    * @param error: Throwable instance that generate this error
    */
  final case class UpdateSecretErr(secretArn: String, error: Throwable) extends SecretsGatewayError

  /** DestroySecretErr
    * Define an error during the destroy secret process
    * @param key: The key of the secret
    * @param error: Throwable instance that generate this error
    */
  final case class DestroySecretErr(key: String, error: Throwable) extends SecretsGatewayError

  /** IsSecretScheduledForDeletionErr
    * Define an error during the is secret scheduled for deletion process
    * @param key: The key of the secret
    * @param error: Throwable instance that generate this error
    */
  final case class IsSecretScheduledForDeletionErr(key: String, error: Throwable) extends SecretsGatewayError

  /** RestoreSecretErr
    * Define an error during the restore secret process
    *
    * @param key: The key of the secret
    * @param error: Throwable instance that generate this error
    */
  final case class RestoreSecretErr(key: String, error: Throwable) extends SecretsGatewayError

  /** Implicit cats.Show implementation for SecretsGatewayError
    */
  implicit val showSecretsGatewayError: Show[SecretsGatewayError] = Show.show {
    case e: SecretsGatewayInitErr           => show"SecretsGatewayInitErr(${e.error})"
    case e: GetSecretErr                    => show"GetSecretErr(${e.key},${e.error})"
    case e: CreateSecretErr                 => show"CreateSecretErr(${e.secretName},${e.error})"
    case e: UpdateSecretErr                 => show"UpdateSecretErr(${e.secretArn},${e.error})"
    case e: DestroySecretErr                => show"DestroySecretErr(${e.key},${e.error})"
    case e: IsSecretScheduledForDeletionErr => show"IsSecretScheduledForDeletionErr(${e.key},${e.error})"
    case e: RestoreSecretErr                => show"RestoreSecretErr(${e.key},${e.error})"
  }
}
