package it.agilelab.provisioning.commons.http

import cats.Show
import cats.implicits._
import io.circe.{ Decoder, Encoder }
import it.agilelab.provisioning.commons.audit.Audit

/** Decorate [[DefaultHttp]] with [[Audit]]
  * @param http: a [[Http]] instance
  * @param audit: an [[Audit]] instance
  */
class DefaultHttpWithAudit(http: Http, audit: Audit) extends Http {

  override def get[A](endpoint: String, headers: Map[String, String], auth: Auth)(implicit
    decoder: Decoder[A]
  ): Either[HttpErrors, A] = {
    audit.info(show"Executing http GET request to $endpoint;auth=$auth")
    val result = http.get[A](endpoint, headers, auth)
    auditWithinResult(result, "GET", endpoint)
    result
  }

  override def post[A, B](endpoint: String, headers: Map[String, String], body: A, auth: Auth)(implicit
    encoder: Encoder[A],
    decoder: Decoder[B]
  ): Either[HttpErrors, Option[B]] = {
    implicit val bodyShow: Show[A] = Show.fromToString[A]
    audit.info(show"Executing http POST request to $endpoint;auth=$auth;body=$body")
    val result                     = http.post[A, B](endpoint, headers, body, auth)
    auditWithinResult(result, "POST", endpoint)
    result
  }

  override def put[A, B](endpoint: String, headers: Map[String, String], body: A, auth: Auth)(implicit
    encoder: Encoder[A],
    decoder: Decoder[B]
  ): Either[HttpErrors, Option[B]] = {
    implicit val bodyShow: Show[A] = Show.fromToString[A]
    audit.info(show"Executing http PUT request to $endpoint;auth=$auth;body=$body")
    val result                     = http.put[A, B](endpoint, headers, body, auth)
    auditWithinResult(result, "PUT", endpoint)
    result
  }

  override def patch[A, B](endpoint: String, headers: Map[String, String], body: A, auth: Auth)(implicit
    encoder: Encoder[A],
    decoder: Decoder[B]
  ): Either[HttpErrors, Option[B]] = {
    implicit val bodyShow: Show[A] = Show.fromToString[A]
    audit.info(show"Executing http PATCH request to $endpoint;auth=$auth;body=$body")
    val result                     = http.patch[A, B](endpoint, headers, body, auth)
    auditWithinResult(result, "PATCH", endpoint)
    result
  }

  override def putFileMultiPart(
    endpoint: String,
    headers: Map[String, String],
    name: String,
    fileName: String,
    contentType: String,
    data: Array[Byte],
    auth: Auth
  ): Either[HttpErrors, Unit] = {
    audit.info(
      show"Executing http PUT FILE MULTIPART request to $endpoint;auth=$auth;name=$name;fileName=$fileName;contentType=$contentType"
    )
    val result = http.putFileMultiPart(endpoint, headers, name, fileName, contentType, data, auth)
    auditWithinResult(result, "PUT FILE MULTIPART", endpoint)
    result
  }

  override def delete[A](endpoint: String, headers: Map[String, String], auth: Auth)(implicit
    decoder: Decoder[A]
  ): Either[HttpErrors, Option[A]] = {
    audit.info(show"Executing http DELETE request to $endpoint;auth=$auth")
    val result = http.delete[A](endpoint, headers, auth)
    auditWithinResult(result, "DELETE", endpoint)
    result
  }

  private def auditWithinResult[A](
    result: Either[HttpErrors, A],
    method: String,
    endpoint: String
  ): Unit =
    result match {
      case Right(_) => audit.info(show"Http $method request to $endpoint completed successful")
      case Left(l)  => audit.error(show"Http $method request to $endpoint failed. Details: $l")
    }
}
