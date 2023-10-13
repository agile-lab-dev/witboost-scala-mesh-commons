package it.agilelab.provisioning.mesh.self.service.lambda.core.repository.provisioningstatus

import it.agilelab.provisioning.mesh.repository.RepositoryError.{
  CreateEntityFailureErr,
  DeleteEntityErr,
  EntityAlreadyExistsErr,
  EntityDoesNotExists,
  FindAllEntitiesErr,
  FindEntityByIdErr,
  UpdateEntityFailureErr
}
import it.agilelab.provisioning.mesh.repository.dynamo.DynamoDBRepository
import it.agilelab.provisioning.mesh.repository.dynamo.model.Item
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.{ COMPLETED, FAILED, RUNNING }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class ProvisioningStatusDynamoDBRepositoryTest extends AnyFunSuite with MockFactory {

  val dynamoDBRepository: DynamoDBRepository = stub[DynamoDBRepository]
  val repository                             = new ProvisioningStatusDynamoDBRepository(dynamoDBRepository)

  test("findById return Right(None)") {
    (dynamoDBRepository.findById _)
      .when(Item(Map("request_id" -> AttributeValue.builder().s("xyz").build())))
      .returns(Right(None))

    assert(repository.findById("xyz") == Right(None))
  }

  test("findById return Right(ProvisioningStatus) with Some(ErrorInfo)") {
    (dynamoDBRepository.findById _)
      .when(Item(Map("request_id" -> AttributeValue.builder().s("xyz").build())))
      .returns(
        Right(
          Some(
            Item(
              Map(
                "request_id"     -> AttributeValue.builder().s("xyz").build(),
                "request_status" -> AttributeValue.builder().s("FAILED").build(),
                "request_result" -> AttributeValue.builder().s("ERROR").build()
              )
            )
          )
        )
      )

    val actual   = repository.findById("xyz")
    val expected = Right(Some(ProvisioningStatus("xyz", FAILED, Some("ERROR"))))
    assert(actual == expected)
  }

  test("findById return Right(ProvisioningStatus) with Some(SuccessInfo)") {
    (dynamoDBRepository.findById _)
      .when(Item(Map("request_id" -> AttributeValue.builder().s("xyz").build())))
      .returns(
        Right(
          Some(
            Item(
              Map(
                "request_id"     -> AttributeValue.builder().s("xyz").build(),
                "request_status" -> AttributeValue.builder().s("COMPLETED").build(),
                "request_result" -> AttributeValue.builder().s("SUCCESS").build()
              )
            )
          )
        )
      )

    val actual   = repository.findById("xyz")
    val expected = Right(Some(ProvisioningStatus("xyz", COMPLETED, Some("SUCCESS"))))
    assert(actual == expected)
  }

  test("findById return Left()") {
    (dynamoDBRepository.findById _)
      .when(Item(Map("request_id" -> AttributeValue.builder().s("xyz").build())))
      .returns(
        Left(
          FindEntityByIdErr(
            Item(Map("request_id" -> AttributeValue.builder().s("xyz").build())),
            new IllegalArgumentException("x")
          )
        )
      )

    assert(repository.findById("xyz").isLeft)
  }

  test("findAll return Right(Seq(RequestState))") {
    (dynamoDBRepository.findAll _)
      .when(*)
      .returns(
        Right(
          Seq(
            Item(
              Map(
                "request_id"     -> AttributeValue.builder().s("abc").build(),
                "request_status" -> AttributeValue.builder().s("RUNNING").build(),
                "request_result" -> AttributeValue.builder().s("").build()
              )
            ),
            Item(
              Map(
                "request_id"     -> AttributeValue.builder().s("xyz").build(),
                "request_status" -> AttributeValue.builder().s("FAILED").build(),
                "request_result" -> AttributeValue.builder().s("ERROR").build()
              )
            )
          )
        )
      )

    val expected = Right(
      Seq(
        ProvisioningStatus("abc", RUNNING, None),
        ProvisioningStatus("xyz", FAILED, Some("ERROR"))
      )
    )
    assert(repository.findAll(None) == expected)
  }

  test("findAll return Left()") {
    (dynamoDBRepository.findAll _)
      .when(*)
      .returns(Left(FindAllEntitiesErr(None, new IllegalArgumentException("x"))))

    assert(repository.findAll(None).isLeft)
  }

  test("create return Right()") {
    (dynamoDBRepository.create _)
      .when(
        Item(
          Map(
            "request_id"     -> AttributeValue.builder().s("abc").build(),
            "request_status" -> AttributeValue.builder().s("RUNNING").build(),
            "request_result" -> AttributeValue.builder().s("").build()
          )
        )
      )
      .returns(Right())

    assert(repository.create(ProvisioningStatus("abc", RUNNING, None)) == Right())
  }

  test("create return Left() on EntityAlreadyExists") {
    (dynamoDBRepository.create _)
      .when(
        Item(
          Map(
            "request_id"     -> AttributeValue.builder().s("abc").build(),
            "request_status" -> AttributeValue.builder().s("RUNNING").build(),
            "request_result" -> AttributeValue.builder().s("").build()
          )
        )
      )
      .returns(
        Left(
          EntityAlreadyExistsErr(
            Item(
              Map(
                "request_id"     -> AttributeValue.builder().s("abc").build(),
                "request_status" -> AttributeValue.builder().s("RUNNING").build(),
                "request_result" -> AttributeValue.builder().s("").build()
              )
            )
          )
        )
      )

    assert(repository.create(ProvisioningStatus("abc", RUNNING, None)).isLeft)
  }

  test("create return Left() on CreateEntityFailure") {
    (dynamoDBRepository.create _)
      .when(
        Item(
          Map(
            "request_id"     -> AttributeValue.builder().s("abc").build(),
            "request_status" -> AttributeValue.builder().s("RUNNING").build(),
            "request_result" -> AttributeValue.builder().s("").build()
          )
        )
      )
      .returns(
        Left(
          CreateEntityFailureErr(
            Item(
              Map(
                "request_id"     -> AttributeValue.builder().s("abc").build(),
                "request_status" -> AttributeValue.builder().s("RUNNING").build(),
                "request_result" -> AttributeValue.builder().s("").build()
              )
            ),
            new IllegalArgumentException("x")
          )
        )
      )

    assert(repository.create(ProvisioningStatus("abc", RUNNING, None)).isLeft)
  }

  test("update return Right()") {
    (dynamoDBRepository.update _)
      .when(
        Item(
          Map(
            "request_id"     -> AttributeValue.builder().s("abc").build(),
            "request_status" -> AttributeValue.builder().s("RUNNING").build(),
            "request_result" -> AttributeValue.builder().s("").build()
          )
        )
      )
      .returns(Right())

    assert(repository.update(ProvisioningStatus("abc", RUNNING, None)) == Right())
  }

  test("update return Left() on EntityDoesNotExists") {
    (dynamoDBRepository.update _)
      .when(
        Item(
          Map(
            "request_id"     -> AttributeValue.builder().s("abc").build(),
            "request_status" -> AttributeValue.builder().s("RUNNING").build(),
            "request_result" -> AttributeValue.builder().s("").build()
          )
        )
      )
      .returns(Left(EntityDoesNotExists()))

    assert(repository.update(ProvisioningStatus("abc", RUNNING, None)).isLeft)
  }

  test("update return Left() on UpdateEntityFailureErr") {
    (dynamoDBRepository.update _)
      .when(
        Item(
          Map(
            "request_id"     -> AttributeValue.builder().s("abc").build(),
            "request_status" -> AttributeValue.builder().s("RUNNING").build(),
            "request_result" -> AttributeValue.builder().s("").build()
          )
        )
      )
      .returns(
        Left(
          UpdateEntityFailureErr(
            Item(
              Map(
                "request_id"     -> AttributeValue.builder().s("abc").build(),
                "request_status" -> AttributeValue.builder().s("RUNNING").build(),
                "request_result" -> AttributeValue.builder().s("").build()
              )
            ),
            new IllegalArgumentException("x")
          )
        )
      )

    assert(repository.update(ProvisioningStatus("abc", RUNNING, None)).isLeft)
  }

  test("delete return Right()") {
    (dynamoDBRepository.delete _)
      .when(Item(Map("request_id" -> AttributeValue.builder().s("xyz").build())))
      .returns(Right())

    assert(repository.delete("xyz") == Right())
  }

  test("delete return Left()") {
    (dynamoDBRepository.delete _)
      .when(Item(Map("request_id" -> AttributeValue.builder().s("xyz").build())))
      .returns(
        Left(
          DeleteEntityErr(
            Item(Map("request_id" -> AttributeValue.builder().s("xyz").build())),
            new IllegalArgumentException("x")
          )
        )
      )

    assert(repository.delete("xyz").isLeft)
  }

}
