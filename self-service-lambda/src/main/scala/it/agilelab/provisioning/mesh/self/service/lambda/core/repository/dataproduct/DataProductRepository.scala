package it.agilelab.provisioning.mesh.self.service.lambda.core.repository.dataproduct

import cats.implicits.{ toBifunctorOps, toTraverseOps }
import it.agilelab.provisioning.mesh.repository.RepositoryError._
import it.agilelab.provisioning.mesh.repository.{ Repository, RepositoryError }
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.{ DataProductEntity, DataProductEntityKey, Domain }

class DataProductRepository(
  dataProductKeyRepo: Repository[DataProductEntityKey, DataProductEntityKey, Unit],
  domainRepo: Repository[Domain, String, Unit]
) extends Repository[DataProductEntity, DataProductEntityKey, Unit] {

  override def findById(
    id: DataProductEntityKey
  ): Either[RepositoryError, Option[DataProductEntity]] =
    for {
      optKey    <- dataProductKeyRepo.findById(id).leftMap {
                     case FindEntityByIdErr(_, error) => FindEntityByIdErr(id, error)
                     case e                           => e
                   }
      optDomain <-
        optKey
          .flatMap(k =>
            domainRepo
              .findById(k.domain)
              .leftMap {
                case FindEntityByIdErr(_, error) => FindEntityByIdErr(id, error)
                case e                           => e
              }
              .sequence
          )
          .sequence
    } yield optKey.flatMap(k => optDomain.map(d => DataProductEntity(d.name, k.dataProduct)))

  override def findAll(
    filter: Option[Unit]
  ): Either[RepositoryError, Seq[DataProductEntity]] =
    for {
      keys        <- dataProductKeyRepo.findAll(None)
      domains     <- domainRepo.findAll(None)
      dataProducts = keys.zip(domains).collect {
                       case (dpKey, dm) if dpKey.domain == dm.name =>
                         DataProductEntity(dm.name, dpKey.dataProduct)
                     }
    } yield dataProducts

  override def create(entity: DataProductEntity): Either[RepositoryError, Unit] =
    Left(CreateEntityFailureErr(entity, new NotImplementedError()))

  override def delete(id: DataProductEntityKey): Either[RepositoryError, Unit] =
    Left(DeleteEntityErr(id, new NotImplementedError()))

  override def update(entity: DataProductEntity): Either[RepositoryError, Unit] =
    Left(UpdateEntityFailureErr(entity, new NotImplementedError()))

}
