package it.agilelab.provisioning.commons.http

import cats.implicits.toBifunctorOps
import io.circe.{ Decoder, Encoder }
import it.agilelab.provisioning.commons.http.Auth.{ BasicCredential, BearerToken }
import it.agilelab.provisioning.commons.http.HttpErrors._
import it.agilelab.provisioning.commons.support.ParserSupport
import sttp.capabilities
import sttp.client3._
import sttp.client3.circe._
import sttp.model.StatusCode

/** Default Http implementation that make usage of sttp.client3
  * as backend system for executing http call
  * @param backend: A backend instances for sttp client
  */
class DefaultHttp(backend: SttpBackend[Identity, capabilities.WebSockets]) extends Http with ParserSupport {

  override def get[A](endpoint: String, headers: Map[String, String], auth: Auth)(implicit
    decoder: Decoder[A]
  ): Either[HttpErrors, A] =
    execReq(_.get(uri"$endpoint").headers(headers), auth, parseBody[A])

  override def post[A, B](endpoint: String, headers: Map[String, String], body: A, auth: Auth)(implicit
    encoder: Encoder[A],
    decoder: Decoder[B]
  ): Either[HttpErrors, Option[B]] =
    execReq(_.post(uri"$endpoint").headers(headers).body(body), auth, parseOptBody[B])

  override def put[A, B](endpoint: String, headers: Map[String, String], body: A, auth: Auth)(implicit
    encoder: Encoder[A],
    decoder: Decoder[B]
  ): Either[HttpErrors, Option[B]] =
    execReq(_.put(uri"$endpoint").headers(headers).body(body), auth, parseOptBody[B])

  override def patch[A, B](endpoint: String, headers: Map[String, String], body: A, auth: Auth)(implicit
    encoder: Encoder[A],
    decoder: Decoder[B]
  ): Either[HttpErrors, Option[B]] =
    execReq(_.patch(uri"$endpoint").headers(headers).body(body), auth, parseOptBody[B])

  override def putFileMultiPart(
    endpoint: String,
    headers: Map[String, String],
    name: String,
    fileName: String,
    contentType: String,
    data: Array[Byte],
    auth: Auth
  ): Either[HttpErrors, Unit] = {
    val parts = multipart(name, data).fileName(fileName).contentType(contentType)
    execReq(_.put(uri"$endpoint").headers(headers).multipartBody(parts), auth, _ => Right())
  }

  override def delete[A](endpoint: String, headers: Map[String, String], auth: Auth)(implicit
    decoder: Decoder[A]
  ): Either[HttpErrors, Option[A]] =
    execReq(_.delete(uri"$endpoint").headers(headers), auth, parseOptBody[A])

  //Execute a basic request and manage output
  //Right case execute parsing function received as args
  //Left case handle errors
  private def execReq[A](
    req: RequestT[Empty, Either[String, String], Any] => RequestT[
      Identity,
      Either[String, String],
      Any
    ],
    auth: Auth,
    bodyParser: String => Either[HttpErrors, A]
  ): Either[HttpErrors, A] =
    try req(authorize(auth, basicRequest)).send(backend) match {
      case Response(Right(b), _, _, _, _, _) => bodyParser(b)
      case Response(Left(e), c, _, _, _, _)  => handleError(c, e)
    } catch {
      case t: SttpClientException => Left(ConnectionErr(t.getMessage, t.getCause))
      case t: Throwable           => Left(ConnectionErr(t.getMessage, t))
    }

  private def authorize(auth: Auth, req: RequestT[Empty, Either[String, String], Any]) =
    auth match {
      case BasicCredential(username, password)      => req.auth.basic(username, password)
      case BearerToken(access_token, _, _, _, _, _) => req.auth.bearer(access_token)
      case _                                        => req
    }

  private def parseBody[A](body: String)(implicit decoder: Decoder[A]): Either[HttpErrors, A] =
    fromJson[A](body).leftMap(UnexpectedBodyErr(body, _))

  private def parseOptBody[A](
    body: String
  )(implicit decoder: Decoder[A]): Either[HttpErrors, Option[A]] =
    Option(body)
      .map(b => if (b.isEmpty || b.isBlank) Right(None) else parseBody(b).map(Some(_)))
      .getOrElse(Right(None))

  private def handleError(code: StatusCode, error: String): Left[HttpErrors, Nothing] =
    code match {
      case c if c.isClientError => Left(ClientErr(c.code, error))
      case c if c.isServerError => Left(ServerErr(c.code, error))
      case c                    => Left(GenericErr(c.code, error))
    }

}
