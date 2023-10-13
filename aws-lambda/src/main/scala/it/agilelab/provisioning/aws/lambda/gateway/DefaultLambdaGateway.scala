package it.agilelab.provisioning.aws.lambda.gateway

import cats.implicits._
import it.agilelab.provisioning.aws.lambda.gateway.LambdaGatewayError.{ InvokeErr, InvokeResultErr, PayloadDeserErr }
import io.circe.{ Decoder, Encoder }
import it.agilelab.provisioning.commons.support.ParserSupport
import software.amazon.awssdk.core.SdkBytes._
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.{ InvocationType, InvokeResponse }
import software.amazon.awssdk.services.lambda.model.InvocationType._
import software.amazon.awssdk.services.lambda.model.InvokeRequest._

/** Default LambdaGateway implementation
  * @param lambdaClient: A LambdaClient instance
  */
class DefaultLambdaGateway(lambdaClient: LambdaClient) extends LambdaGateway with ParserSupport {

  override def asyncCall[A](lambdaName: String, body: A)(implicit ev: Encoder[A]): Either[LambdaGatewayError, Unit] =
    execCall(lambdaName, toJson(body), EVENT)(_ => ())

  override def syncCall[A, B](lambdaName: String, body: A)(implicit
    ev: Encoder[A],
    decoder: Decoder[B]
  ): Either[LambdaGatewayError, B] =
    execCall(lambdaName, toJson(body), REQUEST_RESPONSE)(r => r.payload().asUtf8String())
      .flatMap(r => fromJson[B](r).leftMap(e => PayloadDeserErr(e)))

  private def execCall[A](lambdaName: String, payload: String, invocationType: InvocationType)(
    onSuccess: InvokeResponse => A
  ): Either[LambdaGatewayError, A] =
    try {
      val invokeResult = lambdaClient.invoke(
        builder()
          .functionName(lambdaName)
          .payload(fromUtf8String(payload))
          .invocationType(invocationType)
          .build()
      )
      if ((invokeResult.statusCode() / 100) == 2) Right(onSuccess(invokeResult))
      else Left(InvokeResultErr(lambdaName, payload, invokeResult.statusCode()))
    } catch {
      case t: Throwable => Left(InvokeErr(lambdaName, payload, t))
    }

}
