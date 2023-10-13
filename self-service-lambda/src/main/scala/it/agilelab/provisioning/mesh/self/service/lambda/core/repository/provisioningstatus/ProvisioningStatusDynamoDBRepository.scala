package it.agilelab.provisioning.mesh.self.service.lambda.core.repository.provisioningstatus

import cats.implicits.toBifunctorOps
import it.agilelab.provisioning.mesh.repository.RepositoryError._
import it.agilelab.provisioning.mesh.repository.dynamo.DynamoDBRepository
import it.agilelab.provisioning.mesh.repository.dynamo.model.Item
import it.agilelab.provisioning.mesh.repository.{ Repository, RepositoryError }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.{ COMPLETED, FAILED, RUNNING }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ ProvisioningStatus, Status }
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class ProvisioningStatusDynamoDBRepository(repository: DynamoDBRepository)
    extends Repository[ProvisioningStatus, String, Unit] {

  override def findById(
    id: String
  ): Either[RepositoryError, Option[ProvisioningStatus]] =
    repository
      .findById(primaryKey(id))
      .map(_.map(decodeRequestState))
      .leftMap {
        case FindEntityByIdErr(_, error) => FindEntityByIdErr(id, error)
        case e                           => e
      }

  override def findAll(
    filter: Option[Unit]
  ): Either[RepositoryError, Seq[ProvisioningStatus]] =
    repository
      .findAll(None)
      .map(_.map(decodeRequestState))
      .leftMap {
        case FindAllEntitiesErr(_, error) => FindAllEntitiesErr(filter, error)
        case e                            => e
      }

  override def create(
    entity: ProvisioningStatus
  ): Either[RepositoryError, Unit] =
    repository.create(encodeRequestState(entity)).leftMap {
      case CreateEntityFailureErr(_, e) => CreateEntityFailureErr(entity, e)
      case EntityAlreadyExistsErr(_)    => EntityAlreadyExistsErr(entity)
      case e                            => e
    }

  override def delete(id: String): Either[RepositoryError, Unit] =
    repository.delete(primaryKey(id)).leftMap {
      case DeleteEntityErr(_, error) => DeleteEntityErr(id, error)
      case e                         => e
    }

  override def update(
    entity: ProvisioningStatus
  ): Either[RepositoryError, Unit] =
    repository.update(encodeRequestState(entity)).leftMap {
      case UpdateEntityFailureErr(_, e) => UpdateEntityFailureErr(entity, e)
      case EntityDoesNotExists(_)       => EntityDoesNotExists(entity)
      case e                            => e
    }

  private def primaryKey(id: String) =
    Item(
      Map(
        "request_id" -> AttributeValue.builder().s(id).build()
      )
    )

  private def decodeRequestState(item: Item): ProvisioningStatus =
    ProvisioningStatus(
      item.values("request_id").s(),
      item.values("request_status").s() match {
        case "RUNNING"   => RUNNING
        case "COMPLETED" => COMPLETED
        case "FAILED"    => FAILED
        case _           => FAILED
      },
      Option.when(item.values("request_result").s().nonEmpty)(item.values("request_result").s())
    )

  private def encodeRequestState(requestState: ProvisioningStatus): Item =
    Item(
      Map(
        "request_id"     -> AttributeValue.builder().s(requestState.id).build(),
        "request_status" -> AttributeValue
          .builder()
          .s(requestState.status match {
            case Status.RUNNING   => "RUNNING"
            case Status.COMPLETED => "COMPLETED"
            case Status.FAILED    => "FAILED"
          })
          .build(),
        "request_result" -> AttributeValue.builder().s(requestState.result.getOrElse("")).build()
      )
    )
}
