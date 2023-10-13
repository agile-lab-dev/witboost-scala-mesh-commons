package it.agilelab.provisioning.aws.secrets.gateway

import it.agilelab.provisioning.aws.secrets.gateway.model.AwsSecret
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.{
  CreateSecretRequest,
  CreateSecretResponse,
  DeleteSecretRequest,
  DeleteSecretResponse,
  DescribeSecretRequest,
  DescribeSecretResponse,
  GetSecretValueRequest,
  GetSecretValueResponse,
  ResourceNotFoundException,
  RestoreSecretRequest,
  RestoreSecretResponse,
  UpdateSecretRequest,
  UpdateSecretResponse
}

import java.time.Instant

class DefaultSecretsGatewayTest extends AnyFunSuite with MockFactory with SecretsGatewayTestSupport {

  test("get string secret return Right(Some(String))") {
    val client = stub[SecretsManagerClient]
    (client
      .getSecretValue(_: GetSecretValueRequest))
      .when(GetSecretValueRequest.builder().secretId("my-secret-id").build())
      .returns(GetSecretValueResponse.builder().arn("arn").name("name").secretString("my-secret-string").build())

    val secretsGateway = new DefaultSecretsGateway(client)
    assert(secretsGateway.get("my-secret-id") == Right(Some(AwsSecret("arn", "name", "my-secret-string"))))
  }

  test("get string secret return Right(None)") {
    val client = stub[SecretsManagerClient]
    (client
      .getSecretValue(_: GetSecretValueRequest))
      .when(GetSecretValueRequest.builder().secretId("my-secret-id").build())
      .throws(ResourceNotFoundException.builder().message("x").build())

    val secretsGateway = new DefaultSecretsGateway(client)
    assert(secretsGateway.get("my-secret-id") == Right(None))
  }

  test("get return Left(GetSecretErr)") {
    val client = stub[SecretsManagerClient]
    (client
      .getSecretValue(_: GetSecretValueRequest))
      .when(GetSecretValueRequest.builder().secretId("my-secret-id").build())
      .throws(SdkClientException.create("x"))

    val secretsGateway = new DefaultSecretsGateway(client)
    assertGetSecretErr(secretsGateway.get("my-secret-id"), "my-secret-id", "x")
  }

  test("create string secret return Right()") {
    val client = stub[SecretsManagerClient]
    (client
      .createSecret(_: CreateSecretRequest))
      .when(CreateSecretRequest.builder().name("my-secret-name").secretString("my-secret-string").build())
      .returns(CreateSecretResponse.builder().arn("arn").build())

    val secretsGateway = new DefaultSecretsGateway(client)
    assert(secretsGateway.create("my-secret-name", "my-secret-string") == Right())
  }

  test("create return Left(CreateSecretErr)") {
    val client = stub[SecretsManagerClient]
    (client
      .createSecret(_: CreateSecretRequest))
      .when(CreateSecretRequest.builder().name("my-secret-name").secretString("my-secret-string").build())
      .throws(SdkClientException.create("x"))

    val secretsGateway = new DefaultSecretsGateway(client)
    assertCreateSecretErr(secretsGateway.create("my-secret-name", "my-secret-string"), "my-secret-name", "x")
  }

  test("update string secret return Right()") {
    val client = stub[SecretsManagerClient]
    (client
      .updateSecret(_: UpdateSecretRequest))
      .when(UpdateSecretRequest.builder().secretId("arn").secretString("my-secret-string").build())
      .returns(UpdateSecretResponse.builder().arn("arn").build())

    val secretsGateway = new DefaultSecretsGateway(client)
    assert(secretsGateway.update("arn", "my-secret-string") == Right())
  }

  test("update return Left(UpdateSecretErr)") {
    val client = stub[SecretsManagerClient]
    (client
      .updateSecret(_: UpdateSecretRequest))
      .when(UpdateSecretRequest.builder().secretId("arn").secretString("my-secret-string").build())
      .throws(SdkClientException.create("x"))

    val secretsGateway = new DefaultSecretsGateway(client)
    assertUpdateSecretErr(secretsGateway.update("arn", "my-secret-string"), "arn", "x")
  }

