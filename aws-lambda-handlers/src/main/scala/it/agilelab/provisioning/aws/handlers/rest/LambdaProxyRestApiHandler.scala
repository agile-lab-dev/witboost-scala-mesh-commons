package it.agilelab.provisioning.aws.handlers.rest

import com.amazonaws.services.lambda.runtime.events.{ APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent }
import com.amazonaws.services.lambda.runtime.{ Context, RequestHandler }
import it.agilelab.provisioning.aws.handlers.rest.model.{ Request, Response }
import it.agilelab.provisioning.commons.support.ParserSupport

/** Lambda Proxy Rest Api Handler
  *
  * Allow user to create a Lambda RequestHandler based on ApiGatewayProxy integration
  * Map APIGatewayProxyRequestEvent to Request execute the handle method and map Response back to ApiGatewayProxyResponseEvent
  */
trait LambdaProxyRestApiHandler
    extends RequestHandler[APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent]
    with ParserSupport {

  protected def handle(request: Request): Response

  /** handleRequest
    *
    * Map ApiGatewayProxyRequestEvent to Request
    * Execute the handle method
    * Map the response back to ApiGatewayProxyResponseEvent
    * @param input: [[APIGatewayProxyRequestEvent]] instance
    * @param context: Context
    * @return [[APIGatewayProxyResponseEvent]]
    */
  override def handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent = {
    val req = Request.fromApiGateway(input)
    val res = handle(req)
    Response.toApiGateway(res)
  }
}
