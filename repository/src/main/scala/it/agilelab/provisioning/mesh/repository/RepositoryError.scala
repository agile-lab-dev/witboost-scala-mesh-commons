package it.agilelab.provisioning.mesh.repository

import cats.Show
import cats.implicits._
import io.circe.{ Encoder, Json }
import it.agilelab.provisioning.commons.showable.ShowableOps._

sealed trait RepositoryError extends Exception with Product with Serializable

object RepositoryError {

  final case class RepositoryInitErr(error: Throwable)                    extends RepositoryError
  final case class CreateEntityFailureErr[A](entity: A, error: Throwable) extends RepositoryError
  final case class EntityAlreadyExistsErr[A](entity: A)                   extends RepositoryError
  final case class UpdateEntityFailureErr[A](entity: A, error: Throwable) extends RepositoryError
  final case class EntityDoesNotExists[A](entity: A)                      extends RepositoryError
  final case class FindEntityByIdErr[A](id: A, error: Throwable)          extends RepositoryError
  final case class FindAllEntitiesErr[A](filter: A, error: Throwable)     extends RepositoryError
  final case class DeleteEntityErr[A](id: A, error: Throwable)            extends RepositoryError

  implicit val showRepositoryError: Show[RepositoryError] = Show.show {
    case RepositoryInitErr(error)              => show"RepositoryInitErr($error)"
    case CreateEntityFailureErr(entity, error) => show"CreateEntityFailureErr(${entity.toString},$error)"
    case EntityAlreadyExistsErr(entity)        => show"EntityAlreadyExistsErr(${entity.toString})"
    case UpdateEntityFailureErr(entity, error) => show"UpdateEntityFailureErr(${entity.toString},$error)"
    case EntityDoesNotExists(entity)           => show"EntityDoesNotExists(${entity.toString})"
    case FindEntityByIdErr(id, error)          => show"FindEntityByIdErr(${id.toString},$error)"
    case FindAllEntitiesErr(filter, error)     => show"FindAllEntitiesErr(${filter.toString},$error)"
    case DeleteEntityErr(id, error)            => show"DeleteEntityErr(${id.toString},$error)"
  }

}
