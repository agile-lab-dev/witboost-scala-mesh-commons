package it.agilelab.provisioning.aws.lambda.gateway

import it.agilelab.provisioning.aws.lambda.gateway.LambdaGatewayError.InvokeErr
import io.circe.{ Decoder, Encoder }
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.audit.Audit
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.core.exception.SdkClientException

class DefaultLambdaGatewayWithAuditTest extends AnyFunSuite with MockFactory with LambdaGatewayTestSupport {

  val audit: Audit                               = mock[Audit]
  val defaultLambdaGateway: DefaultLambdaGateway = mock[DefaultLambdaGateway]
  val lambdaGateway: LambdaGateway               = new DefaultLambdaGatewayWithAudit(defaultLambdaGateway, audit)

  case class TestBody1(id: String, name: String)
  case class TestBody2(id: String, name: String, descr: String)

  case class Res(statusCode: Int, description: String)

  test("asyncCall call audit on success") {
    inSequence(
      (defaultLambdaGateway
        .asyncCall(_: String, _: TestBody1)(_: Encoder[TestBody1]))
        .expects("test-lambda", TestBody1("1", "test"), *)
        .once()
        .returns(Right()),
      (audit.info _)
        .expects("AsyncCall(lambda=test-lambda,payload=TestBody1(1,test)) completed successfully")
        .once()
    )
    val actual = lambdaGateway.asyncCall("test-lambda", TestBody1("1", "test"))
    assert(actual == Right())
  }

  test("asyncCall call audit on failure") {
    inSequence(
      (defaultLambdaGateway
        .asyncCall(_: String, _: TestBody1)(_: Encoder[TestBody1]))
        .expects("test-lambda", TestBody1("1", "test"), *)
        .once()
        .returns(Left(InvokeErr("x", "y", SdkClientException.create("x")))),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "AsyncCall(lambda=test-lambda,payload=TestBody1(1,test)) failed. Details: InvokeErr(x,y,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )
    assertInvokeErr(lambdaGateway.asyncCall("test-lambda", TestBody1("1", "test")), "x")
  }

  test("syncCall call audit on success") {
    inSequence(
      (defaultLambdaGateway
        .syncCall(_: String, _: TestBody1)(_: Encoder[TestBody1], _: Decoder[Res]))
        .expects("test-lambda", TestBody1("1", "test"), *, *)
        .once()
        .returns(Right(Res(200, "desc"))),
      (audit.info _)
        .expects("SyncCall(lambda=test-lambda,payload=TestBody1(1,test)) completed successfully")
        .once()
    )
    val actual = lambdaGateway.syncCall[TestBody1, Res]("test-lambda", TestBody1("1", "test"))
    assert(actual == Right(Res(200, "desc")))
  }

  test("syncCall call audit on failure") {
    inSequence(
      (defaultLambdaGateway
        .syncCall(_: String, _: TestBody1)(_: Encoder[TestBody1], _: Decoder[Res]))
        .expects("test-lambda", TestBody1("1", "test"), *, *)
        .once()
        .returns(Left(InvokeErr("x", "y", SdkClientException.create("x")))),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "SyncCall(lambda=test-lambda,payload=TestBody1(1,test)) failed. Details: InvokeErr(x,y,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )
    assertInvokeErr(lambdaGateway.syncCall[TestBody1, Res]("test-lambda", TestBody1("1", "test")), "x")
  }

}
