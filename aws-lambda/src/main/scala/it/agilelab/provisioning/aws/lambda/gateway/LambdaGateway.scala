package it.agilelab.provisioning.aws.lambda.gateway

import it.agilelab.provisioning.aws.lambda.gateway.LambdaGatewayError.LambdaGatewayInitErr
import io.circe.{ Decoder, Encoder }
import it.agilelab.provisioning.commons.audit.Audit
import software.amazon.awssdk.services.lambda.LambdaClient

/** LambdaGateway trait
  *
  * Provide an set of method to easily interact with AWS Lambda function
  */
trait LambdaGateway {

  /** Invoke a given lambda on async mode
    * @param lambdaName: lambda function name
    * @param body: content to sent to the invoked lambda
    * @tparam A: content type param
    * @return Right() if successful invoke lambda
    *         Left(Error) otherwise
    */
  def asyncCall[A](lambdaName: String, body: A)(implicit ev: Encoder[A]): Either[LambdaGatewayError, Unit]

  /** Invoke a given lambda on sync mode and decode the result
    *
    * @param lambdaName: lambda function name
    * @param body: content to sent to the invoked lambda
    * @tparam A: content type param
    * @tparam B: result type param
    * @return Right(B) if successfully invoke the lambda
    *         Left(Error) otherwise
    */
  def syncCall[A, B](lambdaName: String, body: A)(implicit
    ev: Encoder[A],
    decoder: Decoder[B]
  ): Either[LambdaGatewayError, B]

}

object LambdaGateway {

  /** Create a default LambdaGateway
    * Automatically create an instance of [[LambdaClient]] that will be used by the LambdaGateway for managing aws lambda.
    * @return Right(LambdaGateway)
    *         Left(LambdaGatewayIniErr)
    */
  def default(): Either[LambdaGatewayError, LambdaGateway]          =
    try {
      lazy val lambdaClient = LambdaClient.builder().build()
      Right(new DefaultLambdaGateway(lambdaClient))
    } catch { case t: Throwable => Left(LambdaGatewayInitErr(t)) }

  /** Create a default LambdaGateway with Audit enabled
    * Automatically create an instance of [[LambdaClient]] that will be used by the LambdaGateway for managing aws lambda.
    * @return Right(LambdaGateway)
    *         Left(LambdaGatewayIniErr)
    */
  def defaultWithAudit(): Either[LambdaGatewayError, LambdaGateway] =
    default().map(new DefaultLambdaGatewayWithAudit(_, Audit.default("LambdaGateway")))

}
