package it.agilelab.provisioning.mesh.self.service.core.gateway

import io.circe.Json
import it.agilelab.provisioning.commons.principalsmapping.CdpIamUser
import it.agilelab.provisioning.mesh.self.service.api.model.Component.Workload
import it.agilelab.provisioning.mesh.self.service.api.model.{ DataProduct, ProvisionRequest }
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import org.scalatest.funsuite.AnyFunSuite

class PermissionlessComponentGatewayTest extends AnyFunSuite {

  val aPermissionlessGateway = new PermissionlessComponentGateway[String, String, String] {
    override def create(provisionCommand: ProvisionCommand[String, String]): Either[ComponentGatewayError, String]  =
      Right("OK")
    override def destroy(provisionCommand: ProvisionCommand[String, String]): Either[ComponentGatewayError, String] =
      Right("OK")
  }

  val request: ProvisionRequest[String, String] = ProvisionRequest(
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

  test("A permissionless gateway should return Left on updateAcl") {
    val actual   = aPermissionlessGateway.updateAcl(ProvisionCommand("my-id", request), Set(CdpIamUser("", "user", "")))
    val expected = Left(ComponentGatewayError("Update ACL is not a supported operation"))
    assert(actual == expected)
  }
}
