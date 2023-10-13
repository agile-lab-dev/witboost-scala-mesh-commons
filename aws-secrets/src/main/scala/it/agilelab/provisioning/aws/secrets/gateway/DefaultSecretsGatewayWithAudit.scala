package it.agilelab.provisioning.aws.secrets.gateway

import cats.implicits._
import it.agilelab.provisioning.aws.secrets.gateway.model.AwsSecret
import it.agilelab.provisioning.commons.audit.Audit

class DefaultSecretsGatewayWithAudit(secretsGateway: SecretsGateway, audit: Audit) extends SecretsGateway {

  /** Get am aws secret
    *
    * Call Audit.info with an informative message if get key request process
    * are successful completed otherwise call Audit.error with and error message
    * @param key: Secrets id
    * @return Right(Option[String]) if get succeeded
    *         Left(SecretsGatewayError) otherwise
    */
  override def get(key: String): Either[SecretsGatewayError, Option[AwsSecret]] = {
    val result = secretsGateway.get(key)
    result match {
      case Right(_) => audit.info(show"GetSecret(key=$key) completed successfully")
      case Left(l)  => audit.error(show"GetSecret(key=$key) failed. Details: $l")
    }
    result
  }

  /** Create a secret string
    *
    * Call Audit.info with an informative message if create secret process
    * is successful completed otherwise call Audit.error with and error message
    * @param secretName  the name of the secret
    * @param secretValue the value of the secret
    * @return Right() if create succeed
    *         Left(SecretsGatewayError) otherwise
    */
  override def create(secretName: String, secretValue: String): Either[SecretsGatewayError, Unit] = {
    val result = secretsGateway.create(secretName, secretValue)
    result match {
      case Right(_) => audit.info(show"CreateSecret(secretName=$secretName,secretValue=*****) completed successfully")
      case Left(l)  => audit.error(show"CreateSecret(secretName=$secretName,secretValue=*****) failed. Details: $l")
    }
    result
  }

  /** Update a secret with a new value
    *
    * Call Audit.info with an informative message if update request process
    * are successful completed otherwise call Audit.error with and error message
    * @param secretArn  the arn of the already existing secret
    * @param secretValue the new value of the secret
    * @return Right() if update succeed
    *         Left(SecretsGatewayError) otherwise
    */
  override def update(secretArn: String, secretValue: String): Either[SecretsGatewayError, Unit] = {
    val result = secretsGateway.update(secretArn, secretValue)
    result match {
      case Right(_) => audit.info(show"UpdateSecret(secretArn=$secretArn,secretValue=*****) completed successfully")
      case Left(l)  => audit.error(show"UpdateSecret(secretArn=$secretArn,secretValue=*****) failed. Details: $l")
    }
    result
  }

  /** Destroy a secret
    *
    * Call Audit.info with an informative message if destroy request process
    * are successful completed otherwise call Audit.error with an error message
    *
    * @param key: the arn of the already existing secret
    * @return Right() if destroy succeed
    *         Left(SecretsGatewayError) otherwise
    */
  override def destroy(key: String): Either[SecretsGatewayError, Unit] = {
    val result = secretsGateway.destroy(key)
    result match {
      case Right(_) => audit.info(show"DestroySecret(key=$key) completed successfully")
      case Left(l)  => audit.error(show"DestroySecret(key=$key) failed. Details: $l")
    }
    result
  }

  /** Check whether a secret has been scheduled for deletion
    * Call Audit.info with an informative message if check process
    * is successful completed otherwise call Audit.error with an error message
    * @param key: the secret id
    * @return Right() if the process succeed
    *         Left(SecretsGatewayError) otherwise
    */
  override def isSecretScheduledForDeletion(key: String): Either[SecretsGatewayError, Boolean] = {
    val result = secretsGateway.isSecretScheduledForDeletion(key)
    result match {
      case Right(_) => audit.info(show"IsSecretScheduledForDeletion(key=$key) completed successfully")
      case Left(l)  => audit.error(show"IsSecretScheduledForDeletion(key=$key) failed. Details: $l")
    }
    result
  }

  /** Restore a secret
    * Call Audit.info with an informative message if restore process
    * is successful completed otherwise call Audit.error with an error message
    * @param key: the secret id
    * @return Right() if the process succeed
    *         Left(SecretsGatewayError) otherwise
    */
  override def restoreSecret(key: String): Either[SecretsGatewayError, Unit] = {
    val result = secretsGateway.restoreSecret(key)
    result match {
      case Right(_) => audit.info(show"RestoreSecret(key=$key) completed successfully")
      case Left(l)  => audit.error(show"RestoreSecret(key=$key) failed. Details: $l")
    }
    result
  }

}
