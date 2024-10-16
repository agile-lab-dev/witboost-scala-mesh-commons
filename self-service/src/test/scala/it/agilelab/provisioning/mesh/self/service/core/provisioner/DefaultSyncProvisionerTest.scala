package it.agilelab.provisioning.mesh.self.service.core.provisioner

import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status.COMPLETED
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse._
import it.agilelab.provisioning.mesh.self.service.api.model.Component.Workload
import it.agilelab.provisioning.mesh.self.service.api.model.{ DataProduct, ProvisionRequest }
import it.agilelab.provisioning.mesh.self.service.core.gateway.{ ComponentGateway, ComponentGatewayError }
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import io.circe.Json
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.principalsmapping.{ CdpIamPrincipals, CdpIamUser }

class DefaultSyncProvisionerTest extends AnyFunSuite with MockFactory {
  case class ComponentResponse(id: String, value: String)

  val componentGateway: ComponentGateway[String, String, ComponentResponse, CdpIamPrincipals] =
    mock[ComponentGateway[String, String, ComponentResponse, CdpIamPrincipals]]
  val provisioner                                                                             =
    new DefaultSyncProvisioner[String, String, ComponentResponse, CdpIamPrincipals](componentGateway)

  val request: ProvisionRequest[String, String] =
    ProvisionRequest(
      DataProduct[String](
        id = "my-dp-id",
        name = "my-dp-name",
        domain = "my-dp-domain",
        environment = "my-dp-environment",
        version = "my-dp-version",
        dataProductOwner = "my-dp-owner",
        devGroup = "dev-group",
        ownerGroup = "owner-group",
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

  val refs: Set[CdpIamPrincipals] = Set(CdpIamUser("", "user1", ""), CdpIamUser("", "user2", ""))

  test("provision return Right(Provision(id,COMPLETED,None))") {
    (componentGateway.create _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Right(ComponentResponse("x", "y")))

    val actual   = provisioner.provision(ProvisionCommand("my-id", request))
    val expected = Right(ProvisioningStatus("my-id", COMPLETED, Some("""{"id":"x","value":"y"}""")))
    assert(actual == expected)
  }

  test("provision return Left(ProvisionErr) on create fail") {
    (componentGateway.create _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Left(ComponentGatewayError("fail")))
    val actual   = provisioner.provision(ProvisionCommand("my-id", request))
    val expected = Left(ProvisionerError("Unable to complete provision. Component gateway error: fail"))
    assert(actual == expected)
  }

  test("unprovision return Right(Provision(id,COMPLETED,None))") {
    (componentGateway.destroy _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Right(ComponentResponse("x", "y")))

    val actual   = provisioner.unprovision(ProvisionCommand("my-id", request))
    val expected = Right(ProvisioningStatus("my-id", COMPLETED, Some("""{"id":"x","value":"y"}""")))
    assert(actual == expected)
  }

  test("unprovision return Left(ProvisionErr) on destroy fail") {
    (componentGateway.destroy _)
      .expects(ProvisionCommand("my-id", request))
      .once()
      .returns(Left(ComponentGatewayError("fail")))
    val actual   = provisioner.unprovision(ProvisionCommand("my-id", request))
    val expected = Left(ProvisionerError("Unable to complete unprovision. Component gateway error: fail"))
    assert(actual == expected)
  }

  test("updateAcl return Right(Provision(id,COMPLETED,None))") {
    (componentGateway.updateAcl _)
      .expects(ProvisionCommand("my-id", request), refs)
      .once()
      .returns(Right(refs))

    val actual   = provisioner.updateAcl(ProvisionCommand("my-id", request), refs)
    val expected = Right(ProvisioningStatus("my-id", COMPLETED, None))
    assert(actual == expected)
  }

  test("updateAcl return Left(ProvisionErr) on updateAcl fail") {
    (componentGateway.updateAcl _)
      .expects(ProvisionCommand("my-id", request), refs)
      .once()
      .returns(Left(ComponentGatewayError("fail")))
    val actual   = provisioner.updateAcl(ProvisionCommand("my-id", request), refs)
    val expected = Left(ProvisionerError("Unable to complete update ACL. Component gateway error: fail"))
    assert(actual == expected)
  }

}
