package it.agilelab.provisioning.mesh.self.service.lambda.core.gateway

import it.agilelab.provisioning.aws.lambda.gateway.LambdaGateway
import it.agilelab.provisioning.aws.lambda.gateway.LambdaGatewayError.{
  InvokeErr,
  InvokeResultErr,
  LambdaGatewayInitErr,
  PayloadSerErr
}
import it.agilelab.provisioning.mesh.self.service.api.model.Component.Workload
import it.agilelab.provisioning.mesh.self.service.api.model.{ DataProduct, ProvisionRequest }
import it.agilelab.provisioning.mesh.self.service.core.gateway.ComponentGatewayError
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.ComponentOperation.{ Create, Destroy }
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.ComponentServiceCommand
import io.circe.{ Encoder, Json }
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.support.ParserError.EncodeErr
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class AsyncCallLambdaComponentGatewayServiceTest extends AnyFunSuite with MockFactory {

  val lambdaGateway: LambdaGateway = mock[LambdaGateway]
  val asyncLambdaComponentGateway  = new AsyncCallLambdaComponentGateway[String, String](
    lambdaGateway,
    provisionLambdaFunction = "my-provision-lambda-service",
    unprovisionLambdaFunction = "my-unprovision-lambda-service"
  )

  val provisionRequest: ProvisionRequest[String, String] = ProvisionRequest(
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

  test("create will call an async lambda and return Right()") {
    val provisionCommand = ProvisionCommand[String, String](
      requestId = "my-req-id",
      provisionRequest = provisionRequest
    )

    (lambdaGateway
      .asyncCall[ComponentServiceCommand[String, String]](_: String, _: ComponentServiceCommand[String, String])(
        _: Encoder[ComponentServiceCommand[String, String]]
      ))
      .expects("my-provision-lambda-service", ComponentServiceCommand(Create, provisionCommand), *)
      .once()
      .returns(Right())

    val actual = asyncLambdaComponentGateway.create(provisionCommand)
    assert(actual == Right())
  }

  test("create will call an async lambda and return Left(CreateComponentExecutionErr) on LambdaGatewayInitErr") {
    val provisionCommand = ProvisionCommand[String, String](
      requestId = "my-req-id",
      provisionRequest = provisionRequest
    )
    (lambdaGateway
      .asyncCall[ComponentServiceCommand[String, String]](_: String, _: ComponentServiceCommand[String, String])(
        _: Encoder[ComponentServiceCommand[String, String]]
      ))
      .expects("my-provision-lambda-service", ComponentServiceCommand(Create, provisionCommand), *)
      .once()
      .returns(Left(LambdaGatewayInitErr(new IllegalArgumentException("x"))))

    val actual   = asyncLambdaComponentGateway.create(provisionCommand)
    val expected = Left(ComponentGatewayError("Unable to initialize LambdaGateway"))
    assert(actual == expected)
  }

  test("create will call an async lambda and return Left(CreateComponentExecutionErr) on PayloadSerErr") {
    val provisionCommand = ProvisionCommand[String, String](
      requestId = "my-req-id",
      provisionRequest = provisionRequest
    )
    (lambdaGateway
      .asyncCall[ComponentServiceCommand[String, String]](_: String, _: ComponentServiceCommand[String, String])(
        _: Encoder[ComponentServiceCommand[String, String]]
      ))
      .expects("my-provision-lambda-service", ComponentServiceCommand(Create, provisionCommand), *)
      .once()
      .returns(Left(PayloadSerErr(EncodeErr(new IllegalArgumentException("x")))))

    val actual   = asyncLambdaComponentGateway.create(provisionCommand)
    val expected = Left(ComponentGatewayError("Unable to serialize the provided provision command"))
    assert(actual == expected)
  }

  test("create will call an async lambda and return Left(CreateComponentExecutionErr) on InvokeErr") {
    val provisionCommand = ProvisionCommand[String, String](
      requestId = "my-req-id",
      provisionRequest = provisionRequest
    )
    (lambdaGateway
      .asyncCall[ComponentServiceCommand[String, String]](_: String, _: ComponentServiceCommand[String, String])(
        _: Encoder[ComponentServiceCommand[String, String]]
      ))
      .expects("my-provision-lambda-service", ComponentServiceCommand(Create, provisionCommand), *)
      .once()
      .returns(Left(InvokeErr("lambda", "payload", new IllegalArgumentException("x"))))

    val actual   = asyncLambdaComponentGateway.create(provisionCommand)
    val expected = Left(
      ComponentGatewayError("Unable to run lambda service lambda with payload payload. An exception was raised")
    )
    assert(actual == expected)
  }

  test("create will call an async lambda and return Left(CreateComponentExecutionErr) on InvokeResultErr") {
    val provisionCommand = ProvisionCommand[String, String](
      requestId = "my-req-id",
      provisionRequest = provisionRequest
    )
    (lambdaGateway
      .asyncCall[ComponentServiceCommand[String, String]](_: String, _: ComponentServiceCommand[String, String])(
        _: Encoder[ComponentServiceCommand[String, String]]
      ))
      .expects("my-provision-lambda-service", ComponentServiceCommand(Create, provisionCommand), *)
      .once()
      .returns(Left(InvokeResultErr("lambda", "payload", 500)))

    val actual   = asyncLambdaComponentGateway.create(provisionCommand)
    val expected = Left(
      ComponentGatewayError("Unable to run lambda service lambda with payload payload. Unexpected status: 500")
    )
    assert(actual == expected)
  }

  test("destroy will call an async lambda and return Right()") {
    val provisionCommand = ProvisionCommand[String, String](
      requestId = "my-req-id",
      provisionRequest = provisionRequest
    )

    (lambdaGateway
      .asyncCall[ComponentServiceCommand[String, String]](_: String, _: ComponentServiceCommand[String, String])(
        _: Encoder[ComponentServiceCommand[String, String]]
      ))
      .expects("my-unprovision-lambda-service", ComponentServiceCommand(Destroy, provisionCommand), *)
      .once()
      .returns(Right())

    val actual = asyncLambdaComponentGateway.destroy(provisionCommand)
    assert(actual == Right())
  }

  test("destroy will call an async lambda and return Left(CreateComponentExecutionErr) on LambdaGatewayInitErr") {
    val provisionCommand = ProvisionCommand[String, String](
      requestId = "my-req-id",
      provisionRequest = provisionRequest
    )
    (lambdaGateway
      .asyncCall[ComponentServiceCommand[String, String]](_: String, _: ComponentServiceCommand[String, String])(
        _: Encoder[ComponentServiceCommand[String, String]]
      ))
      .expects("my-unprovision-lambda-service", ComponentServiceCommand(Destroy, provisionCommand), *)
      .once()
      .returns(Left(LambdaGatewayInitErr(new IllegalArgumentException("x"))))

    val actual   = asyncLambdaComponentGateway.destroy(provisionCommand)
    val expected = Left(ComponentGatewayError("Unable to initialize LambdaGateway"))
    assert(actual == expected)
  }

  test("destroy will call an async lambda and return Left(CreateComponentExecutionErr) on PayloadSerErr") {
    val provisionCommand = ProvisionCommand[String, String](
      requestId = "my-req-id",
      provisionRequest = provisionRequest
    )
    (lambdaGateway
      .asyncCall[ComponentServiceCommand[String, String]](_: String, _: ComponentServiceCommand[String, String])(
        _: Encoder[ComponentServiceCommand[String, String]]
      ))
      .expects("my-unprovision-lambda-service", ComponentServiceCommand(Destroy, provisionCommand), *)
      .once()
      .returns(Left(PayloadSerErr(EncodeErr(new IllegalArgumentException("x")))))

    val actual   = asyncLambdaComponentGateway.destroy(provisionCommand)
    val expected = Left(ComponentGatewayError("Unable to serialize the provided provision command"))
    assert(actual == expected)
  }

  test("destroy will call an async lambda and return Left(CreateComponentExecutionErr) on InvokeErr") {
    val provisionCommand = ProvisionCommand[String, String](
      requestId = "my-req-id",
      provisionRequest = provisionRequest
    )
    (lambdaGateway
      .asyncCall[ComponentServiceCommand[String, String]](_: String, _: ComponentServiceCommand[String, String])(
        _: Encoder[ComponentServiceCommand[String, String]]
      ))
      .expects("my-unprovision-lambda-service", ComponentServiceCommand(Destroy, provisionCommand), *)
      .once()
      .returns(Left(InvokeErr("lambda", "payload", new IllegalArgumentException("x"))))

    val actual   = asyncLambdaComponentGateway.destroy(provisionCommand)
    val expected = Left(
      ComponentGatewayError("Unable to run lambda service lambda with payload payload. An exception was raised")
    )
    assert(actual == expected)
  }

  test("destroy will call an async lambda and return Left(CreateComponentExecutionErr) on InvokeResultErr") {
    val provisionCommand = ProvisionCommand[String, String](
      requestId = "my-req-id",
      provisionRequest = provisionRequest
    )
    (lambdaGateway
      .asyncCall[ComponentServiceCommand[String, String]](_: String, _: ComponentServiceCommand[String, String])(
        _: Encoder[ComponentServiceCommand[String, String]]
      ))
      .expects("my-unprovision-lambda-service", ComponentServiceCommand(Destroy, provisionCommand), *)
      .once()
      .returns(Left(InvokeResultErr("lambda", "payload", 500)))

    val actual   = asyncLambdaComponentGateway.destroy(provisionCommand)
    val expected = Left(
      ComponentGatewayError("Unable to run lambda service lambda with payload payload. Unexpected status: 500")
    )
    assert(actual == expected)
  }
}
