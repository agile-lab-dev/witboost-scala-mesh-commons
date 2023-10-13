package it.agilelab.provisioning.aws.handlers.rest.model

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import io.circe.Encoder

/** Define a Rest response
  *
  * @param statusCode: Http status code
  * @param body: body on string format
  */
final case class Response(statusCode: Int, body: String)

object Response {

  /** Generate a 200 response with provided body on json format
    * @param body: Body instance
    * @param encoder: implicit [[Encoder]]  for body type
    * @tparam A: body type parameter
    * @return [[Response]]
    */
  def ok[A](body: A)(implicit encoder: Encoder[A]): Response =
    Response(200, toJson(body))

  /** Generate a 202 response with provided body on json format
    * @param body: Body instance
    * @param encoder: implicit [[Encoder]]  for body type
    * @tparam A: body type parameter
    * @return [[Response]]
    */
  def accepted[A](body: A)(implicit encoder: Encoder[A]): Response =
    Response(202, toJson(body))

  /** Generate a 400 response with provided body on json format
    * @param body: Body instance
    * @param encoder: implicit [[Encoder]]  for body type
    * @tparam A: body type parameter
    * @return [[Response]]
    */
  def badRequest[A](body: A)(implicit encoder: Encoder[A]): Response =
    Response(400, toJson(body))

  /** Generate a 500 response with provided body on json format
    * @param body: Body instnace
    * @param encoder: implicit [[Encoder]] for body type
    * @tparam A: body type parameter
    * @return [[Response]]
    */
  def internalServerError[A](body: A)(implicit encoder: Encoder[A]): Response =
    Response(500, toJson(body))

  /** Generate an [[APIGatewayProxyResponseEvent]] from a [[Response]]
    * @param response: [[Response]] instance
    * @return [[APIGatewayProxyResponseEvent]]
    */
  def toApiGateway(response: Response): APIGatewayProxyResponseEvent =
    new APIGatewayProxyResponseEvent()
      .withBody(response.body)
      .withStatusCode(response.statusCode)
      .withIsBase64Encoded(false)

  private def toJson[A](body: A)(implicit encoder: Encoder[A]): String =
    encoder(body).dropNullValues.dropEmptyValues.noSpaces

}
