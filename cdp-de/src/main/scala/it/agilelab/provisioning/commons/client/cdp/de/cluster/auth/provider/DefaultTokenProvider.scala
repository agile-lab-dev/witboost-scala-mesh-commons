package it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider

import cats.implicits._
import TokenProviderError.{ ExchangeErr, UnauthorizedErr }
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.http.Auth.{ BasicCredential, BearerToken }
import it.agilelab.provisioning.commons.http.{ Http, HttpErrors }
import it.agilelab.provisioning.commons.http.HttpErrors.ClientErr

/** Default Token Provider implementation
  *
  * provide a method to retrieve a [[BearerToken]]
  */
class DefaultTokenProvider(http: Http) extends TokenProvider {

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
  override def get(
    uri: String,
    credential: BasicCredential
  ): Either[TokenProviderError, BearerToken] =
    http.get[BearerToken](uri, Map.empty, credential).leftMap {
      case e @ ClientErr(401, _) => UnauthorizedErr(e.details)
      case e: HttpErrors         => ExchangeErr(e)
    }
}
