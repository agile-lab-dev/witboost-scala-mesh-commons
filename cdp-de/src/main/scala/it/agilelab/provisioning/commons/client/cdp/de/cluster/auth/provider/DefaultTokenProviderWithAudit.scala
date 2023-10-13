package it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider

import cats.implicits._
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.http.Auth.{ BasicCredential, BearerToken }
import it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider.TokenProviderError._

/** Default Token Provider with Audit implementation
  *
  *  It is a decorator of the [[DefaultTokenProvider]].
  *  Use the [[DefaultTokenProvider]] to execute the logic and wrap
  *  each action with information logs in case of success message and error log in case of error
  * @param tokenProvider: an instance of [[DefaultTokenProvider]]
  * @param audit: an instance of [[Audit]]
  */
class DefaultTokenProviderWithAudit(tokenProvider: TokenProvider, audit: Audit) extends TokenProvider {

  /** Retrieve a BearerToken at the provided endpoint using the provided [[BasicCredential]]
    *
    * It execute the get method of the [[DefaultTokenProvider]] and wrap the action with information logs or error logs.
    * In case of Right response from the [[DefaultTokenProvider.get]] method will logs a successfull message
    * In case of Left response will logs an error messaage with detailed information of the error
    *
    * @param uri: endpoint uri
    * @param credential basic credentials that will be used for exchange token
    * @return Right([[BearerToken]])
    *         Left([[UnauthorizedErr]] if the basic credentials are not authorized to retrieve the token
    *         Left([[ExchangeErr]] if something wrong happen during the exchange process
    */
  override def get(
    uri: String,
    credential: BasicCredential
  ): Either[TokenProviderError, BearerToken] = {
    val result = tokenProvider.get(uri, credential)
    result match {
      case Right(_) => audit.info("Token Exchange completed successfully")
      case Left(l)  => audit.error(show"Token Exchange failed. Details: $l")
    }
    result
  }

}
