package it.agilelab.provisioning.mesh.self.service.core.provisioner

import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.repository.RepositoryError.{
  CreateEntityFailureErr,
  FindEntityByIdErr,
  UpdateEntityFailureErr
}
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.{ FAILED, RUNNING }
import it.agilelab.provisioning.mesh.self.service.api.model.Component.Workload
import it.agilelab.provisioning.mesh.self.service.api.model.{ DataProduct, ProvisionRequest }
import it.agilelab.provisioning.mesh.self.service.core.gateway.{ ComponentGateway, ComponentGatewayError }
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import io.circe.Json
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultAsyncProvisionerTest extends AnyFunSuite with MockFactory {

  val componentGateway: ComponentGateway[String, String, String]           =
    mock[ComponentGateway[String, String, String]]
  val provisioningStatusRepo: Repository[ProvisioningStatus, String, Unit] =
    mock[Repository[ProvisioningStatus, String, Unit]]

  val provisioner = new DefaultAsyncProvisioner[String, String, String](provisioningStatusRepo, componentGateway)

  val request: ProvisionRequest[String, String] = ProvisionRequest(
    DataProduct[String](
      id = "my-dp-id",
      name = "my-dp-name",
      domain = "my-dp-domain",
      environment = "my-dp-environment",
      version = "my-dp-version",
      dataProductOwner = "my-dp-owner",
      specific = "my-dp-specific",
      components = Seq.empty[Json]
    ),
    Some(
      Workload[String](
        id = "my-dp-workload-id",
        name = "my-dp-workload-name",
        version = "my-dp-workload-version",
        description = "my-dp-description",
        specific = "x"
      )
    )
  )

  test("provision return Right(Provision(id,RUNNING,None))") {
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Right(None))
    (provisioningStatusRepo.create _)
      .expects(ProvisioningStatus("my-id", RUNNING, None))
      .once()
      .returns(Right(None))
    (componentGateway.create _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Right("x"))

    val actual   = provisioner.provision(ProvisionCommand("my-id", request))
    val expected = Right(ProvisioningStatus("my-id", RUNNING, None))
    assert(actual == expected)
  }

  test("provision return Left(ProvisionErr) on repo error") {
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Left(FindEntityByIdErr("my-id", new IllegalArgumentException())))
    (provisioningStatusRepo.create _)
      .expects(ProvisioningStatus("my-id", RUNNING, None))
      .once()
      .returns(Left(CreateEntityFailureErr(ProvisioningStatus("my-id", RUNNING, None), new IllegalArgumentException())))

    val actual   = provisioner.provision(ProvisionCommand("my-id", request))
    val expected = Left(ProvisionerError("Unable to create provisioning status with provided repository"))
    assert(actual == expected)
  }

  test("provision return Left(ProvisionErr) on repo create error") {
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Right(None))
    (provisioningStatusRepo.create _)
      .expects(ProvisioningStatus("my-id", RUNNING, None))
      .once()
      .returns(Left(CreateEntityFailureErr(ProvisioningStatus("my-id", RUNNING, None), new IllegalArgumentException())))

    val actual   = provisioner.provision(ProvisionCommand("my-id", request))
    val expected = Left(ProvisionerError("Unable to create provisioning status with provided repository"))
    assert(actual == expected)
  }

  test("provision return Left(ProvisionErr) on repo update error") {
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (provisioningStatusRepo.update _)
      .expects(ProvisioningStatus("my-id", RUNNING, None))
      .once()
      .returns(Left(UpdateEntityFailureErr(ProvisioningStatus("my-id", RUNNING, None), new IllegalArgumentException())))

    val actual   = provisioner.provision(ProvisionCommand("my-id", request))
    val expected = Left(ProvisionerError("Unable to update provisioning status with provided repository"))
    assert(actual == expected)
  }

  test("provision return Left(ProvisionErr) on component create error") {
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Right(None))
    (provisioningStatusRepo.create _)
      .expects(ProvisioningStatus("my-id", RUNNING, None))
      .once()
      .returns(Right(None))
    (componentGateway.create _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Left(ComponentGatewayError("x")))
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (provisioningStatusRepo.update _)
      .expects(
        ProvisioningStatus(
          "my-id",
          FAILED,
          Some("Unable to execute component gateway for provided request. Details: x")
        )
      )
      .once()
      .returns(Right())

    val actual   = provisioner.provision(ProvisionCommand("my-id", request))
    val expected = Left(ProvisionerError("Unable to execute component gateway for provided request. Details: x"))
    assert(actual == expected)
  }

  test("unprovision return Right(Provision(id,RUNNING,None))") {
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Right(None))
    (provisioningStatusRepo.create _)
      .expects(ProvisioningStatus("my-id", RUNNING, None))
      .once()
      .returns(Right(None))
    (componentGateway.destroy _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Right("x"))

    val actual   = provisioner.unprovision(ProvisionCommand("my-id", request))
    val expected = Right(ProvisioningStatus("my-id", RUNNING, None))
    assert(actual == expected)
  }

  test("unprovision return Left(ProvisionErr) on repo error") {
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Left(FindEntityByIdErr("my-id", new IllegalArgumentException())))
    (provisioningStatusRepo.create _)
      .expects(ProvisioningStatus("my-id", RUNNING, None))
      .once()
      .returns(Left(CreateEntityFailureErr(ProvisioningStatus("my-id", RUNNING, None), new IllegalArgumentException())))

    val actual   = provisioner.unprovision(ProvisionCommand("my-id", request))
    val expected = Left(ProvisionerError("Unable to create provisioning status with provided repository"))
    assert(actual == expected)
  }

  test("unprovision return Left(ProvisionErr) on repo create error") {
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Right(None))
    (provisioningStatusRepo.create _)
      .expects(ProvisioningStatus("my-id", RUNNING, None))
      .once()
      .returns(Left(CreateEntityFailureErr(ProvisioningStatus("my-id", RUNNING, None), new IllegalArgumentException())))

    val actual   = provisioner.unprovision(ProvisionCommand("my-id", request))
    val expected = Left(ProvisionerError("Unable to create provisioning status with provided repository"))
    assert(actual == expected)
  }

  test("unprovision return Left(ProvisionErr) on repo update error") {
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (provisioningStatusRepo.update _)
      .expects(ProvisioningStatus("my-id", RUNNING, None))
      .once()
      .returns(Left(UpdateEntityFailureErr(ProvisioningStatus("my-id", RUNNING, None), new IllegalArgumentException())))

    val actual   = provisioner.unprovision(ProvisionCommand("my-id", request))
    val expected = Left(ProvisionerError("Unable to update provisioning status with provided repository"))
    assert(actual == expected)
  }

  test("unprovision return Left(ProvisionErr) on component create error") {
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Right(None))
    (provisioningStatusRepo.create _)
      .expects(ProvisioningStatus("my-id", RUNNING, None))
      .once()
      .returns(Right(None))
    (componentGateway.destroy _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Left(ComponentGatewayError("x")))
    (provisioningStatusRepo.findById _)
      .expects("my-id")
      .once()
      .returns(Right(Some(ProvisioningStatus("my-id", RUNNING, None))))
    (provisioningStatusRepo.update _)
      .expects(
        ProvisioningStatus(
          "my-id",
          FAILED,
          Some("Unable to execute component gateway for provided request. Details: x")
        )
      )
      .once()
      .returns(Right())

    val actual   = provisioner.unprovision(ProvisionCommand("my-id", request))
    val expected = Left(ProvisionerError("Unable to execute component gateway for provided request. Details: x"))
    assert(actual == expected)
  }
}
