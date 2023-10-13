package it.agilelab.provisioning.aws.secrets.gateway

import it.agilelab.provisioning.aws.secrets.gateway.SecretsGatewayError.{
  CreateSecretErr,
  DestroySecretErr,
  GetSecretErr,
  IsSecretScheduledForDeletionErr,
  RestoreSecretErr,
  UpdateSecretErr
}
import it.agilelab.provisioning.aws.secrets.gateway.model.AwsSecret
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.{
  CreateSecretRequest,
  DeleteSecretRequest,
  DescribeSecretRequest,
  GetSecretValueRequest,
  ResourceNotFoundException,
  RestoreSecretRequest,
  UpdateSecretRequest
}

class DefaultSecretsGateway(secretsClient: SecretsManagerClient) extends SecretsGateway {
  override def get(key: String): Either[SecretsGatewayError, Option[AwsSecret]] =
    try {
      val secret = secretsClient
        .getSecretValue(
          GetSecretValueRequest
            .builder()
            .secretId(key)
            .build()
        )
      Right(Some(AwsSecret(secret.arn, secret.name, secret.secretString)))
    } catch {
      case _: ResourceNotFoundException => Right(None)
      case t: Throwable                 => Left(GetSecretErr(key, t))
    }

  override def create(secretName: String, secretValue: String): Either[SecretsGatewayError, Unit] =
    try {
      secretsClient.createSecret(
        CreateSecretRequest
          .builder()
          .name(secretName)
          .secretString(secretValue)
          .build()
      )
      Right()
    } catch { case t: Throwable => Left(CreateSecretErr(secretName, t)) }

  override def update(secretArn: String, secretValue: String): Either[SecretsGatewayError, Unit]  =
    try {
      secretsClient.updateSecret(
        UpdateSecretRequest
          .builder()
          .secretId(secretArn)
          .secretString(secretValue)
          .build()
      )
      Right()
    } catch { case t: Throwable => Left(UpdateSecretErr(secretArn, t)) }

  override def destroy(key: String): Either[SecretsGatewayError, Unit]                            =
    try {
      secretsClient.deleteSecret(
        DeleteSecretRequest
          .builder()
          .secretId(key)
          .build()
      )
      Right()
    } catch {
      case t: Throwable => Left(DestroySecretErr(key, t))
    }

  override def isSecretScheduledForDeletion(key: String): Either[SecretsGatewayError, Boolean] =
    try {
      val resp = secretsClient
        .describeSecret(
          DescribeSecretRequest
            .builder()
            .secretId(key)
            .build()
        )
      Right(resp.deletedDate() != null)
    } catch {
      case _: ResourceNotFoundException => Right(false)
      case t: Throwable                 => Left(IsSecretScheduledForDeletionErr(key, t))
    }

  override def restoreSecret(key: String): Either[SecretsGatewayError, Unit] =
    try {
      secretsClient.restoreSecret(
        RestoreSecretRequest
          .builder()
          .secretId(key)
          .build()
      )
      Right()
    } catch {
      case t: Throwable => Left(RestoreSecretErr(key, t))
    }
}
