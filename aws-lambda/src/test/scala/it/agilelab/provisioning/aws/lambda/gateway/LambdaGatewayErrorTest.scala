package it.agilelab.provisioning.aws.lambda.gateway

import it.agilelab.provisioning.aws.lambda.gateway.LambdaGatewayError.{ InvokeResultErr, PayloadSerErr }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import cats.implicits._
import it.agilelab.provisioning.commons.support.ParserError.EncodeErr

class LambdaGatewayErrorTest extends AnyFunSuite with MockFactory {

  test("show PayloadSerErr") {
    val error: LambdaGatewayError = PayloadSerErr(EncodeErr(new IllegalArgumentException("x")))
    assert(error.show.startsWith("PayloadSerErr(EncodeErr(java.lang.IllegalArgumentException: x"))
  }

  test("show InvokeStatusErr") {
    val error: LambdaGatewayError = InvokeResultErr("lambdaName", "payload", 500)
    assert(error.show.startsWith("InvokeStatusErr(lambdaName,payload,500"))
  }
}
