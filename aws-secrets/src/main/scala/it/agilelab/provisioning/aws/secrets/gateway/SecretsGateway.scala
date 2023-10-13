package it.agilelab.provisioning.aws.secrets.gateway

import it.agilelab.provisioning.aws.secrets.gateway.SecretsGatewayError.SecretsGatewayInitErr
import it.agilelab.provisioning.aws.secrets.gateway.model.AwsSecret
import it.agilelab.provisioning.commons.audit.Audit
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient

/** SecretsGateway provide a method to interact with aws secrets gateway
  */
trait SecretsGateway {

  /** Get an aws secret
    * @param key: Secrets id
    * @return Right(Option[AwsSecret]) if get succeeded
    *         Left(SecretsGatewayError) otherwise
    */
  def get(key: String): Either[SecretsGatewayError, Option[AwsSecret]]

  /** Create a secret string
    * @param secretName the name of the secret
    * @param secretValue the value of the secret
    * @return Right() if create succeed
    *         Left(SecretsGatewayError) otherwise
    */
  def create(secretName: String, secretValue: String): Either[SecretsGatewayError, Unit]

  /** Update a secret with a new value
    * @param secretArn the arn of the already existing secret
    * @param secretValue the new value of the secret
    * @return Right() if update succeed
    *         Left(SecretsGatewayError) otherwise
    */
  def update(secretArn: String, secretValue: String): Either[SecretsGatewayError, Unit]

  /** Destroy an aws secret
    * @param key  : Secrets id
    * @return Right() if destroy succeeded
    *         Left(SecretsGatewayError) otherwise
    */
  def destroy(key: String): Either[SecretsGatewayError, Unit]

  /** Check whether or not a secret has been scheduled for deletion
    * @param key: secret id
    * @return Right(Boolean) if the operation succeeded
    *          Left(SecretsGatewayError) otherwise
    */
  def isSecretScheduledForDeletion(key: String): Either[SecretsGatewayError, Boolean]

  /** Restore a secret that has been previously scheduled for deletion
    * @param key: secret id
    * @return Right() if the operation succeeded
    *         Left(SecretsGatewayError) otherwise
    */
  def restoreSecret(key: String): Either[SecretsGatewayError, Unit]
}

object SecretsGateway {

  def default(): Either[SecretsGatewayInitErr, SecretsGateway]          =
    try Right(new DefaultSecretsGateway(SecretsManagerClient.builder().build()))
    catch { case t: Throwable => Left(SecretsGatewayInitErr(t)) }

  def defaultWithAudit(): Either[SecretsGatewayInitErr, SecretsGateway] =
    default().map(new DefaultSecretsGatewayWithAudit(_, Audit.default("SecretsGateway")))

}
