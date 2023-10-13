package it.agilelab.provisioning.commons.http

import io.circe.{ Decoder, Encoder }
import it.agilelab.provisioning.commons.audit.Audit
import sttp.client3.okhttp.OkHttpSyncBackend

/** RestClient trait
  *
  * Provide a generic interface for handling rest api.
  *
  * Take one type parameter that help specific the backed request type.
  * For example in case of syncronuous sttp client you will implement
  * an interface of type HttpClient[RequestT[Empty,Either[String,String],Any]
  */
trait Http {

  /** Execute a get request and try to map the response body as instance of [[A]]
    *
    * @param endpoint: uri endpoint
    * @param decoder: implicit [[Decoder]]
    * @param headers: headers
    * @tparam A type parameter to map the response
    * @return Right(A) if api return 2xx code with a json body that could be converted into A
    *         Left(UnexpectedBodyError) if api return 2xx with a json body that cannot be converted into A
    *         Left(ClientError) if api return 4xx
    *         Left(ServerError) if api return 5xx
    *         Left(GenericError) if api return a code that are not in 2xx,4xx,5xx
    */
  def get[A](endpoint: String, headers: Map[String, String], auth: Auth)(implicit
    decoder: Decoder[A]
  ): Either[HttpErrors, A]

  /** Execute a post request and try to map the response body, if exists to an optional instance of [[B]]
    *
    * Post method optionally can return an object. Some times will return just empty body.
    *
    * @param endpoint: uri endpoint
    * @param body: an instance of the request body
    * @param headers: headers
    * @param encoder: implicit [[Encoder]]
    * @param decoder: implicit [[Decoder]]
    * @tparam A: type parameter of the body
    * @tparam B: type parameter of the expected response
    * @return Right(Some(A)) if api return 2xx code with a json body that could be converted into A
    *         Right(None) if api return 2xx code with empty, blank or null body
    *         Left(UnexpectedBodyError) if api return 2xx with a non empty json body that cannot be converted into A
    *         Left(ClientError) if api return 4xx
    *         Left(ServerError) if api return 5xx
    *         Left(GenericError) if api return a code that are not in 2xx,4xx,5xx
    */
  def post[A, B](endpoint: String, headers: Map[String, String], body: A, auth: Auth)(implicit
    encoder: Encoder[A],
    decoder: Decoder[B]
  ): Either[HttpErrors, Option[B]]

  /** Execute a put request and try to map the response body, if exists to an optional instance of [[B]]
    *
    * Put method optionally can return an object. Some times will return just empty body.
    *
    * @param endpoint: uri endpoint
    * @param body: an instance of the request body
    * @param headers: headers
    * @param encoder: implicit [[Encoder]]
    * @param decoder: implicit [[Decoder]]
    * @tparam A: type parameter of the body
    * @tparam B: type parameter of the expected response
    * @return Right(Some(A)) if api return 2xx code with a json body that could be converted into A
    *         Right(None) if api return 2xx code with empty, blank or null body
    *         Left(UnexpectedBodyError) if api return 2xx with a non empty json body that cannot be converted into A
    *         Left(ClientError) if api return 4xx
    *         Left(ServerError) if api return 5xx
    *         Left(GenericError) if api return a code that are not in 2xx,4xx,5xx
    */
  def put[A, B](endpoint: String, headers: Map[String, String], body: A, auth: Auth)(implicit
    encoder: Encoder[A],
    decoder: Decoder[B]
  ): Either[HttpErrors, Option[B]]

  /** Execute a patch request and try to map the response body, if exists to an optional instance of [[B]]
    *
    * Patch method optionally can return an object. Some times will return just empty body.
    *
    * @param endpoint: uri endpoint
    * @param body: an instance of the request body
    * @param headers: headers
    * @param encoder: implicit [[Encoder]]
    * @param decoder: implicit [[Decoder]]
    * @tparam A: type parameter of the body
    * @tparam B: type parameter of the expected response
    * @return Right(Some(A)) if api return 2xx code with a json body that could be converted into A
    *         Right(None) if api return 2xx code with empty, blank or null body
    *         Left(UnexpectedBodyError) if api return 2xx with a non empty json body that cannot be converted into A
    *         Left(ClientError) if api return 4xx
    *         Left(ServerError) if api return 5xx
    *         Left(GenericError) if api return a code that are not in 2xx,4xx,5xx
    */
  def patch[A, B](endpoint: String, headers: Map[String, String], body: A, auth: Auth)(implicit
    encoder: Encoder[A],
    decoder: Decoder[B]
  ): Either[HttpErrors, Option[B]]

  /** Experimental a put request for upload a file.
    *
    * @param endpoint: uri endpoint
    * @param name: multipart name
    * @param fileName: file name
    * @param contentType: content type to use
    * @param data: Array[Byte] of data
    * @param headers: headers
    * @return
    */
  def putFileMultiPart(
    endpoint: String,
    headers: Map[String, String],
    name: String,
    fileName: String,
    contentType: String,
    data: Array[Byte],
    auth: Auth
  ): Either[HttpErrors, Unit]

  /** Execute a delete request and try to map the response body, if exists to an optional instance of [[A]]
    *
    * Delete method optionally can return an object. Some times will return just empty body.
    *
    * @param endpoint  : uri endpoint
    * @param headers    : headers
    * @param decoder    : implicit [[Decoder]]
    * @tparam A  : type parameter of the expected response
    * @return Right(Some(A)) if api return 2xx code with a json body that could be converted into A
    *         Right(None) if api return 2xx code with empty, blank or null body
    *         Left(UnexpectedBodyError) if api return 2xx with a non empty json body that cannot be converted into A
    *         Left(ClientError) if api return 4xx
    *         Left(ServerError) if api return 5xx
    *         Left(GenericError) if api return a code that are not in 2xx,4xx,5xx
    */
  def delete[A](endpoint: String, headers: Map[String, String], auth: Auth)(implicit
    decoder: Decoder[A]
  ): Either[HttpErrors, Option[A]]
}

object Http {

  /** Create a default Http instance
    *
    * @return
    */
  def default(): Http = new DefaultHttp(OkHttpSyncBackend())

  /** Create a default Http instance with Audit enable
    *
    * @return
    */
  def defaultWithAudit(): Http = new DefaultHttpWithAudit(default(), Audit.default("Http"))

}
