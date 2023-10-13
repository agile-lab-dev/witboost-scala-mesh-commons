package it.agilelab.provisioning.mesh.self.service.lambda.core.repository.dataproduct

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
import it.agilelab.provisioning.mesh.repository.dynamo.model.{ Item, ScanFilter }
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.{ DataProductEntity, DataProductEntityKey }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class DataProductEntityKeyDynamoDBRepositoryTest extends AnyFunSuite with MockFactory {
  val dynamoDBRepo: DynamoDBRepository             = stub[DynamoDBRepository]
  val repository: DataProductKeyDynamoDBRepository = new DataProductKeyDynamoDBRepository(dynamoDBRepo)
  val item1: Item                                  = Item(
    Map(
      "key_id"       -> AttributeValue.builder().s("domain#x").build(),
      "range_key_id" -> AttributeValue.builder().s("dataproduct#y").build()
    )
  )
  val item2: Item                                  = Item(
    Map(
      "key_id"       -> AttributeValue.builder().s("domain#i").build(),
      "range_key_id" -> AttributeValue.builder().s("dataproduct#j").build()
    )
  )

  test("findById return Right(None)") {
    (dynamoDBRepo.findById _)
      .when(item1)
      .returns(Right(None))

    assert(repository.findById(DataProductEntityKey("x", "y")) == Right(None))
  }

  test("findById return Left") {
    (dynamoDBRepo.findById _)
      .when(item1)
      .returns(Left(FindEntityByIdErr(item1, new IllegalArgumentException("x"))))

    assert(repository.findById(DataProductEntityKey("x", "y")).isLeft)
  }

  test("findById return Right(DataProduct)") {
    (dynamoDBRepo.findById _)
      .when(item1)
      .returns(Right(Some(item1)))

    assert(repository.findById(DataProductEntityKey("x", "y")) == Right(Some(DataProductEntityKey("x", "y"))))
  }

  test("findAll return Right(SeqEmpty)") {
    (dynamoDBRepo.findAll _)
      .when(where { filter: Option[ScanFilter] =>
        filter.exists(s =>
          s.filterExpression == "begins_with(range_key_id, :v)" &&
            s.filterAttributeValues == Map(":v" -> AttributeValue.builder().s("dataproduct").build())
        )
      })
      .returns(Right(Seq.empty))

    assert(repository.findAll(None) == Right(Seq.empty))
  }

  test("findAll return Right(Seq((DataProductKey))") {
    (dynamoDBRepo.findAll _)
      .when(where { filter: Option[ScanFilter] =>
        filter.exists(s =>
          s.filterExpression == "begins_with(range_key_id, :v)" &&
            s.filterAttributeValues == Map(":v" -> AttributeValue.builder().s("dataproduct").build())
        )
      })
      .returns(Right(Seq(item1, item2)))

    assert(repository.findAll(None) == Right(Seq(DataProductEntityKey("x", "y"), DataProductEntityKey("i", "j"))))
  }

  test("findAll return Left()") {
    (dynamoDBRepo.findAll _)
      .when(where { filter: Option[ScanFilter] =>
        filter.exists(s =>
          s.filterExpression == "begins_with(range_key_id, :v)" &&
            s.filterAttributeValues == Map(":v" -> AttributeValue.builder().s("dataproduct").build())
        )
      })
      .returns(Left(FindAllEntitiesErr(None, new IllegalArgumentException("x"))))

    assert(repository.findAll(None).isLeft)
  }

  test("create return Right()") {
    (dynamoDBRepo.create _)
      .when(
        Item(
          Map(
            "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
            "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
          )
        )
      )
      .returns(Right())

    assert(repository.create(DataProductEntityKey("domainName", "dataProductName")) == Right())
  }

  test("create return Left() on EntityAlreadyExists") {
    (dynamoDBRepo.create _)
      .when(
        Item(
          Map(
            "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
            "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
          )
        )
      )
      .returns(
        Left(
          EntityAlreadyExistsErr(
            Item(
              Map(
                "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
                "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
              )
            )
          )
        )
      )

    assert(repository.create(DataProductEntityKey("domainName", "dataProductName")).isLeft)
  }

  test("create return Left() on CreateEntityFailure") {
    (dynamoDBRepo.create _)
      .when(
        Item(
          Map(
            "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
            "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
          )
        )
      )
      .returns(
        Left(
          CreateEntityFailureErr(
            Item(
              Map(
                "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
                "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
              )
            ),
            new IllegalArgumentException("x")
          )
        )
      )

    assert(repository.create(DataProductEntityKey("domainName", "dataProductName")).isLeft)
  }

  test("update return Right()") {
    (dynamoDBRepo.update _)
      .when(
        Item(
          Map(
            "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
            "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
          )
        )
      )
      .returns(Right())

    assert(repository.update(DataProductEntityKey("domainName", "dataProductName")) == Right())
  }

  test("update return Left() on EntityDoesNotExists") {
    (dynamoDBRepo.update _)
      .when(
        Item(
          Map(
            "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
            "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
          )
        )
      )
      .returns(Left(EntityDoesNotExists()))

    assert(repository.update(DataProductEntityKey("domainName", "dataProductName")).isLeft)
  }

  test("update return Left() on UpdateEntityFailureErr") {
    (dynamoDBRepo.update _)
      .when(
        Item(
          Map(
            "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
            "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
          )
        )
      )
      .returns(
        Left(
          UpdateEntityFailureErr(
            Item(
              Map(
                "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
                "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
              )
            ),
            new IllegalArgumentException("x")
          )
        )
      )

    assert(repository.update(DataProductEntityKey("domainName", "dataProductName")).isLeft)
  }

  test("delete return Right()") {
    (dynamoDBRepo.delete _)
      .when(
        Item(
          Map(
            "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
            "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
          )
        )
      )
      .returns(Right())

    assert(repository.delete(DataProductEntityKey("domainName", "dataProductName")) == Right())
  }

  test("delete return Left()") {
    (dynamoDBRepo.delete _)
      .when(
        Item(
          Map(
            "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
            "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
          )
        )
      )
      .returns(
        Left(
          DeleteEntityErr(
            Item(
              Map(
                "key_id"       -> AttributeValue.builder().s("domain#domainName").build(),
                "range_key_id" -> AttributeValue.builder().s("dataproduct#dataProductName").build()
              )
            ),
            new IllegalArgumentException("x")
          )
        )
      )

    assert(repository.delete(DataProductEntityKey("domainName", "dataProductName")).isLeft)
  }
}
