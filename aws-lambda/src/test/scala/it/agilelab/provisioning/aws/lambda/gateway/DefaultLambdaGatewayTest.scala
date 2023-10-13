package it.agilelab.provisioning.aws.lambda.gateway

import it.agilelab.provisioning.aws.lambda.gateway.LambdaGatewayError.{ InvokeErr, InvokeResultErr }
import io.circe.generic.auto._
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.{ InvocationType, InvokeRequest, InvokeResponse }

class DefaultLambdaGatewayTest extends AnyFunSuite with MockFactory with LambdaGatewayTestSupport {

  val lambdaClient: LambdaClient   = mock[LambdaClient]
  val lambdaGateway: LambdaGateway = new DefaultLambdaGateway(lambdaClient)

  case class TestBody1(id: String, name: String)
  case class TestBody2(id: String, name: String, descr: String)

  case class Resp(statusCode: Int, description: String)

  test("asyncCall return Right() with TestBody1") {
    (lambdaClient
      .invoke(_: InvokeRequest))
      .expects(
        InvokeRequest
          .builder()
          .functionName("test-lambda")
          .payload(SdkBytes.fromUtf8String("{\"id\":\"1\",\"name\":\"test\"}"))
          .invocationType(InvocationType.EVENT)
          .build()
      )
      .once()
      .returns(InvokeResponse.builder().statusCode(202).build())

    val actual = lambdaGateway.asyncCall("test-lambda", TestBody1("1", "test"))
    assert(actual == Right())
  }

  test("asyncCall return Right() with TestBody2") {
    (lambdaClient
      .invoke(_: InvokeRequest))
      .expects(
        InvokeRequest
          .builder()
          .functionName("test-lambda")
          .payload(SdkBytes.fromUtf8String("{\"id\":\"1\",\"name\":\"test\",\"descr\":\"right\"}"))
          .invocationType(InvocationType.EVENT)
          .build()
      )
      .once()
      .returns(InvokeResponse.builder().statusCode(202).build())

    val actual = lambdaGateway.asyncCall("test-lambda", TestBody2("1", "test", "right"))
    assert(actual == Right())
  }

  test("asyncCall return Left(Error) exception") {
    (lambdaClient
      .invoke(_: InvokeRequest))
      .expects(
        InvokeRequest
          .builder()
          .functionName("test-lambda")
          .payload(SdkBytes.fromUtf8String("{\"id\":\"1\",\"name\":\"test\",\"descr\":\"right\"}"))
          .invocationType(InvocationType.EVENT)
          .build()
      )
      .once()
      .throws(SdkClientException.create("x"))

    assertInvokeErr(lambdaGateway.asyncCall("test-lambda", TestBody2("1", "test", "right")), "x")
  }

  test("asyncCall return Left(Error) status code") {
    (lambdaClient
      .invoke(_: InvokeRequest))
      .expects(
        InvokeRequest
          .builder()
          .functionName("test-lambda")
          .payload(SdkBytes.fromUtf8String("{\"id\":\"1\",\"name\":\"test\",\"descr\":\"right\"}"))
          .invocationType(InvocationType.EVENT)
          .build()
      )
      .once()
      .returns(InvokeResponse.builder().statusCode(404).build())

    val actual   = lambdaGateway.asyncCall("test-lambda", TestBody2("1", "test", "right"))
    val expected = Left(
      InvokeResultErr(
        "test-lambda",
        "{\"id\":\"1\",\"name\":\"test\",\"descr\":\"right\"}",
        404
      )
    )
    assert(actual == expected)
  }

  test("syncCall return Right() with TestBody1") {
    (lambdaClient
      .invoke(_: InvokeRequest))
      .expects(
        InvokeRequest
          .builder()
          .functionName("test-lambda")
          .payload(SdkBytes.fromUtf8String("{\"id\":\"1\",\"name\":\"test\"}"))
          .invocationType(InvocationType.REQUEST_RESPONSE)
          .build()
      )
      .once()
      .returns(
        InvokeResponse
          .builder()
          .statusCode(200)
          .payload(SdkBytes.fromUtf8String("{\"statusCode\":200, \"description\":\"description\"}"))
          .build()
      )

    val actual = lambdaGateway.syncCall[TestBody1, Resp]("test-lambda", TestBody1("1", "test"))
    assert(actual == Right(Resp(200, "description")))
  }

  test("syncCall return Left(Error) exception") {
    (lambdaClient
      .invoke(_: InvokeRequest))
      .expects(
        InvokeRequest
          .builder()
          .functionName("test-lambda")
          .payload(SdkBytes.fromUtf8String("{\"id\":\"1\",\"name\":\"test\",\"descr\":\"right\"}"))
          .invocationType(InvocationType.REQUEST_RESPONSE)
          .build()
      )
      .once()
      .throws(SdkClientException.create("x"))

    assertInvokeErr(lambdaGateway.syncCall[TestBody2, Resp]("test-lambda", TestBody2("1", "test", "right")), "x")
  }

  test("syncCall return Left(Error) status code") {
    (lambdaClient
      .invoke(_: InvokeRequest))
      .expects(
        InvokeRequest
          .builder()
          .functionName("test-lambda")
          .payload(SdkBytes.fromUtf8String("{\"id\":\"1\",\"name\":\"test\",\"descr\":\"right\"}"))
          .invocationType(InvocationType.REQUEST_RESPONSE)
          .build()
      )
      .once()
      .returns(InvokeResponse.builder().statusCode(404).build())

    val actual   = lambdaGateway.syncCall[TestBody2, Resp]("test-lambda", TestBody2("1", "test", "right"))
    val expected = Left(
      InvokeResultErr(
        "test-lambda",
        "{\"id\":\"1\",\"name\":\"test\",\"descr\":\"right\"}",
        404
      )
    )
    assert(actual == expected)
  }

  test("syncCall return Left(Error) decoding") {
    (lambdaClient
      .invoke(_: InvokeRequest))
      .expects(
        InvokeRequest
          .builder()
          .functionName("test-lambda")
          .payload(SdkBytes.fromUtf8String("{\"id\":\"1\",\"name\":\"test\",\"descr\":\"right\"}"))
          .invocationType(InvocationType.REQUEST_RESPONSE)
          .build()
      )
      .once()
      .returns(InvokeResponse.builder().statusCode(200).payload(SdkBytes.fromUtf8String("xxx")).build())

    val actual = lambdaGateway.syncCall[TestBody2, Resp]("test-lambda", TestBody2("1", "test", "right"))
    assertDecodeErr(actual, "expected json value got 'xxx' (line 1, column 1)")
  }
}
