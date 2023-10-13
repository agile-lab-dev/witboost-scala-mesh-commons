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
import it.agilelab.provisioning.commons.audit.Audit
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.core.exception.SdkClientException

class DefaultSecretsGatewayWithAuditTest extends AnyFunSuite with MockFactory with SecretsGatewayTestSupport {

  val defaultSecretsGateway: SecretsGateway = stub[SecretsGateway]
  val audit: Audit                          = mock[Audit]
  val secretsGateway                        = new DefaultSecretsGatewayWithAudit(defaultSecretsGateway, audit)

  test("get call info on success") {
    (defaultSecretsGateway.get _)
      .when("id")
      .returns(Right(Some(AwsSecret("arn", "name", "my-secret-string"))))
    (audit.info _)
      .expects("GetSecret(key=id) completed successfully")
      .once()
    assert(secretsGateway.get("id") == Right(Some(AwsSecret("arn", "name", "my-secret-string"))))
  }

  test("get call error on failure") {
    (defaultSecretsGateway.get _)
      .when("id")
      .returns(Left(GetSecretErr("id", SdkClientException.create("xyz"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith(
          "GetSecret(key=id) failed. Details: GetSecretErr(id,software.amazon.awssdk.core.exception.SdkClientException: xyz"
        )
      })
      .once()
    assertGetSecretErr(secretsGateway.get("id"), "id", "xyz")
  }

  test("create call info on success") {
    (defaultSecretsGateway.create _)
      .when("name", "value")
      .returns(Right())
    (audit.info _)
      .expects("CreateSecret(secretName=name,secretValue=*****) completed successfully")
      .once()
    assert(secretsGateway.create("name", "value") == Right())
  }

  test("create call error on failure") {
    (defaultSecretsGateway.create _)
      .when("name", "value")
      .returns(Left(CreateSecretErr("name", SdkClientException.create("xyz"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith(
          "CreateSecret(secretName=name,secretValue=*****) failed. Details: CreateSecretErr(name,software.amazon.awssdk.core.exception.SdkClientException: xyz"
        )
      })
      .once()
    assertCreateSecretErr(secretsGateway.create("name", "value"), "name", "xyz")
  }

  test("update call info on success") {
    (defaultSecretsGateway.update _)
      .when("arn", "value")
      .returns(Right())
    (audit.info _)
      .expects("UpdateSecret(secretArn=arn,secretValue=*****) completed successfully")
      .once()
    assert(secretsGateway.update("arn", "value") == Right())
  }

  test("update call error on failure") {
    (defaultSecretsGateway.update _)
      .when("arn", "value")
      .returns(Left(UpdateSecretErr("arn", SdkClientException.create("xyz"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith(
          "UpdateSecret(secretArn=arn,secretValue=*****) failed. Details: UpdateSecretErr(arn,software.amazon.awssdk.core.exception.SdkClientException: xyz"
        )
      })
      .once()
    assertUpdateSecretErr(secretsGateway.update("arn", "value"), "arn", "xyz")
  }

  test("destroy call info on success") {
    (defaultSecretsGateway.destroy _)
      .when("arn")
      .returns(Right())
    (audit.info _)
      .expects("DestroySecret(key=arn) completed successfully")
      .once()
    assert(secretsGateway.destroy("arn") == Right())
  }

  test("destroy call error on failure") {
    (defaultSecretsGateway.destroy _)
      .when("arn")
      .returns(Left(DestroySecretErr("arn", SdkClientException.create("xyz"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith(
          "DestroySecret(key=arn) failed. Details: DestroySecretErr(arn,software.amazon.awssdk.core.exception.SdkClientException: xyz"
        )
      })
      .once()
    assertDestroySecretErr(secretsGateway.destroy("arn"), "arn", "xyz")
  }

  test("isSecretScheduledForDeletion call info on success") {
    (defaultSecretsGateway.isSecretScheduledForDeletion _)
      .when("arn")
      .returns(Right(true))
    (audit.info _)
      .expects("IsSecretScheduledForDeletion(key=arn) completed successfully")
      .once()
    assert(secretsGateway.isSecretScheduledForDeletion("arn") == Right(true))
  }

  test("isSecretScheduledForDeletion call error on failure") {
    (defaultSecretsGateway.isSecretScheduledForDeletion _)
      .when("arn")
      .returns(Left(IsSecretScheduledForDeletionErr("arn", SdkClientException.create("xyz"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith(
          "IsSecretScheduledForDeletion(key=arn) failed. Details: IsSecretScheduledForDeletionErr(arn,software.amazon.awssdk.core.exception.SdkClientException: xyz"
        )
      })
      .once()
    assertIsSecretScheduledForDeletionErr(secretsGateway.isSecretScheduledForDeletion("arn"), "arn", "xyz")
  }

  test("restoreSecret call info on success") {
    (defaultSecretsGateway.restoreSecret _)
      .when("arn")
      .returns(Right())
    (audit.info _)
      .expects("RestoreSecret(key=arn) completed successfully")
      .once()
    assert(secretsGateway.restoreSecret("arn") == Right())
  }

  test("restoreSecret call error on failure") {
    (defaultSecretsGateway.restoreSecret _)
      .when("arn")
      .returns(Left(RestoreSecretErr("arn", SdkClientException.create("xyz"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith(
          "RestoreSecret(key=arn) failed. Details: RestoreSecretErr(arn,software.amazon.awssdk.core.exception.SdkClientException: xyz"
        )
      })
      .once()
    assertRestoreSecretErr(secretsGateway.restoreSecret("arn"), "arn", "xyz")
  }

}
