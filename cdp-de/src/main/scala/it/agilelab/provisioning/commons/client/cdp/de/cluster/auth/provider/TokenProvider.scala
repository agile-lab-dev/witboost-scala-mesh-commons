package it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider.TokenProviderError.TokenProviderInitErr
import it.agilelab.provisioning.commons.http.Auth.{ BasicCredential, BearerToken }
import it.agilelab.provisioning.commons.http.Http

/** Token provider trait
  *
  * provide a method to retrieve a [[BearerToken]]
  */
trait TokenProvider {

  /** Retrieve a BearerToken at the provided endpoint using the provided [[BasicCredential]]
    *
    * It execute an http get method agains the endpoint with the provided credentials as auth method
    *
    * @param uri: endpoint uri
    * @param credential basic credentials that will be used for exchange token
    * @return Right([[BearerToken]])
    *         Left([[UnauthorizedErr]] if the basic credentials are not authorized to retrieve the token
    *         Left([[ExchangeErr]] if something wrong happen during the exchange process
    */
  def get(uri: String, credential: BasicCredential): Either[TokenProviderError, BearerToken]

}

object TokenProvider {

  /** Create a [[DefaultTokenProvider]]
    *
    * Generate a default implementation for the [[TokenProvider]] trait
    * @return [[TokenProvider]]
    */
  def default(): Either[TokenProviderInitErr, TokenProvider]          =
    try Right(new DefaultTokenProvider(Http.default()))
    catch { case t: Throwable => Left(TokenProviderInitErr(t)) }

  /** Create a [[DefaultTokenProviderWithAudit]]
    *
    * Generate a default implementation with audit enabled for the [[TokenProvider]] trait
    * @return [[TokenProvider]]
    */
  def defaultWithAudit(): Either[TokenProviderInitErr, TokenProvider] =
    try {
      val tokenProvider = new DefaultTokenProvider(Http.defaultWithAudit())
      Right(new DefaultTokenProviderWithAudit(tokenProvider, Audit.default("TokenProvider")))
    } catch { case t: Throwable => Left(TokenProviderInitErr(t)) }

}
