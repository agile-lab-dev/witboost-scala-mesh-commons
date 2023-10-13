package it.agilelab.provisioning.mesh.repository.dynamo

import it.agilelab.provisioning.mesh.repository.RepositoryError._
import it.agilelab.provisioning.mesh.repository.RepositoryTestSupport
import it.agilelab.provisioning.mesh.repository.dynamo.model.{ Item, ScanFilter }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.{
  AttributeValue,
  ConditionalCheckFailedException,
  DeleteItemRequest,
  DeleteItemResponse,
  GetItemRequest,
  GetItemResponse,
  PutItemRequest,
  PutItemResponse,
  ScanRequest,
  ScanResponse,
  UpdateItemRequest,
  UpdateItemResponse
}

import scala.jdk.CollectionConverters.{ IterableHasAsJava, MapHasAsJava }

class DynamoDBRepositoryTest extends AnyFunSuite with MockFactory with RepositoryTestSupport {

  val amazonDynamoDB: DynamoDbClient                   = mock[DynamoDbClient]
  val repository: DefaultDynamoDBRepository            = new DefaultDynamoDBRepository(amazonDynamoDB, "table", "key", None)
  val repositoryWithSortKey: DefaultDynamoDBRepository =
    new DefaultDynamoDBRepository(amazonDynamoDB, "table", "key", Some("sortKey"))

  val key1: Map[String, AttributeValue]           = Map("key" -> AttributeValue.builder().s("my-key").build())
  val key2: Map[String, AttributeValue]           = Map("key" -> AttributeValue.builder().s("my-key-2").build())
  val item1: Map[String, AttributeValue]          = Map(
    "key"   -> AttributeValue.builder().s("my-key").build(),
    "field" -> AttributeValue.builder().s("value").build()
  )
  val item1UpdateReq: Map[String, AttributeValue] = Map(
    "field"  -> AttributeValue.builder().n("1").build(),
    "field1" -> AttributeValue.builder().s("value1").build(),
    "field2" -> AttributeValue.builder().bool(true).build()
  )
  val item2: Map[String, AttributeValue]          = Map(
    "key"   -> AttributeValue.builder().s("my-key-2").build(),
    "field" -> AttributeValue.builder().s("value-2").build()
  )

  val key1WithSK: Map[String, AttributeValue] = Map(
    "key"     -> AttributeValue.builder().s("my-key").build(),
    "sortKey" -> AttributeValue.builder().s("my-sort-key").build()
  )

  test("findById return Right(None)") {
    (amazonDynamoDB
      .getItem(_: GetItemRequest))
      .expects(
        GetItemRequest
          .builder()
          .tableName("table")
          .key(key1.asJava)
          .build()
      )
      .returns(GetItemResponse.builder().item(null).build())

    assert(repository.findById(Item(key1)) == Right(None))
  }

  test("findById return Right(Some)") {
    (amazonDynamoDB
      .getItem(_: GetItemRequest))
      .expects(
        GetItemRequest
          .builder()
          .tableName("table")
          .key(key1.asJava)
          .build()
      )
      .returns(
        GetItemResponse
          .builder()
          .item(
            Map(
              "key"   -> AttributeValue.builder().s("my-key").build(),
              "field" -> AttributeValue.builder().s("value").build()
            ).asJava
          )
          .build()
      )

    val expected = Right(Some(Item(item1)))
    assert(repository.findById(Item(key1)) == expected)
  }

  test("findById return Left") {
    (amazonDynamoDB
      .getItem(_: GetItemRequest))
      .expects(
        GetItemRequest
          .builder()
          .tableName("table")
          .key(key1.asJava)
          .build()
      )
      .throws(SdkClientException.create("client ex"))

    assertFindEntityByIdErr(
      repository.findById(Item(key1)),
      Item(key1),
      "client ex"
    )
  }

  test("findAll return Right without filter") {
    (amazonDynamoDB
      .scan(_: ScanRequest))
      .expects(
        ScanRequest
          .builder()
          .tableName("table")
          .build()
      )
      .returns(ScanResponse.builder().items(Seq(item1.asJava, item2.asJava).asJavaCollection).build())

    val expected = Right(Seq(Item(item1), Item(item2)))
    assert(repository.findAll(None) == expected)
  }

