package it.agilelab.provisioning.mesh.self.service.lambda.core.repository.role

import it.agilelab.provisioning.mesh.repository.{ Repository, RepositoryError }
import it.agilelab.provisioning.mesh.repository.RepositoryError._
import cats.implicits._
import it.agilelab.provisioning.mesh.repository.dynamo.DynamoDBRepository
import it.agilelab.provisioning.mesh.repository.dynamo.model.Item
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.Role
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

/** A Repository[Role,String] based on DynamoDB table
  *
  * @param repository: an [[Repository[Item, PrimaryKey, ScanFilter]]] instance
  */
class RoleDynamoDBRepository(repository: DynamoDBRepository) extends Repository[Role, String, Unit] {

  override def findById(id: String): Either[RepositoryError, Option[Role]] =
    repository
      .findById(primaryKey(id))
      .map(_.map(decodeRole))
      .leftMap {
        case FindEntityByIdErr(_, error) => FindEntityByIdErr(id, error)
        case e                           => e
      }

  override def findAll(filter: Option[Unit]): Either[RepositoryError, Seq[Role]] =
    repository
      .findAll(None)
      .map(_.map(decodeRole))
      .leftMap {
        case FindAllEntitiesErr(_, error) => FindAllEntitiesErr(filter, error)
        case e                            => e
      }

  private def primaryKey(id: String) =
    Item(
      Map(
        "key_id" -> AttributeValue.builder().s(s"role#$id").build()
      )
    )

  private def decodeRole(item: Item): Role =
    Role(
      name = item.values("key_id").s().split("#")(1),
      domain = item.values("domain").s(),
      iamRole = item.values("iam_role").s(),
      iamRoleArn = item.values("iam_role_arn").s(),
      cdpRole = item.values("ad_role").s(),
      cdpRoleCrn = item.values("cdp_role_crn").s()
    )

  override def create(entity: Role): Either[RepositoryError, Unit] =
    Left(CreateEntityFailureErr(entity, new NotImplementedError()))

  override def delete(id: String): Either[RepositoryError, Unit] =
    Left(DeleteEntityErr(id, new NotImplementedError()))

  override def update(entity: Role): Either[RepositoryError, Unit] =
    Left(UpdateEntityFailureErr(entity, new NotImplementedError()))
}
