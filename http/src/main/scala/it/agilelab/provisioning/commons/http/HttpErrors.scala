package it.agilelab.provisioning.commons.http

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.showable.ShowableOps
import it.agilelab.provisioning.commons.support.ParserError

/** HttpErrors SumType
  */
sealed trait HttpErrors extends Exception with Product with Serializable

object HttpErrors {

  /** ConnectionError
    * @param err: String error details
    */
  final case class ConnectionErr(err: String, throwable: Throwable) extends HttpErrors

  /** ClientError, returned when receive a response with 4xx status code
    * @param code: Specific code
    * @param details: Error details
    */
  final case class ClientErr(code: Int, details: String) extends HttpErrors

  /** ServerError, returned when receive a response with 5xx as a status code
    * @param code: Specific code
    * @param details: Error details
    */
  final case class ServerErr(code: Int, details: String) extends HttpErrors

  /** GenericError, returned  when receive a response with a status code that is not 2xx,4xx or 5xx
    * @param code: Specific code
    * @param details: Error details
    */
  final case class GenericErr(code: Int, details: String) extends HttpErrors

  /** UnexpectedBodyError returned when the response json doesn't match the required type
    * @param body: Body returned
    * @param error: Decode error as string
    */
  final case class UnexpectedBodyErr(body: String, error: ParserError) extends HttpErrors

  implicit val showHttpErrors: Show[HttpErrors] = Show.show {
    case e: ConnectionErr     => show"ConnectionErr(${e.err},${ShowableOps.showThrowableError.show(e.throwable)})"
    case e: ClientErr         => show"ClientErr(${e.code},${e.details})"
    case e: ServerErr         => show"ServerErr(${e.code},${e.details})"
    case e: GenericErr        => show"GenericErr(${e.code},${e.details})"
    case e: UnexpectedBodyErr => show"UnexpectedBodyErr(${e.body},${e.error})"
  }
}
