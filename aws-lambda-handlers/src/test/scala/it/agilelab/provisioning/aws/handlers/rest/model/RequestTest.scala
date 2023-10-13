package it.agilelab.provisioning.aws.handlers.rest.model

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import org.scalatest.funsuite.AnyFunSuite

class RequestTest extends AnyFunSuite {

  test("fromApiGateway with clear body") {
    val actual = Request.fromApiGateway(
      new APIGatewayProxyRequestEvent()
        .withHttpMethod("POST")
        .withPath("my-path")
        .withBody("x")
    )
    assert(actual == Request("POST", Map.empty, "my-path", Map.empty, Map.empty, "x"))
  }

  test("fromApiGateway with base64 body") {
    val actual = Request.fromApiGateway(
      new APIGatewayProxyRequestEvent()
        .withHttpMethod("POST")
        .withPath("my-path")
        .withBody("x")
    )
    assert(actual == Request("POST", Map.empty, "my-path", Map.empty, Map.empty, "x"))
  }
}
