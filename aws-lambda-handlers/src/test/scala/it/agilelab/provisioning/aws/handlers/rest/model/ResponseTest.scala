package it.agilelab.provisioning.aws.handlers.rest.model

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import org.scalatest.funsuite.AnyFunSuite
import io.circe.generic.auto._

class ResponseTest extends AnyFunSuite {

  case class FakeOk(id: String)
  case class FakeBr(issue: String)
  case class FakeIse(issue: String)

  test("ok") {
    case class FakeData(id: String)
    val actual   = Response.ok(FakeOk("123"))
    val expected = Response(200, "{\"id\":\"123\"}")
    assert(actual == expected)
  }

  test("accepted") {
    val actual   = Response.accepted("123")
    val expected = Response(202, "\"123\"")
    assert(actual == expected)
  }

  test("badRequest") {
    val actual   = Response.badRequest(FakeBr("issue"))
    val expected = Response(
      statusCode = 400,
      body = "{\"issue\":\"issue\"}"
    )
    assert(actual == expected)
  }

  test("badRequest with case class list") {
    val actual   = Response.badRequest(Seq(FakeBr("issue")))
    val expected = Response(
      statusCode = 400,
      body = "[{\"issue\":\"issue\"}]"
    )
    assert(actual == expected)
  }

  test("internalServerError with case class") {
    val actual   = Response.internalServerError(FakeIse("issue"))
    val expected = Response(
      statusCode = 500,
      body = "{\"issue\":\"issue\"}"
    )
    assert(actual == expected)
  }

  test("internalServerError with simple string") {
    val actual   = Response.internalServerError("Not found")
    val expected = Response(
      statusCode = 500,
      body = "\"Not found\""
    )
    assert(actual == expected)
  }

  test("toApiGateway") {
    val actual   = Response.toApiGateway(Response(123, "x"))
    val expected = new APIGatewayProxyResponseEvent().withStatusCode(123).withBody("x")
    assert(actual == expected)
  }
}
