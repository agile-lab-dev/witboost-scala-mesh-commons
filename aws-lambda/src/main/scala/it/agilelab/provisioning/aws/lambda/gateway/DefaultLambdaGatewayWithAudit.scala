package it.agilelab.provisioning.aws.lambda.gateway

import cats.Show
import cats.implicits._
import io.circe.{ Decoder, Encoder }
import it.agilelab.provisioning.commons.audit.Audit

/** A Default LambdaGateway implementation with Audit
  * @param lambdaGateway: LambdaGateway instance
  * @param audit: Audit instance
  */
class DefaultLambdaGatewayWithAudit(lambdaGateway: LambdaGateway, audit: Audit) extends LambdaGateway {

  override def asyncCall[A](lambdaName: String, body: A)(implicit ev: Encoder[A]): Either[LambdaGatewayError, Unit] = {
    val result = lambdaGateway.asyncCall(lambdaName, body)
    auditWithinResult(result, s"AsyncCall(lambda=$lambdaName,payload=${Show.fromToString[A].show(body)})")
    result
  }

  override def syncCall[A, B](lambdaName: String, body: A)(implicit
    ev: Encoder[A],
    decoder: Decoder[B]
  ): Either[LambdaGatewayError, B] = {
    val result = lambdaGateway.syncCall(lambdaName, body)
    auditWithinResult(result, s"SyncCall(lambda=$lambdaName,payload=${Show.fromToString[A].show(body)})")
    result
  }

  private def auditWithinResult[A](result: Either[LambdaGatewayError, A], action: String): Unit =
    result match {
      case Right(_) => audit.info(show"$action completed successfully")
      case Left(l)  => audit.error(show"$action failed. Details: $l")
    }

}
