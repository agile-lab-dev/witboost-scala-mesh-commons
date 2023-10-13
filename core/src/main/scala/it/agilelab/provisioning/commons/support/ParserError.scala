package it.agilelab.provisioning.commons.support

import cats.Show
import cats.implicits._
import io.circe.Error
import it.agilelab.provisioning.commons.showable.ShowableOps

/** ParserError
  */
sealed trait ParserError extends Exception with Product with Serializable

object ParserError {

  /** Decode error
    * @param error: a [[String]] error explanation
    */
  final case class DecodeErr(error: Error) extends ParserError

  /** Encode error
    * @param throwable: a [[Throwable]] instance that generate this error
    */
  final case class EncodeErr(throwable: Throwable) extends ParserError

  implicit val showParserError: Show[ParserError] = Show.show {
    case e: DecodeErr => show"DecodeErr(${e.error})"
    case e: EncodeErr => show"EncodeErr(${ShowableOps.showThrowableError.show(e.throwable)})"
  }

}
