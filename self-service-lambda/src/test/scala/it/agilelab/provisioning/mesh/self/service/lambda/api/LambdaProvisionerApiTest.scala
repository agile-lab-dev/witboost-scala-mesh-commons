package it.agilelab.provisioning.mesh.self.service.lambda.api

import it.agilelab.provisioning.aws.handlers.rest.model.{ Request, Response }
import it.agilelab.provisioning.aws.lambda.gateway.LambdaGateway
import it.agilelab.provisioning.mesh.repository.Repository
import it.agilelab.provisioning.mesh.self.service.api.controller.ProvisionerController
import it.agilelab.provisioning.mesh.self.service.api.model.ApiError.{ sysErr, validErr }
import it.agilelab.provisioning.mesh.self.service.api.model.ApiRequest.ProvisioningRequest
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.Status._
import it.agilelab.provisioning.mesh.self.service.api.model.ApiResponse.{ invalid, valid, ProvisioningStatus }
import it.agilelab.provisioning.mesh.self.service.api.model.{ Component, ProvisionRequest, ProvisioningDescriptor }
import io.circe.Decoder
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.principalsmapping.CdpIamPrincipals
import it.agilelab.provisioning.commons.validator.Validator
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class LambdaProvisionerApiTest extends AnyFunSuite with MockFactory {
  val provisionerController: ProvisionerController[String, String, CdpIamPrincipals] =
    stub[ProvisionerController[String, String, CdpIamPrincipals]]

  test("default") {
    val validator     = mock[Validator[ProvisionRequest[String, String]]]
    val repository    = mock[Repository[ProvisioningStatus, String, Unit]]
    val lambdaGateway = mock[LambdaGateway]
    val actual        =
      LambdaProvisionerApi.default[String, String](
        validator,
        repository,
        lambdaGateway,
        "lambdaProvision",
        "lambdaUnprovision",
        ApiConfig.default()
      )
    assert(actual.isInstanceOf[LambdaProvisionerApi[String, String]])
  }

  test("POST /provision return 202 with id") {
    (provisionerController
      .provision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(ProvisioningStatus("id", RUNNING, None)))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/provision",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    assert(actual == Response(202, "\"id\""))
  }

  test("POST /rootPath/provision return 202 with id") {
    (provisionerController
      .provision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(ProvisioningStatus("id", RUNNING, None)))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.withRootPath("/rootPath")
      ).handle(
        Request(
          "POST",
          Map.empty,
          "/rootPath/provision",
          Map.empty,
          Map.empty,
          """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
        )
      )
    assert(actual == Response(202, "\"id\""))
  }

  test("POST /my/awesome/rootPath/provision return 202 with id") {
    (provisionerController
      .provision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(ProvisioningStatus("id", RUNNING, None)))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.withRootPath("/my/awesome/rootPath/")
      ).handle(
        Request(
          "POST",
          Map.empty,
          "/my/awesome/rootPath/provision",
          Map.empty,
          Map.empty,
          """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
        )
      )
    assert(actual == Response(202, "\"id\""))
  }

  test("POST /provision return 200 a Completed ProvisioningStatus") {
    (provisionerController
      .provision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(ProvisioningStatus("id", COMPLETED, Some("x"))))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/provision",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    assert(actual == Response(200, "{\"id\":\"id\",\"status\":\"COMPLETED\",\"result\":\"x\"}"))
  }

  test("POST /provision return 200 a Failed ProvisioningStatus") {
    (provisionerController
      .provision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(ProvisioningStatus("id", FAILED, Some("x"))))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/provision",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    assert(actual == Response(200, "{\"id\":\"id\",\"status\":\"FAILED\",\"result\":\"x\"}"))
  }

  test("POST /provision return 400") {
    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/provision",
            Map.empty,
            Map.empty,
            """{"descripto": "\ndataProduct: dp\nenvironment: dev\nversion: 0.0.1\nname: x\notherField: y"}""".stripMargin
          )
        )

    val expected = Response(
      400,
      "{\"errors\":[\"DecodeErr(DecodingFailure at .descriptor: Attempt to decode value on failed cursor)\"]}"
    )
    assert(actual == expected)
  }

  test("POST /provision return 400 with ValidationError") {
    (provisionerController
      .provision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Left(validErr("my-error")))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/provision",
            Map.empty,
            Map.empty,
            """{"descriptor": "dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    val expected = Response(
      400,
      "{\"errors\":[\"my-error\"]}"
    )
    assert(actual == expected)
  }

  test("POST /provision return 500 with SystemError") {
    (provisionerController
      .provision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Left(sysErr("my-error")))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/provision",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    assert(actual == Response(500, "{\"error\":\"my-error\"}"))
  }

  test("GET /provision/id/status return 200") {
    (provisionerController.getProvisionStatus _)
      .when("my-id")
      .returns(Right(ProvisioningStatus("x", RUNNING, None)))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig("/validate", "/provision", "/provision/(.*?)/status", "/unprovision")
      )
        .handle(
          Request(
            "GET",
            Map.empty,
            "/provision/my-id/another/status",
            Map("id" -> "my-id"),
            Map.empty,
            ""
          )
        )
    assert(actual == Response(200, "{\"id\":\"x\",\"status\":\"RUNNING\"}"))
  }

  test("GET /rootPath/provision/id/status return 200") {
    (provisionerController.getProvisionStatus _)
      .when("my-id")
      .returns(Right(ProvisioningStatus("x", RUNNING, None)))

    val actual =
      new LambdaProvisionerApi[String, String](provisionerController, ApiConfig.withRootPath("/rootPath")).handle(
        Request(
          "GET",
          Map.empty,
          "/rootPath/provision/id/status",
          Map("id" -> "my-id"),
          Map.empty,
          ""
        )
      )
    assert(actual == Response(200, "{\"id\":\"x\",\"status\":\"RUNNING\"}"))
  }

  test("GET /my/awesome/rootPath/provision/id/status return 200 ") {
    (provisionerController.getProvisionStatus _)
      .when("my-id")
      .returns(Right(ProvisioningStatus("x", RUNNING, None)))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.withRootPath("/my/awesome/rootPath/")
      ).handle(
        Request(
          "GET",
          Map.empty,
          "/my/awesome/rootPath/provision/id/status",
          Map("id" -> "my-id"),
          Map.empty,
          ""
        )
      )
    assert(actual == Response(200, "{\"id\":\"x\",\"status\":\"RUNNING\"}"))
  }

  test("GET /provision/id/status state return 400") {
    (provisionerController.getProvisionStatus _)
      .when("my-id")
      .returns(Left(validErr("Not found provision request with id")))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "GET",
            Map.empty,
            "/provision/id/status",
            Map("id" -> "my-id"),
            Map.empty,
            ""
          )
        )

    val expected = Response(400, "{\"errors\":[\"Not found provision request with id\"]}")
    assert(actual == expected)
  }

  test("GET /provision/id/status state return 500") {
    (provisionerController.getProvisionStatus _)
      .when("my-id")
      .returns(Left(sysErr("x")))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "GET",
            Map.empty,
            "/provision/id/status",
            Map("id" -> "my-id"),
            Map.empty,
            ""
          )
        )

    assert(actual == Response(500, "{\"error\":\"x\"}"))
  }

  test("POST /validate return 200 invalid on wrong descriptor") {
    val actual   =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/validate",
            Map.empty,
            Map.empty,
            """{"descripto":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )
    val expected = Response(
      200,
      "{\"valid\":false,\"error\":{\"errors\":[\"DecodeErr(DecodingFailure at .descriptor: Attempt to decode value on failed cursor)\"]}}"
    )
    assert(actual == expected)
  }

  test("POST /validate return 200 invalid") {
    (provisionerController
      .validate(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(invalid("x")))

    val actual   =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/validate",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )
    val expected = Response(200, "{\"valid\":false,\"error\":{\"errors\":[\"x\"]}}")
    assert(actual == expected)
  }

  test("POST /validate return 200 valid") {
    (provisionerController
      .validate(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(valid()))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/validate",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    assert(actual == Response(200, "{\"valid\":true}"))
  }

  test("POST /rootPath/validate return 200 valid") {
    (provisionerController
      .validate(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(valid()))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.withRootPath("/rootPath/")
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/rootPath/validate",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    assert(actual == Response(200, "{\"valid\":true}"))
  }

  test("POST /my/awesome/rootPath/validate return 200 valid") {
    (provisionerController
      .validate(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(valid()))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.withRootPath("/my/awesome/rootPath/")
      ).handle(
        Request(
          "POST",
          Map.empty,
          "/my/awesome/rootPath/validate",
          Map.empty,
          Map.empty,
          """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
        )
      )

    assert(actual == Response(200, "{\"valid\":true}"))
  }

  test("POST /validate return 500 on system error") {
    (provisionerController
      .validate(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Left(sysErr("my-error")))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/validate",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    val expected = Response(500, "{\"error\":\"my-error\"}")
    assert(actual == expected)
  }

  test("POST /unprovision return 202 with id") {
    (provisionerController
      .unprovision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(ProvisioningStatus("id", RUNNING, None)))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/unprovision",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    assert(actual == Response(202, "\"id\""))
  }

  test("POST /rootPath/unprovision return 202 with id") {
    (provisionerController
      .unprovision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(ProvisioningStatus("id", RUNNING, None)))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.withRootPath("/rootPath")
      ).handle(
        Request(
          "POST",
          Map.empty,
          "/rootPath/unprovision",
          Map.empty,
          Map.empty,
          """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
        )
      )
    assert(actual == Response(202, "\"id\""))
  }

  test("POST /my/awesome/rootPath/unprovision return 202 with id") {
    (provisionerController
      .unprovision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(ProvisioningStatus("id", RUNNING, None)))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.withRootPath("/my/awesome/rootPath/")
      ).handle(
        Request(
          "POST",
          Map.empty,
          "/my/awesome/rootPath/unprovision",
          Map.empty,
          Map.empty,
          """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
        )
      )
    assert(actual == Response(202, "\"id\""))
  }

  test("POST /unprovision return 200 a Completed ProvisioningStatus") {
    (provisionerController
      .unprovision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(ProvisioningStatus("id", COMPLETED, Some("x"))))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/unprovision",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    assert(actual == Response(200, "{\"id\":\"id\",\"status\":\"COMPLETED\",\"result\":\"x\"}"))
  }

  test("POST /unprovision return 200 a Failed ProvisioningStatus") {
    (provisionerController
      .unprovision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Right(ProvisioningStatus("id", FAILED, Some("x"))))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/unprovision",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    assert(actual == Response(200, "{\"id\":\"id\",\"status\":\"FAILED\",\"result\":\"x\"}"))
  }

  test("POST /unprovision return 400") {
    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/unprovision",
            Map.empty,
            Map.empty,
            """{"descripto": "\ndataProduct: dp\nenvironment: dev\nversion: 0.0.1\nname: x\notherField: y"}""".stripMargin
          )
        )

    val expected = Response(
      400,
      "{\"errors\":[\"DecodeErr(DecodingFailure at .descriptor: Attempt to decode value on failed cursor)\"]}"
    )
    assert(actual == expected)
  }

  test("POST /unprovision return 400 with ValidationError") {
    (provisionerController
      .unprovision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Left(validErr("my-error")))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/unprovision",
            Map.empty,
            Map.empty,
            """{"descriptor": "dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    val expected = Response(
      400,
      "{\"errors\":[\"my-error\"]}"
    )
    assert(actual == expected)
  }

  test("POST /unprovision return 500 with SystemError") {
    (provisionerController
      .unprovision(_: ProvisioningRequest)(_: Decoder[ProvisioningDescriptor[String]], _: Decoder[Component[String]]))
      .when(*, *, *)
      .returns(Left(sysErr("my-error")))

    val actual =
      new LambdaProvisionerApi[String, String](
        provisionerController,
        ApiConfig.default()
      )
        .handle(
          Request(
            "POST",
            Map.empty,
            "/unprovision",
            Map.empty,
            Map.empty,
            """{"descriptor":"dataProduct:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   domain: domain\n   description: description\n   environment: environment\n   version: version\n   kind: kind\n   dataProductOwner: dataProductOwner\n   dataProductOwnerDisplayName: dataProductOwnerDisplayName\n   email: email\n   informationSLA: informationSLA\n   status: status\n   maturity: maturity\n   billing: {}\n   tags: []\n   specific: specific\ncomponent:\n   id: id\n   name: name\n   fullyQualifiedName: fullyQualifiedName\n   description: description\n   kind: kind\n   workloadType: workloadType\n   connectionType: connectionType\n   technology: technology\n   platform: platform\n   version: version\n   infrastructureTemplateId: infrastructureTemplateId\n   useCaseTemplateId: useCaseTemplateId\n   tags: []\n   readsFrom: []\n   specific: specific"}""".stripMargin
          )
        )

    assert(actual == Response(500, "{\"error\":\"my-error\"}"))
  }

  Seq(
    Request("PUT", Map.empty, "/validate", Map.empty, Map.empty, ""),
    Request("PATCH", Map.empty, "/validate", Map.empty, Map.empty, ""),
    Request("DELETE", Map.empty, "/validate", Map.empty, Map.empty, ""),
    Request("PUT", Map.empty, "/provision", Map.empty, Map.empty, ""),
    Request("PATCH", Map.empty, "/provision", Map.empty, Map.empty, ""),
    Request("DELETE", Map.empty, "/provision", Map.empty, Map.empty, ""),
    Request("PUT", Map.empty, "/provision/id/status", Map.empty, Map.empty, ""),
    Request("PATCH", Map.empty, "/provision/id/status", Map.empty, Map.empty, ""),
    Request("DELETE", Map.empty, "/provision/id/status", Map.empty, Map.empty, ""),
    Request("PUT", Map.empty, "/unprovision", Map.empty, Map.empty, ""),
    Request("PATCH", Map.empty, "/unprovision", Map.empty, Map.empty, ""),
    Request("DELETE", Map.empty, "/unprovision", Map.empty, Map.empty, "")
  ) foreach { request: Request =>
    test(s"${request.method} ${request.path} return 400 Method not found") {
      val actual   =
        new LambdaProvisionerApi[String, String](
          provisionerController,
          ApiConfig.default()
        )
          .handle(request)
      val expected = Response(400, s"""{"errors":["Method not found for ${request.path}"]}""")
      assert(actual == expected)
    }
  }

}
