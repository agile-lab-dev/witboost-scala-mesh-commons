package it.agilelab.provisioning.mesh.self.service.lambda.core.repository.domain

import it.agilelab.provisioning.mesh.repository.{ Repository, RepositoryError }
import it.agilelab.provisioning.mesh.repository.RepositoryError._
import cats.implicits._
import it.agilelab.provisioning.mesh.repository.dynamo.DynamoDBRepository
import it.agilelab.provisioning.mesh.repository.dynamo.model.{ Item, ScanFilter }
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.Domain
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

/** A Repository[Domain,String] based on DynamoDB table
  * @param repository: an [[Repository[Item, PrimaryKey, ScanFilter]]] instance
  */
class DomainDynamoDBRepository(repository: DynamoDBRepository) extends Repository[Domain, String, Unit] {

  override def findById(id: String): Either[RepositoryError, Option[Domain]] =
    repository
      .findById(primaryKey(id))
      .map(_.map(decodeDomain))
      .leftMap {
        case FindEntityByIdErr(_, error) => FindEntityByIdErr(id, error)
        case e                           => e
      }

  override def findAll(
    filter: Option[Unit]
  ): Either[RepositoryError, Seq[Domain]] =
    repository
      .findAll(Some(ScanFilter(s"range_key_id = :v", Map(":v" -> AttributeValue.builder().s("METADATA").build()))))
      .map(_.map(decodeDomain))
      .leftMap {
        case FindAllEntitiesErr(_, error) => FindAllEntitiesErr(filter, error)
        case e                            => e
      }

  private def primaryKey(id: String) =
    Item(
      Map(
        "key_id"       -> AttributeValue.builder().s(s"domain#$id").build(),
        "range_key_id" -> AttributeValue.builder().s("METADATA").build()
      )
    )

  private def decodeDomain(item: Item): Domain =
    Domain(
      item.values("key_id").s().split("#")(1),
      item.values("domain_short_name").s()
    )

  private def encodeDomain(domain: Domain): Item =
    Item(
      Map(
        "key_id"            -> AttributeValue.builder().s("domain#" + domain.name).build(),
        "range_key_id"      -> AttributeValue.builder().s("METADATA").build(),
        "domain_short_name" -> AttributeValue.builder().s(domain.shortName).build()
      )
    )

  override def create(entity: Domain): Either[RepositoryError, Unit] =
    repository.create(encodeDomain(entity)).leftMap {
      case CreateEntityFailureErr(_, e) => CreateEntityFailureErr(entity, e)
      case EntityAlreadyExistsErr(_)    => EntityAlreadyExistsErr(entity)
      case e                            => e
    }

  override def delete(id: String): Either[RepositoryError, Unit] =
    repository.delete(primaryKey(id)).leftMap {
      case DeleteEntityErr(_, error) => DeleteEntityErr(id, error)
      case e                         => e
    }

  override def update(entity: Domain): Either[RepositoryError, Unit] =
    repository.update(encodeDomain(entity)).leftMap {
      case UpdateEntityFailureErr(_, e) => UpdateEntityFailureErr(entity, e)
      case EntityDoesNotExists(_)       => EntityDoesNotExists(entity)
      case e                            => e
    }

}
