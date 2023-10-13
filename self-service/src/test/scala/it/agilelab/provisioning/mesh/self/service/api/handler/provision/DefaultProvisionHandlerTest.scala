package it.agilelab.provisioning.mesh.self.service.api.handler.provision

import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.SystemError
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.ProvisioningStatus
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.RUNNING
import it.agilelab.provisioning.mesh.self.service.api.model.Component.Workload
import it.agilelab.provisioning.mesh.self.service.api.model.{ DataProduct, ProvisionRequest }
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import it.agilelab.provisioning.mesh.self.service.core.provisioner.{ Provisioner, ProvisionerError }
import io.circe.Json
import it.agilelab.provisioning.commons.identifier.IDGenerator
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultProvisionHandlerTest extends AnyFunSuite with MockFactory {
  val idGenerator: IDGenerator                      = mock[IDGenerator]
  val provisionService: Provisioner[String, String] = mock[Provisioner[String, String]]
  val provisionHandler                              = new DefaultProvisionHandler(idGenerator, provisionService)

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

  test("provision return Right(ProvisioningStatus)") {
    (idGenerator.random _).expects().once().returns("my-id")
    (provisionService.provision _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Right(ProvisioningStatus("my-id", RUNNING, None)))

    val actual   = provisionHandler.provision(request)
    val expected = Right(ProvisioningStatus("my-id", RUNNING, None))
    assert(actual == expected)
  }

  test("provision return SystemErr on repo findByIdError") {
    (idGenerator.random _).expects().once().returns("my-id")
    (provisionService.provision _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Left(ProvisionerError("x")))

    val actual   = provisionHandler.provision(request)
    val expected = Left(SystemError("Unable to execute provision service: x"))
    assert(actual == expected)
  }

  test("unprovision return Right(ProvisioningStatus)") {
    (idGenerator.random _).expects().once().returns("my-id")
    (provisionService.unprovision _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Right(ProvisioningStatus("my-id", RUNNING, None)))

    val actual   = provisionHandler.unprovision(request)
    val expected = Right(ProvisioningStatus("my-id", RUNNING, None))
    assert(actual == expected)
  }

  test("unprovision return SystemErr on repo findByIdError") {
    (idGenerator.random _).expects().once().returns("my-id")
    (provisionService.unprovision _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Left(ProvisionerError("x")))

    val actual   = provisionHandler.unprovision(request)
    val expected = Left(SystemError("Unable to execute unprovision service: x"))
    assert(actual == expected)
  }

}
