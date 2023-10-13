package it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.http.HttpErrors
import it.agilelab.provisioning.commons.showable.ShowableOps

/** TokenProviderError
  * A sealed trait for TokenProviderError(s)
  */
sealed trait TokenProviderError extends Exception with Product with Serializable

object TokenProviderError {

  /** Define an error during the initialization of the TokenProvider
    * @param error: Throwable instance that generate this error
    */
  final case class TokenProviderInitErr(error: Throwable) extends TokenProviderError

  /** Exchange unauthorized: The credentials provided in the request are not valid and
    * we receive a forbidden error from the server
    * @param details: Error details
    */
  final case class UnauthorizedErr(details: String) extends TokenProviderError

  /** Exchange error: Server error
    * @param error: HttpError that generate this error
    */
  final case class ExchangeErr(error: HttpErrors) extends TokenProviderError

  implicit val showTokenProviderError: Show[TokenProviderError] = Show.show {
    case e: TokenProviderInitErr => show"TokenProviderInitErr(${ShowableOps.showThrowableError.show(e.error)})"
    case e: UnauthorizedErr      => show"UnauthorizedErr(${e.details})"
    case e: ExchangeErr          => show"ExchangeErr(${e.error})"
  }

}
