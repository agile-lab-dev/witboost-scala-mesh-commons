package it.agilelab.provisioning.mesh.repository.dynamo

import it.agilelab.provisioning.mesh.repository.RepositoryError
import cats.implicits._
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.mesh.repository.dynamo.model.{ Item, ScanFilter }

class DefaultDynamoDBRepositoryWithAudit(
  repository: DynamoDBRepository,
  audit: Audit
) extends DynamoDBRepository {
  override val tableName: String       = repository.tableName
  override val partitionKey: String    = repository.partitionKey
  override val sortKey: Option[String] = repository.sortKey

  private val INFO_MSG = "Executing %s"

  override def findById(id: Item): Either[RepositoryError, Option[Item]] = {
    val action = s"FindById(${id.toString})"
    audit.info(INFO_MSG.format(action))
    val result = repository.findById(id)
    auditWithinResult(result, action)
    result
  }

  override def findAll(
    filter: Option[ScanFilter]
  ): Either[RepositoryError, Seq[Item]] = {
    val action = s"FindAll(${filter.toString})"
    audit.info(INFO_MSG.format(action))
    val result = repository.findAll(filter)
    auditWithinResult(result, action)
    result
  }

  override def create(entity: Item): Either[RepositoryError, Unit] = {
    val action = s"Create(${entity.toString})"
    audit.info(INFO_MSG.format(action))
    val result = repository.create(entity)
    auditWithinResult(result, action)
    result
  }

  override def delete(id: Item): Either[RepositoryError, Unit] = {
    val action = s"Delete(${id.toString})"
    audit.info(INFO_MSG.format(action))
    val result = repository.delete(id)
    auditWithinResult(result, action)
    result
  }

  override def update(entity: Item): Either[RepositoryError, Unit] = {
    val action = s"Update(${entity.toString})"
    audit.info(INFO_MSG.format(action))
    val result = repository.update(entity)
    auditWithinResult(result, action)
    result
  }

  private def auditWithinResult[A](
    result: Either[RepositoryError, A],
    action: String
  ): Unit =
    result match {
      case Right(_) => audit.info(show"$action completed successfully")
      case Left(l)  => audit.error(show"$action failed. Details: $l")
    }
}
