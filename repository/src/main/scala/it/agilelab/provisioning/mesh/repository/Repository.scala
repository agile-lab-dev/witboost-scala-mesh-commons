package it.agilelab.provisioning.mesh.repository

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.mesh.repository.RepositoryError.RepositoryInitErr
import it.agilelab.provisioning.mesh.repository.dynamo.{
  DefaultDynamoDBRepository,
  DefaultDynamoDBRepositoryWithAudit,
  DynamoDBRepository
}
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

/** Repository trait
  *
  * @tparam ENTITY: entity
  * @tparam ID: entity id
  * @tparam FILTER: filter type
  */
trait Repository[ENTITY, ID, FILTER] {

  /** Find a an instance of type A given an id of type B
    * @param id the DataEntity Id
    * @return Right(Some(A)) if successful find an entity given the provided id
    *         Right(None) if entity with provided id does not exists
    *         Left(error) otherwise
    */
  def findById(id: ID): Either[RepositoryError, Option[ENTITY]]

  /** Find all instance of type A
    *
    * @return Right(Seq(A)) if successful list all entity
    *         Left(Error) otherwise
    */
  def findAll(filter: Option[FILTER]): Either[RepositoryError, Seq[ENTITY]]

  /** Create an entity of type A on the storage layer
    * @param entity the DataEntity to create
    * @return Right() if entity are created
    *         Left(Error) otherwise
    */
  def create(entity: ENTITY): Either[RepositoryError, Unit]

  /** Delete an entity with id B on the storage layer
    * @param id the DataEntity Id
    * @return Right() if entity are deleted
    *         Left(Error) otherwise
    */
  def delete(id: ID): Either[RepositoryError, Unit]

  /** Update an entity of type A on the storage layer
    * @param entity the DataEntity
    * @return Right() if entity are updated
    *          Left(Error) otherwise
    */
  def update(entity: ENTITY): Either[RepositoryError, Unit]

}

/** Repository companion object
  */
object Repository {

  /** Create a dynamoDB repository
    * @param table: table string
    * @param primaryKey: primaryKey string
    * @param sortKey: Optional sortKey
    * @return Right(Repository)
    *         Left(RepositoryError)
    */
  def dynamoDB(
    table: String,
    primaryKey: String,
    sortKey: Option[String]
  ): Either[RepositoryError, DynamoDBRepository] =
    try {
      val dynamoDB = DynamoDbClient.builder().build()
      Right(new DefaultDynamoDBRepository(dynamoDB, table, primaryKey, sortKey))
    } catch { case t: Throwable => Left(RepositoryInitErr(t)) }

  /** Create a dynamoDB Repository with Audit enabled
    * @param table: table string
    * @param primaryKey: primaryKey string
    * @param sortKey: Optional sortKey
    * @return Right(Repository)
    *         Left(RepositoryError)
    */
  def dynamoDBWithAudit(
    table: String,
    primaryKey: String,
    sortKey: Option[String]
  ): Either[RepositoryError, DynamoDBRepository] =
    dynamoDB(table, primaryKey, sortKey).map(
      new DefaultDynamoDBRepositoryWithAudit(_, Audit.default("DynamoDBRepository"))
    )

}
