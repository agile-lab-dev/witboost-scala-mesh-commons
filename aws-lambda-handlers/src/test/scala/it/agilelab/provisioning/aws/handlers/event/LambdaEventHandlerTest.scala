package it.agilelab.provisioning.aws.handlers.event

import com.amazonaws.services.lambda.runtime.Context
import org.scalamock.function.MockFunction1
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

class LambdaEventHandlerTest extends AnyFunSuite with MockFactory with LambdaEventHandler {

  val context: Context                          = mock[Context]
  val handleMock: MockFunction1[String, String] = mockFunction[String, String]

  override def handle(event: String): String =
    handleMock(event)

  test("handleRequest") {
    handleMock.expects("my in").returns("my out")
    val outputStream = new ByteArrayOutputStream()
    val inputStream  = new ByteArrayInputStream("my in".getBytes("UTF-8"))

    handleRequest(inputStream, outputStream, context)
    assert(outputStream.toString("UTF-8") == "my out")
  }
}
