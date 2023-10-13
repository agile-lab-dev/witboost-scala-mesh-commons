package it.agilelab.provisioning.mesh.self.service.lambda.errorhandler

import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.repository.RepositoryError.{ FindEntityByIdErr, UpdateEntityFailureErr }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.{ FAILED, RUNNING }
import io.circe.generic.auto._
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class LambdaErrorHandlerTest extends AnyFunSuite with MockFactory {

  val repository: Repository[ProvisioningStatus, String, Unit] = mock[Repository[ProvisioningStatus, String, Unit]]
  val lambdaErrorHandler                                       = new LambdaErrorHandler[String, String](repository)

  val event =
    "{\"version\":\"1.0\",\"timestamp\":\"x\",\"requestContext\":{\"requestId\":\"x\",\"functionArn\":\"x\",\"condition\":\"RetriesExhausted\",\"approximateInvokeCount\":3},\"requestPayload\":{\"requestId\":\"my-id\",\"provisionRequest\":{\"dataProduct\": { \"id\": \"id\", \"name\": \"name\", \"fullyQualifiedName\": \"fullyQualifiedName\", \"domain\": \"domain\", \"description\": \"description\", \"environment\": \"environment\", \"version\": \"version\", \"kind\": \"kind\", \"dataProductOwner\": \"dataProductOwner\", \"dataProductOwnerDisplayName\": \"dataProductOwnerDisplayName\", \"email\": \"email\", \"informationSLA\": \"informationSLA\", \"status\": \"status\", \"maturity\": \"maturity\", \"billing\": {}, \"tags\": [], \"specific\": \"specific\", \"components\": [] }, \"componentIdToProvision\": \"componentIdToProvision\"}},\"responseContext\":{\"statusCode\":200,\"executedVersion\":\"$LATEST\",\"functionError\":\"Unhandled\"},\"responsePayload\":{\"errorMessage\":\"2021-10-29T14:14:36.068Z 884d8924-a351-49f1-b68d-e2984486a9be Task timed out after 3.00 seconds\"}}"

  test("handle error on json parse") {
    assert(
      lambdaErrorHandler.handle("""{"id": "xyz"}""") == Left(
        "DecodeErr(DecodingFailure at .version: Attempt to decode value on failed cursor)"
      )
    )
  }

  test("handle success recovery error") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (repository.update _)
      .expects(
        ProvisioningStatus(
          "my-id",
          FAILED,
          Some("2021-10-29T14:14:36.068Z 884d8924-a351-49f1-b68d-e2984486a9be Task timed out after 3.00 seconds")
        )
      )
      .once()
      .returns(Right())

    assert(lambdaErrorHandler.handle(event) == Right(event))
  }

  test("handle repo findById None") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Right(None))

    assert(lambdaErrorHandler.handle(event) == Left("Provisioning Status with id: my-id Not found"))
  }

  test("handle repo findById error") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Left(FindEntityByIdErr("my-id", new IllegalArgumentException("y"))))

    val expected = Left("Unable to fetch Provisioning Status information with provided repository.")
    assert(lambdaErrorHandler.handle(event) == expected)
  }

  test("handle repo update error") {
    (repository.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (repository.update _)
      .expects(
        ProvisioningStatus(
          "my-id",
          FAILED,
          Some("2021-10-29T14:14:36.068Z 884d8924-a351-49f1-b68d-e2984486a9be Task timed out after 3.00 seconds")
        )
      )
      .once()
      .returns(
        Left(UpdateEntityFailureErr(ProvisioningStatus("my-id", RUNNING, None), new IllegalArgumentException("y")))
      )

    val expected = Left("Unable to update provisioning status to failed.")
    assert(lambdaErrorHandler.handle(event) == expected)
  }
}
