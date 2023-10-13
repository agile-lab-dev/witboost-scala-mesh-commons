package it.agilelab.provisioning.aws.lambda.gateway

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.showable.ShowableOps
import it.agilelab.provisioning.commons.support.ParserError

/** LambdaGatewayError
  * A sealed trait for LambdaGatewayError(s)
  */
sealed trait LambdaGatewayError extends Exception with Product with Serializable

object LambdaGatewayError {

  /** LambdaGatewayInitErr
    * @param error: A Throwable instance that generate this error
    */
  final case class LambdaGatewayInitErr(
    error: Throwable
  ) extends LambdaGatewayError

  /** PayloadSerErr
    * Define a payload serialization error
    * @param error: ParserError that generate this error
    */
  final case class PayloadSerErr(
    error: ParserError
  ) extends LambdaGatewayError

  /** InvokeErr
    * Define an Invocation error
    * @param lambdaName: lambda name used on invocation
    * @param payload: payload used on invocation
    * @param error: Throwable instance that generate this error
    */
  final case class InvokeErr(
    lambdaName: String,
    payload: String,
    error: Throwable
  ) extends LambdaGatewayError

  /** InvokeResultErr
    * Define an Invocation result error
    * @param lambdaName: Lambda name used on invocation
    * @param payload: payload used on invocation
    * @param status: status code returned during invocation
    */
  final case class InvokeResultErr(
    lambdaName: String,
    payload: String,
    status: Int
  ) extends LambdaGatewayError

  /** PayloadDeserErr
    * Define a payload deserialization error
    * @param error: ParserError that generate this error
    */
  final case class PayloadDeserErr(
    error: ParserError
  ) extends LambdaGatewayError

  /** Implicit Show definition for LambdaGatewayError instances
    */
  implicit val showLambdaGatewayError: Show[LambdaGatewayError] = Show.show {
    case e: LambdaGatewayInitErr => show"LambdaGatewayInitErr(${ShowableOps.showThrowableError.show(e.error)})"
    case e: PayloadSerErr        => show"PayloadSerErr(${e.error})"
    case e: InvokeResultErr      => show"InvokeStatusErr(${e.lambdaName},${e.payload},${e.status})"
    case e: InvokeErr            => show"InvokeErr(${e.lambdaName},${e.payload},${ShowableOps.showThrowableError.show(e.error)})"
    case e: PayloadDeserErr      => show"PayloadDeserErr(${e.error})"
  }
}
