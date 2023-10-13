package it.agilelab.provisioning.mesh.repository.dynamo

import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.repository.dynamo.model.{ Item, ScanFilter }
import software.amazon.awssdk.services.dynamodb.model.{
  AttributeValue,
  DeleteItemRequest,
  GetItemRequest,
  PutItemRequest,
  ScanRequest,
  UpdateItemRequest
}

import scala.jdk.CollectionConverters._

trait DynamoDBRepository extends Repository[Item, Item, ScanFilter] {
  val tableName: String
  val partitionKey: String
  val sortKey: Option[String]

  protected def getConditionalPutItemRequest(a: Map[String, AttributeValue]): PutItemRequest =
    PutItemRequest
      .builder()
      .tableName(tableName)
      .item(a.asJava)
      .conditionExpression(getConditionalPutExpression)
      .build()

  protected def getConditionalUpdateItemRequest(a: Map[String, AttributeValue]): UpdateItemRequest = {
    val updateExpression = updateExpressionFromItem(a)
    UpdateItemRequest
      .builder()
      .tableName(tableName)
      .key(keyFromItem(a).asJava)
      .updateExpression(updateExpression._1)
      .expressionAttributeValues(updateExpression._2.asJava)
      .conditionExpression(getConditionalUpdateExpression)
      .build()
  }

  protected def getDeleteItemRequest(key: Map[String, AttributeValue]): DeleteItemRequest =
    DeleteItemRequest
      .builder()
      .tableName(tableName)
      .key(key.asJava)
      .build()

  protected def getItemRequest(key: Map[String, AttributeValue]): GetItemRequest =
    GetItemRequest
      .builder()
      .tableName(tableName)
      .key(key.asJava)
      .build()

  protected def getScanRequest(
    filter: Option[ScanFilter],
    lastEvaluatedKey: Option[java.util.Map[String, AttributeValue]]
  ): ScanRequest =
    lastEvaluatedKey
      .map(key =>
        getScanRequestBuilder(filter)
          .exclusiveStartKey(key)
          .build()
      )
      .getOrElse(
        getScanRequestBuilder(filter)
          .build()
      )

  private def getScanRequestBuilder(filter: Option[ScanFilter]): ScanRequest.Builder =
    filter
      .map(f =>
        ScanRequest
          .builder()
          .tableName(tableName)
          .filterExpression(f.filterExpression)
          .expressionAttributeValues(f.filterAttributeValues.asJava)
      )
      .getOrElse(
        ScanRequest
          .builder()
          .tableName(tableName)
      )

  private def keyFromItem(a: Map[String, AttributeValue]): Map[String, AttributeValue] =
    sortKey
      .map(sk =>
        Map(
          partitionKey -> a.getOrElse(partitionKey, AttributeValue.builder().build()),
          sk           -> a.getOrElse(sk, AttributeValue.builder().build())
        )
      )
      .getOrElse(Map(partitionKey -> a.getOrElse(partitionKey, AttributeValue.builder().build())))

  private def getConditionalPutExpression: String                                      =
    sortKey
      .map(sk => s"attribute_not_exists($partitionKey) AND attribute_not_exists($sk)")
      .getOrElse(s"attribute_not_exists($partitionKey)")

  private def getConditionalUpdateExpression: String =
    sortKey
      .map(sk => s"attribute_exists($partitionKey) AND attribute_exists($sk)")
      .getOrElse(s"attribute_exists($partitionKey)")

  private def updateExpressionFromItem(a: Map[String, AttributeValue]): (String, Map[String, AttributeValue]) = {
    val fields     = sortKey
      .map(sk => a.filter { case (k, _) => k != partitionKey && k != sk })
      .getOrElse(a.filter { case (k, _) => k != partitionKey })
    val updateExpr = s"SET ${fields.keys.map(k => s"$k = :$k").mkString(",")}"
    val valueMap   = fields.map(t => (s":${t._1}", t._2))
    (updateExpr, valueMap)
  }

}
