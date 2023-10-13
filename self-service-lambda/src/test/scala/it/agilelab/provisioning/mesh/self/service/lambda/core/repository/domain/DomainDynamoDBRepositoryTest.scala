package it.agilelab.provisioning.mesh.self.service.lambda.core.repository.domain

import it.agilelab.provisioning.mesh.repository.RepositoryError.{
  CreateEntityFailureErr,
  DeleteEntityErr,
  EntityAlreadyExistsErr,
  EntityDoesNotExists,
  UpdateEntityFailureErr
}
import it.agilelab.provisioning.mesh.repository.dynamo.DynamoDBRepository
import it.agilelab.provisioning.mesh.repository.dynamo.model.{ Item, ScanFilter }
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.{ Domain, Role }
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class DomainDynamoDBRepositoryTest extends AnyFunSuite with MockFactory with EitherValues {

  val dynamoRepo: DynamoDBRepository       = stub[DynamoDBRepository]
  val repository: DomainDynamoDBRepository = new DomainDynamoDBRepository(dynamoRepo)
  val key1: Item                           = Item(
    Map(
      "key_id"       -> AttributeValue.builder().s("domain#x").build(),
      "range_key_id" -> AttributeValue.builder().s("METADATA").build()
    )
  )
  val item1: Item                          = Item(
    Map(
      "key_id"            -> AttributeValue.builder().s("domain#x").build(),
      "range_key_id"      -> AttributeValue.builder().s("x").build(),
      "domain_short_name" -> AttributeValue.builder().s("y").build()
    )
  )
  val item2: Item                          = Item(
    Map(
      "key_id"            -> AttributeValue.builder().s("domain#i").build(),
      "range_key_id"      -> AttributeValue.builder().s("i").build(),
      "domain_short_name" -> AttributeValue.builder().s("j").build()
    )
  )

  test("findById return Right(None)") {
    (dynamoRepo.findById _)
      .when(key1)
      .returns(Right(None))

    assert(repository.findById("x") == Right(None))
  }

  test("findById return Right(Some(Domain))") {
    (dynamoRepo.findById _)
      .when(key1)
      .returns(Right(Some(item1)))

    assert(repository.findById("x") == Right(Some(Domain("x", "y"))))
  }

  test("findAll return Right(SeqEmpty))") {
    (dynamoRepo.findAll _)
      .when(where { filter: Option[ScanFilter] =>
        filter.exists(s =>
          s.filterExpression == "range_key_id = :v" &&
            s.filterAttributeValues == Map(":v" -> AttributeValue.builder().s("METADATA").build())
        )
      })
      .returns(Right(Seq.empty))
    assert(repository.findAll(None) == Right(Seq.empty))
  }

  test("findAll return Right(Seq(Domain)))") {
    (dynamoRepo.findAll _)
      .when(where { filter: Option[ScanFilter] =>
        filter.exists(s =>
          s.filterExpression == "range_key_id = :v" &&
            s.filterAttributeValues == Map(":v" -> AttributeValue.builder().s("METADATA").build())
        )
      })
      .returns(Right(Seq(item1, item2)))
    assert(repository.findAll(None) == Right(Seq(Domain("x", "y"), Domain("i", "j"))))
  }

  test("create return Right()") {
    (dynamoRepo.create _)
      .when(
        Item(
          Map(
            "key_id"            -> AttributeValue.builder().s("domain#name").build(),
            "range_key_id"      -> AttributeValue.builder().s("METADATA").build(),
            "domain_short_name" -> AttributeValue.builder().s("shortName").build()
          )
        )
      )
      .returns(Right())

    assert(repository.create(Domain("name", "shortName")) == Right())
  }

  test("create return Left() on EntityAlreadyExists") {
    (dynamoRepo.create _)
      .when(
        Item(
          Map(
            "key_id"            -> AttributeValue.builder().s("domain#name").build(),
            "range_key_id"      -> AttributeValue.builder().s("METADATA").build(),
            "domain_short_name" -> AttributeValue.builder().s("shortName").build()
          )
        )
      )
      .returns(
        Left(
          EntityAlreadyExistsErr(
            Item(
              Map(
                "key_id"            -> AttributeValue.builder().s("domain#name").build(),
                "range_key_id"      -> AttributeValue.builder().s("METADATA").build(),
                "domain_short_name" -> AttributeValue.builder().s("shortName").build()
              )
            )
          )
        )
      )

    assert(repository.create(Domain("name", "shortName")).isLeft)
  }

  test("create return Left() on CreateEntityFailure") {
    (dynamoRepo.create _)
      .when(
        Item(
          Map(
            "key_id"            -> AttributeValue.builder().s("domain#name").build(),
            "range_key_id"      -> AttributeValue.builder().s("METADATA").build(),
            "domain_short_name" -> AttributeValue.builder().s("shortName").build()
          )
        )
      )
      .returns(
        Left(
          CreateEntityFailureErr(
            Item(
              Map(
                "key_id"            -> AttributeValue.builder().s("domain#name").build(),
                "range_key_id"      -> AttributeValue.builder().s("METADATA").build(),
                "domain_short_name" -> AttributeValue.builder().s("shortName").build()
              )
            ),
            new IllegalArgumentException("x")
          )
        )
      )

    assert(repository.create(Domain("name", "shortName")).isLeft)
  }

  test("update return Right()") {
    (dynamoRepo.update _)
      .when(
        Item(
          Map(
            "key_id"            -> AttributeValue.builder().s("domain#name").build(),
            "range_key_id"      -> AttributeValue.builder().s("METADATA").build(),
            "domain_short_name" -> AttributeValue.builder().s("shortName").build()
          )
        )
      )
      .returns(Right())

    assert(repository.update(Domain("name", "shortName")) == Right())
  }

  test("update return Left() on EntityDoesNotExists") {
    (dynamoRepo.update _)
      .when(
        Item(
          Map(
            "key_id"            -> AttributeValue.builder().s("domain#name").build(),
            "range_key_id"      -> AttributeValue.builder().s("METADATA").build(),
            "domain_short_name" -> AttributeValue.builder().s("shortName").build()
          )
        )
      )
      .returns(Left(EntityDoesNotExists()))

    assert(repository.update(Domain("name", "shortName")).isLeft)
  }

  test("update return Left() on UpdateEntityFailureErr") {
    (dynamoRepo.update _)
      .when(
        Item(
          Map(
            "key_id"            -> AttributeValue.builder().s("domain#name").build(),
            "range_key_id"      -> AttributeValue.builder().s("METADATA").build(),
            "domain_short_name" -> AttributeValue.builder().s("shortName").build()
          )
        )
      )
      .returns(
        Left(
          UpdateEntityFailureErr(
            Item(
              Map(
                "key_id"            -> AttributeValue.builder().s("domain#name").build(),
                "range_key_id"      -> AttributeValue.builder().s("METADATA").build(),
                "domain_short_name" -> AttributeValue.builder().s("shortName").build()
              )
            ),
            new IllegalArgumentException("x")
          )
        )
      )

    assert(repository.update(Domain("name", "shortName")).isLeft)
  }

  test("delete return Right()") {
    (dynamoRepo.delete _)
      .when(
        Item(
          Map(
            "key_id"       -> AttributeValue.builder().s("domain#name").build(),
            "range_key_id" -> AttributeValue.builder().s("METADATA").build()
          )
        )
      )
      .returns(Right())

    assert(repository.delete("name") == Right())
  }

  test("delete return Left()") {
    (dynamoRepo.delete _)
      .when(
        Item(
          Map(
            "key_id"       -> AttributeValue.builder().s("domain#name").build(),
            "range_key_id" -> AttributeValue.builder().s("METADATA").build()
          )
        )
      )
      .returns(
        Left(
          DeleteEntityErr(
            Item(
              Map(
                "key_id"       -> AttributeValue.builder().s("domain#name").build(),
                "range_key_id" -> AttributeValue.builder().s("METADATA").build()
              )
            ),
            new IllegalArgumentException("x")
          )
        )
      )

    assert(repository.delete("name").isLeft)
  }
}
