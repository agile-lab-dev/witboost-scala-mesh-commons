package it.agilelab.provisioning.aws.handlers.event

import com.amazonaws.services.lambda.runtime.{ Context, RequestStreamHandler }

import java.io.{ InputStream, OutputStream, OutputStreamWriter }
import scala.io.Source.fromInputStream
import scala.util.{ Try, Using }

/** Lambda Event Handler.
  *
  * A trait to create a Lambda that handle stream event
  */
trait LambdaEventHandler extends RequestStreamHandler {

  private val CHARSET = "UTF-8"

  /** Handle input event as String.
    *
    * contains the main logic of the lambda function
    *
    * @param event: String input event on string format
    * @return String output
    */
  protected def handle(event: String): String

  /** handleRequest
    *
    * The main handle method of the generated lambda function.
    * Read the InputStream as a simple String. Execute the handle method and then write the Output to OutputStream
    * @param input: InputStream instance
    * @param output: OutputStream instance
    * @param context: Context
    */
  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit =
    for {
      event  <- Try(fromInputStream(input, CHARSET).mkString)
      result <- Try(handle(event))
      out    <- Using(new OutputStreamWriter(output))(o => o.write(result))
    } yield out

}
