package it.agilelab.provisioning.aws.handlers.rest

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{ APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent }
import it.agilelab.provisioning.aws.handlers.rest.model.{ Request, Response }
import org.scalamock.function.MockFunction1
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

import scala.jdk.CollectionConverters.MapHasAsJava

class LambdaProxyRestApiTest extends AnyFunSuite with MockFactory with LambdaProxyRestApiHandler {
  val context: Context = mock[Context]

  val mockHandle: MockFunction1[Request, Response] = mockFunction[Request, Response]

  override def handle(request: Request): Response =
    mockHandle(request)

  test("handleRequest with Base64 body") {
    mockHandle
      .expects(Request("POST", Map("x" -> "y"), "path", Map.empty, Map.empty, "Test"))
      .returning(Response(1, "x"))
      .once()

    val request = new APIGatewayProxyRequestEvent()
      .withHttpMethod("POST")
      .withPath("path")
      .withBody("VGVzdA==")
      .withHeaders(Map("x" -> "y").asJava)
      .withIsBase64Encoded(true)

    val actual = handleRequest(request, context)

    val expected = new APIGatewayProxyResponseEvent()
      .withStatusCode(1)
      .withBody("x")

    assert(actual == expected)
  }

  test("handleRequest with clear body") {
    mockHandle
      .expects(Request("POST", Map("x" -> "y"), "path", Map.empty, Map.empty, "Test"))
      .returning(Response(1, "x"))
      .once()

    val request = new APIGatewayProxyRequestEvent()
      .withHttpMethod("POST")
      .withPath("path")
      .withBody("Test")
      .withHeaders(Map("x" -> "y").asJava)
      .withIsBase64Encoded(false)

    val actual = handleRequest(request, context)

    val expected = new APIGatewayProxyResponseEvent()
      .withStatusCode(1)
      .withBody("x")

    assert(actual == expected)
  }
}
