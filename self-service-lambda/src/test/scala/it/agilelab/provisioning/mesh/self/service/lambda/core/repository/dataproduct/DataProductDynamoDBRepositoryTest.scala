package it.agilelab.provisioning.mesh.self.service.lambda.core.repository.dataproduct

import it.agilelab.provisioning.mesh.repository.RepositoryError.{
  CreateEntityFailureErr,
  DeleteEntityErr,
  UpdateEntityFailureErr
}
import it.agilelab.provisioning.mesh.self.service.api.model.DataProduct
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.{ DataProductEntity, DataProductEntityKey, Domain }
import it.agilelab.provisioning.mesh.self.service.lambda.core.repository.domain.DomainDynamoDBRepository
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite

class DataProductDynamoDBRepositoryTest extends AnyFunSuite with MockFactory with EitherValues {

  val dataProductKeyRepo: DataProductKeyDynamoDBRepository = stub[DataProductKeyDynamoDBRepository]
  val domainRepo: DomainDynamoDBRepository                 = stub[DomainDynamoDBRepository]
  val repository: DataProductRepository                    =
    new DataProductRepository(dataProductKeyRepo, domainRepo)

  test("findById return Right(None) with dataProductKey not exists") {
    (dataProductKeyRepo.findById _)
      .when(DataProductEntityKey("domainName", "dataProductName"))
      .returns(Right(None))
    val actual = repository.findById(DataProductEntityKey("domainName", "dataProductName"))
    assert(actual == Right(None))
  }

  test("findById return Right(None) with domain not exists") {
    (dataProductKeyRepo.findById _)
      .when(DataProductEntityKey("domainName", "dataProductName"))
      .returns(Right(Some(DataProductEntityKey("domainName", "dataProductName"))))
    (domainRepo.findById _)
      .when("domainName")
      .returns(Right(None))
    val actual = repository.findById(DataProductEntityKey("domainName", "dataProductName"))
    assert(actual == Right(None))
  }

  test("findById return Right(DataProduct) with dataProductKey and domain exists") {
    (dataProductKeyRepo.findById _)
      .when(DataProductEntityKey("domainName", "dataProductName"))
      .returns(Right(Some(DataProductEntityKey("domainName", "dataProductName"))))
    (domainRepo.findById _)
      .when("domainName")
      .returns(Right(Some(Domain("domainName", "domainShortName"))))

    val actual   = repository.findById(DataProductEntityKey("domainName", "dataProductName"))
    val expected = Right(Some(DataProductEntity("domainName", "dataProductName")))
    assert(actual == expected)
  }

  test("findAll return Right(Seq.empty) with Key not exists") {
    (dataProductKeyRepo.findAll _)
      .when(None)
      .returns(Right(Seq.empty))
    (domainRepo.findAll _)
      .when(None)
      .returns(Right(Seq(Domain("domain", "domainShortName"))))

    val actual = repository.findAll(None)

    assert(actual == Right(Seq.empty))
  }

  test("findAll return Right(Seq.empty) with Domain not exists") {
    (dataProductKeyRepo.findAll _)
      .when(None)
      .returns(Right(Seq(DataProductEntityKey("dm", "dp"))))
    (domainRepo.findAll _)
      .when(None)
      .returns(Right(Seq.empty))

    val actual = repository.findAll(None)

    assert(actual == Right(Seq.empty))
  }

  test("findAll return Right(Seq.empty) with no match between dp and dm") {
    (dataProductKeyRepo.findAll _)
      .when(None)
      .returns(Right(Seq(DataProductEntityKey("dm", "dp"), DataProductEntityKey("dm2", "dp2"))))
    (domainRepo.findAll _)
      .when(None)
      .returns(Right(Seq(Domain("dm3", "x"), Domain("dm4", "y"))))

    val actual = repository.findAll(None)

    assert(actual == Right(Seq.empty))
  }

  test("findAll return Right(Seq(DataProduct))") {
    (dataProductKeyRepo.findAll _)
      .when(None)
      .returns(Right(Seq(DataProductEntityKey("dm1", "dp"), DataProductEntityKey("dm2", "dp2"))))
    (domainRepo.findAll _)
      .when(None)
      .returns(Right(Seq(Domain("dm1", "x"), Domain("dm2", "y"))))

    val actual   = repository.findAll(None)
    val expected = Right(Seq(DataProductEntity("dm1", "dp"), DataProductEntity("dm2", "dp2")))
    assert(actual == expected)
  }

  test("create return Left() not implemented") {
    assert(repository.create(DataProductEntity("a", "b")).isLeft)
  }

  test("delete return Left() not implemented") {
    assert(repository.delete(DataProductEntityKey("a", "b")).isLeft)
  }

  test("update return Left() not implemented") {
    assert(repository.update(DataProductEntity("a", "b")).isLeft)
  }
}
