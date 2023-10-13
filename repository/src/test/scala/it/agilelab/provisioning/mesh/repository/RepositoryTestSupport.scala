package it.agilelab.provisioning.mesh.repository

import it.agilelab.provisioning.mesh.repository.RepositoryError.{
  CreateEntityFailureErr,
  DeleteEntityErr,
  EntityDoesNotExists,
  FindAllEntitiesErr,
  FindEntityByIdErr,
  UpdateEntityFailureErr
}
import org.scalatest.EitherValues._

trait RepositoryTestSupport {
  def assertFindEntityByIdErr[A, B](actual: Either[RepositoryError, B], id: A, error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[FindEntityByIdErr[A]])
    assert(actual.left.value.asInstanceOf[FindEntityByIdErr[A]].id == id)
    assert(actual.left.value.asInstanceOf[FindEntityByIdErr[A]].error.getMessage == error)
  }

  def assertFindAllEntitiesErr[A, B](actual: Either[RepositoryError, B], filter: A, error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[FindAllEntitiesErr[A]])
    assert(actual.left.value.asInstanceOf[FindAllEntitiesErr[A]].filter == filter)
    assert(actual.left.value.asInstanceOf[FindAllEntitiesErr[A]].error.getMessage == error)
  }

  def assertCreateEntityFailureErr[A, B](actual: Either[RepositoryError, B], entity: A, error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[CreateEntityFailureErr[A]])
    assert(actual.left.value.asInstanceOf[CreateEntityFailureErr[A]].entity == entity)
    assert(actual.left.value.asInstanceOf[CreateEntityFailureErr[A]].error.getMessage == error)
  }

  def assertDeleteEntityErr[A, B](actual: Either[RepositoryError, B], id: A, error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[DeleteEntityErr[A]])
    assert(actual.left.value.asInstanceOf[DeleteEntityErr[A]].id == id)
    assert(actual.left.value.asInstanceOf[DeleteEntityErr[A]].error.getMessage == error)
  }

  def assertUpdateEntityFailureErr[A, B](actual: Either[RepositoryError, B], entity: A, error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[UpdateEntityFailureErr[A]])
    assert(actual.left.value.asInstanceOf[UpdateEntityFailureErr[A]].entity == entity)
    assert(actual.left.value.asInstanceOf[UpdateEntityFailureErr[A]].error.getMessage == error)
  }

  def assertEntityDoesNotExists[A, B](actual: Either[RepositoryError, B], entity: A): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[EntityDoesNotExists[A]])
    assert(actual.left.value.asInstanceOf[EntityDoesNotExists[A]].entity == entity)
  }

}
