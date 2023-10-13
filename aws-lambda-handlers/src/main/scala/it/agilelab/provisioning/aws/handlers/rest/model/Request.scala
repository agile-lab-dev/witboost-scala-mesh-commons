package it.agilelab.provisioning.aws.handlers.rest.model

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent

import java.util.Base64
import scala.jdk.CollectionConverters.MapHasAsScala

/** Define a Rest Request
  *
  * @param method: Http method
  * @param headers: Http headers
  * @param path: Http path
  * @param pathParameters: Http path parameters
  * @param queryStrings: Http query strings
  * @param body: Http request body
  */
final case class Request(
  method: String,
  headers: Map[String, String],
  path: String,
  pathParameters: Map[String, String],
  queryStrings: Map[String, String],
  body: String
)

object Request {

  /** Generate a [[Request]] starting from an [[APIGatewayProxyRequestEvent]]
    * @param request: an instance of [[APIGatewayProxyRequestEvent]]
    * @return [[Request]]
    */
  def fromApiGateway(request: APIGatewayProxyRequestEvent): Request =
    Request(
      method = request.getHttpMethod,
      headers = asScalaMapOrEmpty(request.getHeaders),
      path = request.getPath,
      pathParameters = asScalaMapOrEmpty(request.getPathParameters),
      queryStrings = asScalaMapOrEmpty(request.getQueryStringParameters),
      body = getBodyAsString(request.getBody, request.getIsBase64Encoded)
    )

  private def asScalaMapOrEmpty[T, V](map: java.util.Map[T, V]): Map[T, V] =
    if (map == null) Map.empty[T, V]
    else map.asScala.toMap

  private def getBodyAsString(body: String, isBase64Encoded: Boolean): String =
    if (isBase64Encoded) Base64.getDecoder.decode(body).map(_.toChar).mkString
    else body

}
