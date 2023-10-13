package it.agilelab.provisioning.aws.secrets.gateway

import it.agilelab.provisioning.aws.secrets.gateway.SecretsGatewayError.{
  CreateSecretErr,
  DestroySecretErr,
  GetSecretErr,
  IsSecretScheduledForDeletionErr,
  RestoreSecretErr,
  UpdateSecretErr
}
import org.scalatest.EitherValues._

trait SecretsGatewayTestSupport {
  def assertGetSecretErr[A](
    actual: Either[SecretsGatewayError, A],
    key: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[GetSecretErr])
    assert(actual.left.value.asInstanceOf[GetSecretErr].key == key)
    assert(actual.left.value.asInstanceOf[GetSecretErr].error.getMessage == error)
  }

  def assertCreateSecretErr[A](
    actual: Either[SecretsGatewayError, A],
    secretName: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[CreateSecretErr])
    assert(actual.left.value.asInstanceOf[CreateSecretErr].secretName == secretName)
    assert(actual.left.value.asInstanceOf[CreateSecretErr].error.getMessage == error)
  }

  def assertUpdateSecretErr[A](
    actual: Either[SecretsGatewayError, A],
    secretArn: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[UpdateSecretErr])
    assert(actual.left.value.asInstanceOf[UpdateSecretErr].secretArn == secretArn)
    assert(actual.left.value.asInstanceOf[UpdateSecretErr].error.getMessage == error)
  }

  def assertDestroySecretErr[A](
    actual: Either[SecretsGatewayError, A],
    key: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[DestroySecretErr])
    assert(actual.left.value.asInstanceOf[DestroySecretErr].key == key)
    assert(actual.left.value.asInstanceOf[DestroySecretErr].error.getMessage == error)
  }

  def assertIsSecretScheduledForDeletionErr[A](
    actual: Either[SecretsGatewayError, A],
    key: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[IsSecretScheduledForDeletionErr])
    assert(actual.left.value.asInstanceOf[IsSecretScheduledForDeletionErr].key == key)
    assert(actual.left.value.asInstanceOf[IsSecretScheduledForDeletionErr].error.getMessage == error)
  }

  def assertRestoreSecretErr[A](
    actual: Either[SecretsGatewayError, A],
    key: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[RestoreSecretErr])
    assert(actual.left.value.asInstanceOf[RestoreSecretErr].key == key)
    assert(actual.left.value.asInstanceOf[RestoreSecretErr].error.getMessage == error)
  }
}
