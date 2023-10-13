package it.agilelab.provisioning.mesh.self.service.api.handler.state

import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.repository.RepositoryError.{
  EntityDoesNotExists,
  FindEntityByIdErr,
  RepositoryInitErr
}
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ SystemError, ValidationError }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.RUNNING
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultProvisionStateHandlerTest extends AnyFunSuite with MockFactory {

  val repository: Repository[ProvisioningStatus, String, Unit] = mock[Repository[ProvisioningStatus, String, Unit]]
  val provisionStateHandler                                    = new DefaultProvisionStateHandler(repository)

  test("get return Right(state)") {
    (repository.findById _)
      .expects("x")
      .once()
      .returns(Right(Some(ProvisioningStatus("x", RUNNING, None))))
    val actual   = provisionStateHandler.get("x")
    val expected = Right(ProvisioningStatus("x", RUNNING, None))
    assert(actual == expected)
  }

  test("get return Left(ValidationError) with StateNotFound") {
    (repository.findById _).expects("x").once().returns(Right(None))
    val actual   = provisionStateHandler.get("x")
    val expected = Left(ValidationError(Seq("Provision state with id: x not found.")))
    assert(actual == expected)
  }

  test("get return Left(ValidationError) with FindByIdError") {
    (repository.findById _).expects("x").once().returns(Left(FindEntityByIdErr("x", new IllegalArgumentException("x"))))
    val actual   = provisionStateHandler.get("x")
    val expected = Left(
      SystemError("Get provision x fail. An exception was raised: java.lang.IllegalArgumentException x")
    )
    assert(actual == expected)
  }

  test("get return Left(ValidationError) with RepositoryError") {
    (repository.findById _)
      .expects("x")
      .once()
      .returns(Left(RepositoryInitErr(new IllegalArgumentException("x"))))
    val actual   = provisionStateHandler.get("x")
    val expected = Left(
      SystemError("Get provision x fail. An exception was raised")
    )
    assert(actual == expected)
  }

}