  test("findAll return Right with pagination without filter") {
    inSequence(
      (amazonDynamoDB
        .scan(_: ScanRequest))
        .expects(
          ScanRequest
            .builder()
            .tableName("table")
            .build()
        )
        .returns(
          ScanResponse
            .builder()
            .items(Seq(item1.asJava).asJavaCollection)
            .lastEvaluatedKey(key1.asJava)
            .build()
        ),
      (amazonDynamoDB
        .scan(_: ScanRequest))
        .expects(
          ScanRequest
            .builder()
            .tableName("table")
            .exclusiveStartKey(key1.asJava)
            .build()
        )
        .returns(
          ScanResponse
            .builder()
            .items(Seq(item2.asJava).asJavaCollection)
            .build()
        )
    )

    val expected = Right(Seq(Item(item1), Item(item2)))
    assert(repository.findAll(None) == expected)
  }

  test("findAll return Right with filter") {
    (amazonDynamoDB
      .scan(_: ScanRequest))
      .expects(
        ScanRequest
          .builder()
          .tableName("table")
          .filterExpression("key = :v")
          .expressionAttributeValues(Map(":v" -> AttributeValue.builder().s("my-key").build()).asJava)
          .build()
      )
      .returns(ScanResponse.builder().items(Seq(item1.asJava).asJavaCollection).build())

    val expected =
      Right(Seq(Item(item1)))
    assert(
      repository.findAll(
        Some(ScanFilter("key = :v", Map(":v" -> AttributeValue.builder().s("my-key").build())))
      ) == expected
    )
  }

  test("findAll return Left without filter") {
    (amazonDynamoDB
      .scan(_: ScanRequest))
      .expects(ScanRequest.builder().tableName("table").build())
      .throws(new IllegalArgumentException("x"))
    assertFindAllEntitiesErr(repository.findAll(None), None, "x")
  }

  test("findAll return Left with filter") {
    (amazonDynamoDB
      .scan(_: ScanRequest))
      .expects(
        ScanRequest
          .builder()
          .tableName("table")
          .filterExpression("key = :v")
          .expressionAttributeValues(Map(":v" -> AttributeValue.builder().s("my-key").build()).asJava)
          .build()
      )
      .throws(new IllegalArgumentException("x"))

    val actual =
      repository.findAll(Some(ScanFilter("key = :v", Map(":v" -> AttributeValue.builder().s("my-key").build()))))

    assertFindAllEntitiesErr(
      actual,
      Some(ScanFilter("key = :v", Map(":v" -> AttributeValue.builder().s("my-key").build()))),
      "x"
    )
  }

  test("create return Right") {
    (amazonDynamoDB
      .putItem(_: PutItemRequest))
      .expects(
        PutItemRequest
          .builder()
          .tableName("table")
          .item(item1.asJava)
          .conditionExpression(s"attribute_not_exists(key)")
          .build()
      )
      .returns(PutItemResponse.builder().build())

    assert(repository.create(Item(item1)) == Right())
  }

  test("create return Left(EntityAlreadyExists)") {
    (amazonDynamoDB
      .putItem(_: PutItemRequest))
      .expects(
        PutItemRequest
          .builder()
          .tableName("table")
          .item(item1.asJava)
          .conditionExpression(s"attribute_not_exists(key)")
          .build()
      )
      .throws(ConditionalCheckFailedException.builder().message("x").build())

    assert(repository.create(Item(item1)) == Left(EntityAlreadyExistsErr(Item(item1))))
  }

  test("create return Left(CreateEntityErr)") {
    (amazonDynamoDB
      .putItem(_: PutItemRequest))
      .expects(
        PutItemRequest
          .builder()
          .tableName("table")
          .item(item1.asJava)
          .conditionExpression(s"attribute_not_exists(key)")
          .build()
      )
      .throws(SdkClientException.create("x"))

    assertCreateEntityFailureErr(repository.create(Item(item1)), Item(item1), "x")
  }

