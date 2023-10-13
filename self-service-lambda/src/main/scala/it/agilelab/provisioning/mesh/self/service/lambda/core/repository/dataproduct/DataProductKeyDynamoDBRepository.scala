package it.agilelab.provisioning.mesh.self.service.lambda.core.repository.dataproduct

import it.agilelab.provisioning.mesh.repository.{ Repository, RepositoryError }
import it.agilelab.provisioning.mesh.repository.RepositoryError._
import cats.implicits.toBifunctorOps
import it.agilelab.provisioning.mesh.repository.dynamo.DynamoDBRepository
import it.agilelab.provisioning.mesh.repository.dynamo.model.{ Item, ScanFilter }
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.DataProductEntityKey
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

/** A Repository[DataProduct,DataProductKey] based on DynamoDB table
  * @param repository: an [[Repository[Item, PrimaryKey, ScanFilter]]] instance
  */
class DataProductKeyDynamoDBRepository(repository: DynamoDBRepository)
    extends Repository[DataProductEntityKey, DataProductEntityKey, Unit] {

  override def findById(
    id: DataProductEntityKey
  ): Either[RepositoryError, Option[DataProductEntityKey]] =
    repository
      .findById(primaryKey(id))
      .map(_.map(decodeDataProduct))
      .leftMap {
        case FindEntityByIdErr(_, error) => FindEntityByIdErr(id, error)
        case e                           => e
      }

  override def findAll(
    filter: Option[Unit]
  ): Either[RepositoryError, Seq[DataProductEntityKey]] =
    repository
      .findAll(
        Some(
          ScanFilter(s"begins_with(range_key_id, :v)", Map(":v" -> AttributeValue.builder().s("dataproduct").build()))
        )
      )
      .map(_.map(decodeDataProduct))
      .leftMap {
        case FindAllEntitiesErr(_, error) => FindAllEntitiesErr(filter, error)
        case e                            => e
      }

  private def primaryKey(id: DataProductEntityKey) =
    Item(
      Map(
        "key_id"       -> AttributeValue.builder().s(s"domain#${id.domain}").build(),
        "range_key_id" -> AttributeValue.builder().s(s"dataproduct#${id.dataProduct}").build()
      )
    )

  private def decodeDataProduct(item: Item): DataProductEntityKey =
    DataProductEntityKey(
      item.values("key_id").s().split("#")(1),
      item.values("range_key_id").s().split("#")(1)
    )

  private def encodeDataProduct(dp: DataProductEntityKey): Item =
    Item(
      Map(
        "key_id"       -> AttributeValue.builder().s("domain#" + dp.domain).build(),
        "range_key_id" -> AttributeValue.builder().s("dataproduct#" + dp.dataProduct).build()
      )
    )

  override def create(entity: DataProductEntityKey): Either[RepositoryError, Unit] =
    repository.create(encodeDataProduct(entity)).leftMap {
      case CreateEntityFailureErr(_, e) => CreateEntityFailureErr(entity, e)
      case EntityAlreadyExistsErr(_)    => EntityAlreadyExistsErr(entity)
      case e                            => e
    }

  override def delete(id: DataProductEntityKey): Either[RepositoryError, Unit] =
    repository.delete(primaryKey(id)).leftMap {
      case DeleteEntityErr(_, error) => DeleteEntityErr(id, error)
      case e                         => e
    }

  override def update(entity: DataProductEntityKey): Either[RepositoryError, Unit] =
    repository.update(encodeDataProduct(entity)).leftMap {
      case UpdateEntityFailureErr(_, e) => UpdateEntityFailureErr(entity, e)
      case EntityDoesNotExists(_)       => EntityDoesNotExists(entity)
      case e                            => e
    }

}
