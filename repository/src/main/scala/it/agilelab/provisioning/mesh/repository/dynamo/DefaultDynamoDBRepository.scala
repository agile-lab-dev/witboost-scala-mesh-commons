package it.agilelab.provisioning.mesh.repository.dynamo

import it.agilelab.provisioning.mesh.repository.RepositoryError
import it.agilelab.provisioning.mesh.repository.RepositoryError._
import it.agilelab.provisioning.mesh.repository.dynamo.model.{ Item, ScanFilter }
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.{ AttributeValue, ConditionalCheckFailedException }

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

class DefaultDynamoDBRepository(
  val dynamoDbClient: DynamoDbClient,
  override val tableName: String,
  override val partitionKey: String,
  override val sortKey: Option[String]
) extends DynamoDBRepository {

  override def findById(id: Item): Either[RepositoryError, Option[Item]] =
    try {
      val response = dynamoDbClient.getItem(getItemRequest(id.values))
      if (response.hasItem) Right(Some(Item(response.item().asScala.toMap))) else Right(None)
    } catch { case t: Throwable => Left(FindEntityByIdErr(id, t)) }

  override def findAll(
    filter: Option[ScanFilter]
  ): Either[RepositoryError, Seq[Item]]                                  =
    try Right(recursiveScan(filter, None, Seq.empty[Item]))
    catch { case t: Throwable => Left(FindAllEntitiesErr(filter, t)) }

  override def create(entity: Item): Either[RepositoryError, Unit]       =
    try {
      dynamoDbClient.putItem(getConditionalPutItemRequest(entity.values))
      Right()
    } catch {
      case _: ConditionalCheckFailedException => Left(EntityAlreadyExistsErr(entity))
      case t: Throwable                       => Left(CreateEntityFailureErr(entity, t))
    }

  override def delete(id: Item): Either[RepositoryError, Unit]     =
    try {
      dynamoDbClient.deleteItem(getDeleteItemRequest(id.values))
      Right()
    } catch { case t: Throwable => Left(DeleteEntityErr(id, t)) }

  override def update(entity: Item): Either[RepositoryError, Unit] =
    try {
      dynamoDbClient.updateItem(getConditionalUpdateItemRequest(entity.values))
      Right()
    } catch {
      case _: ConditionalCheckFailedException => Left(EntityDoesNotExists(entity))
      case t: Throwable                       => Left(UpdateEntityFailureErr(entity, t))
    }

  @tailrec
  private def recursiveScan(
    filter: Option[ScanFilter],
    lastEvaluatedKey: Option[java.util.Map[String, AttributeValue]],
    items: Seq[Item]
  ): Seq[Item] = {
    val request  = getScanRequest(filter, lastEvaluatedKey)
    val response = dynamoDbClient.scan(request)
    val newItems = response.items().asScala.toSeq.map(e => Item(e.asScala.toMap))

    if (!response.hasLastEvaluatedKey) items ++ newItems
    else recursiveScan(filter, Some(response.lastEvaluatedKey()), items ++ newItems)
  }

}