  test("destroy secret return Right()") {
    val client = stub[SecretsManagerClient]
    (client
      .deleteSecret(_: DeleteSecretRequest))
      .when(DeleteSecretRequest.builder().secretId("arn").build())
      .returns(DeleteSecretResponse.builder().arn("arn").build())

    val secretsGateway = new DefaultSecretsGateway(client)
    assert(secretsGateway.destroy("arn") == Right())
  }

  test("destroy return Left(UpdateSecretErr)") {
    val client = stub[SecretsManagerClient]
    (client
      .deleteSecret(_: DeleteSecretRequest))
      .when(DeleteSecretRequest.builder().secretId("arn").build())
      .throws(SdkClientException.create("x"))

    val secretsGateway = new DefaultSecretsGateway(client)
    assertDestroySecretErr(secretsGateway.destroy("arn"), "arn", "x")
  }

  test("isSecretScheduledForDeletion return Right(true)") {
    val client = stub[SecretsManagerClient]
    (client
      .describeSecret(_: DescribeSecretRequest))
      .when(DescribeSecretRequest.builder().secretId("arn").build())
      .returns(DescribeSecretResponse.builder().arn("arn").deletedDate(Instant.now()).build())

    val secretsGateway = new DefaultSecretsGateway(client)
    assert(secretsGateway.isSecretScheduledForDeletion("arn") == Right(true))
  }

  test("isSecretScheduledForDeletion return Right(false)") {
    val client = stub[SecretsManagerClient]
    (client
      .describeSecret(_: DescribeSecretRequest))
      .when(DescribeSecretRequest.builder().secretId("arn").build())
      .returns(DescribeSecretResponse.builder().arn("arn").deletedDate(null).build())

    val secretsGateway = new DefaultSecretsGateway(client)
    assert(secretsGateway.isSecretScheduledForDeletion("arn") == Right(false))
  }

  test("isSecretScheduledForDeletion return Right(false) when resource was not found") {
    val client = stub[SecretsManagerClient]
    (client
      .describeSecret(_: DescribeSecretRequest))
      .when(DescribeSecretRequest.builder().secretId("arn").build())
      .throws(ResourceNotFoundException.builder().message("x").build())

    val secretsGateway = new DefaultSecretsGateway(client)
    assert(secretsGateway.isSecretScheduledForDeletion("arn") == Right(false))
  }

  test("isSecretScheduledForDeletion return Left(IsSecretScheduledForDeletionErr)") {
    val client = stub[SecretsManagerClient]
    (client
      .describeSecret(_: DescribeSecretRequest))
      .when(DescribeSecretRequest.builder().secretId("arn").build())
      .throws(SdkClientException.create("x"))

    val secretsGateway = new DefaultSecretsGateway(client)
    assertIsSecretScheduledForDeletionErr(secretsGateway.isSecretScheduledForDeletion("arn"), "arn", "x")
  }

  test("restoreSecret return Right()") {
    val client = stub[SecretsManagerClient]
    (client
      .restoreSecret(_: RestoreSecretRequest))
      .when(RestoreSecretRequest.builder().secretId("arn").build())
      .returns(RestoreSecretResponse.builder().arn("arn").build())

    val secretsGateway = new DefaultSecretsGateway(client)
    assert(secretsGateway.restoreSecret("arn") == Right())
  }

  test("restoreSecret return Left(RestoreSecretErr)") {
    val client = stub[SecretsManagerClient]
    (client
      .restoreSecret(_: RestoreSecretRequest))
      .when(RestoreSecretRequest.builder().secretId("arn").build())
      .throws(SdkClientException.create("x"))

    val secretsGateway = new DefaultSecretsGateway(client)
    assertRestoreSecretErr(secretsGateway.restoreSecret("arn"), "arn", "x")
  }

}
