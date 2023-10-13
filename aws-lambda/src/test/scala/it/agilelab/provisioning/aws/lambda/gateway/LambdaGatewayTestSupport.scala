package it.agilelab.provisioning.aws.lambda.gateway

import it.agilelab.provisioning.aws.lambda.gateway.LambdaGatewayError.{ InvokeErr, PayloadDeserErr }
import it.agilelab.provisioning.commons.support.ParserError.DecodeErr
import org.scalatest.EitherValues._

trait LambdaGatewayTestSupport {
  def assertInvokeErr[A](
    actual: Either[LambdaGatewayError, A],
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[InvokeErr])
    assert(actual.left.value.asInstanceOf[InvokeErr].error.getMessage == error)
  }

  def assertDecodeErr[A](
    actual: Either[LambdaGatewayError, A],
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[PayloadDeserErr])
    assert(actual.left.value.asInstanceOf[PayloadDeserErr].error.isInstanceOf[DecodeErr])
    assert(actual.left.value.asInstanceOf[PayloadDeserErr].error.asInstanceOf[DecodeErr].error.getMessage == error)
  }
}