  test("delete return Right") {
    (amazonDynamoDB
      .deleteItem(_: DeleteItemRequest))
      .expects(
        DeleteItemRequest
          .builder()
          .tableName("table")
          .key(key1.asJava)
          .build()
      )
      .returns(DeleteItemResponse.builder().build())

    assert(repository.delete(Item(key1)) == Right())
  }

  test("delete return Left") {
    (amazonDynamoDB
      .deleteItem(_: DeleteItemRequest))
      .expects(
        DeleteItemRequest
          .builder()
          .tableName("table")
          .key(key1.asJava)
          .build()
      )
      .throws(SdkClientException.create("x"))

    val actual = repository.delete(Item(key1))
    assertDeleteEntityErr(actual, Item(key1), "x")
  }

  test("update return Right") {
    (amazonDynamoDB
      .updateItem(_: UpdateItemRequest))
      .expects(
        UpdateItemRequest
          .builder()
          .tableName("table")
          .key(key1.asJava)
          .updateExpression("SET field = :field,field1 = :field1,field2 = :field2")
          .expressionAttributeValues(
            Map(
              ":field"  -> AttributeValue.builder().n("1").build(),
              ":field1" -> AttributeValue.builder().s("value1").build(),
              ":field2" -> AttributeValue.builder().bool(true).build()
            ).asJava
          )
          .conditionExpression("attribute_exists(key)")
          .build()
      )
      .returns(UpdateItemResponse.builder().build())

    val item = key1 ++ item1UpdateReq
    assert(repository.update(Item(item)) == Right())
  }

  test("update return Right with sort key") {
    (amazonDynamoDB
      .updateItem(_: UpdateItemRequest))
      .expects(
        UpdateItemRequest
          .builder()
          .tableName("table")
          .key(key1WithSK.asJava)
          .updateExpression("SET field = :field,field1 = :field1,field2 = :field2")
          .expressionAttributeValues(
            Map(
              ":field"  -> AttributeValue.builder().n("1").build(),
              ":field1" -> AttributeValue.builder().s("value1").build(),
              ":field2" -> AttributeValue.builder().bool(true).build()
            ).asJava
          )
          .conditionExpression("attribute_exists(key) AND attribute_exists(sortKey)")
          .build()
      )
      .returns(UpdateItemResponse.builder().build())

    val item   = key1WithSK ++ item1UpdateReq
    val actual = repositoryWithSortKey.update(Item(item))
    assert(actual == Right())
  }

  test("update return Left(EntityDoesNotExists)") {
    (amazonDynamoDB
      .updateItem(_: UpdateItemRequest))
      .expects(
        UpdateItemRequest
          .builder()
          .tableName("table")
          .key(key1.asJava)
          .updateExpression("SET field = :field,field1 = :field1,field2 = :field2")
          .expressionAttributeValues(
            Map(
              ":field"  -> AttributeValue.builder().n("1").build(),
              ":field1" -> AttributeValue.builder().s("value1").build(),
              ":field2" -> AttributeValue.builder().bool(true).build()
            ).asJava
          )
          .conditionExpression("attribute_exists(key)")
          .build()
      )
      .throws(ConditionalCheckFailedException.builder().message("x").build())

    val item = key1 ++ item1UpdateReq
    assertEntityDoesNotExists(repository.update(Item(item)), Item(item))
  }

  test("update return Left(UpdateEntityErr") {
    (amazonDynamoDB
      .updateItem(_: UpdateItemRequest))
      .expects(
        UpdateItemRequest
          .builder()
          .tableName("table")
          .key(key1.asJava)
          .updateExpression("SET field = :field,field1 = :field1,field2 = :field2")
          .expressionAttributeValues(
            Map(
              ":field"  -> AttributeValue.builder().n("1").build(),
              ":field1" -> AttributeValue.builder().s("value1").build(),
              ":field2" -> AttributeValue.builder().bool(true).build()
            ).asJava
          )
          .conditionExpression("attribute_exists(key)")
          .build()
      )
      .throws(SdkClientException.create("x"))

    val item = key1 ++ item1UpdateReq
    assertUpdateEntityFailureErr(repository.update(Item(item)), Item(item), "x")
  }

}
