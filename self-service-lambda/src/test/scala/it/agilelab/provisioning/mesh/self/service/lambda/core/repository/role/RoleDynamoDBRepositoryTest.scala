package it.agilelab.provisioning.mesh.self.service.lambda.core.repository.role

import it.agilelab.provisioning.mesh.repository.RepositoryError
import it.agilelab.provisioning.mesh.repository.RepositoryError.{
  CreateEntityFailureErr,
  DeleteEntityErr,
  FindAllEntitiesErr,
  FindEntityByIdErr,
  UpdateEntityFailureErr
}
import it.agilelab.provisioning.mesh.repository.dynamo.DynamoDBRepository
import it.agilelab.provisioning.mesh.repository.dynamo.model.Item
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.Role
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class RoleDynamoDBRepositoryTest extends AnyFunSuite with MockFactory with EitherValues {

  val dynamoDBRepo: DynamoDBRepository = stub[DynamoDBRepository]
  val repository                       = new RoleDynamoDBRepository(dynamoDBRepo)
  val key1: Item                       = Item(
    Map(
      "key_id" -> AttributeValue.builder().s("role#r").build()
    )
  )
  val item1: Item                      = Item(
    Map(
      "key_id"       -> AttributeValue.builder().s("role#r").build(),
      "domain"       -> AttributeValue.builder().s("d").build(),
      "iam_role"     -> AttributeValue.builder().s("ir").build(),
      "iam_role_arn" -> AttributeValue.builder().s("ira").build(),
      "ad_role"      -> AttributeValue.builder().s("ar").build(),
      "cdp_role_crn" -> AttributeValue.builder().s("crc").build()
    )
  )
  val item2: Item                      = Item(
    Map(
      "key_id"       -> AttributeValue.builder().s("role#r2").build(),
      "domain"       -> AttributeValue.builder().s("d2").build(),
      "iam_role"     -> AttributeValue.builder().s("ir2").build(),
      "iam_role_arn" -> AttributeValue.builder().s("ira2").build(),
      "ad_role"      -> AttributeValue.builder().s("ar2").build(),
      "cdp_role_crn" -> AttributeValue.builder().s("crc2").build()
    )
  )

  test("findById return Right(None)") {
    (dynamoDBRepo.findById _)
      .when(key1)
      .returns(Right(None))
    assert(repository.findById("r") == Right(None))
  }

  test("findById return Right(Some(Role))") {
    (dynamoDBRepo.findById _)
      .when(key1)
      .returns(Right(Some(item1)))

    val expected = Right(Some(Role("r", "d", "ir", "ira", "ar", "crc")))
    assert(repository.findById("r") == expected)
  }

  test("findById return Left()") {
    (dynamoDBRepo.findById _)
      .when(key1)
      .returns(Left(FindEntityByIdErr(key1, new IllegalArgumentException("x"))))
    val actual = repository.findById("r")

    assert(actual.isLeft)
  }

  test("findAll return Right(SeqEmpty)") {
    (dynamoDBRepo.findAll _)
      .when(None)
      .returns(Right(Seq.empty))

    assert(repository.findAll(None) == Right(Seq.empty))
  }

  test("findAll return Right(Seq(Role))") {
    (dynamoDBRepo.findAll _)
      .when(None)
      .returns(
        Right(
          Seq(item1, item2)
        )
      )

    val expected = Right(
      Seq(
        Role("r", "d", "ir", "ira", "ar", "crc"),
        Role("r2", "d2", "ir2", "ira2", "ar2", "crc2")
      )
    )

    assert(repository.findAll(None) == expected)
  }

  test("findAll return Left()") {
    (dynamoDBRepo.findAll _)
      .when(None)
      .returns(
        Left(FindAllEntitiesErr(None, new IllegalArgumentException("x")))
      )

    val actual = repository.findAll(None)
    assert(actual.isLeft)
  }

  test("create return Left() not implemented") {
    assert(repository.create(Role("a", "b", "c", "d", "e", "f")).isLeft)
  }

  test("delete return Left() not implemented") {
    assert(repository.delete("x").isLeft)
  }

  test("update return Left() not implemented") {
    assert(repository.update(Role("a", "b", "c", "d", "e", "f")).isLeft)
  }
}
