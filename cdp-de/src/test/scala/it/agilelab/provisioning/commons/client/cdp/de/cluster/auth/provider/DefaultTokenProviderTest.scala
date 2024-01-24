package it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider

import io.circe.Decoder
import it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider.TokenProviderError._
import it.agilelab.provisioning.commons.http.Auth.{ BasicCredential, BearerToken }
import it.agilelab.provisioning.commons.http.Http
import it.agilelab.provisioning.commons.http.HttpErrors.{ ClientErr, GenericErr, ServerErr }
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

class DefaultTokenProviderTest extends AnyFunSuite with MockFactory with BeforeAndAfterAll {

  val http: Http    = stub[Http]
  val tokenProvider = new DefaultTokenProvider(http)

  test("get return Right(BearerToken)") {
    (http
      .get[BearerToken](_: String, _: Map[String, String], _: BasicCredential)(_: Decoder[BearerToken]))
      .when("endpoint", *, BasicCredential("x", "y"), *)
      .returns(Right(BearerToken("token", "id", "a", "b", "c", 1L)))

    val actual   = tokenProvider.get("endpoint", BasicCredential("x", "y"))
    val expected = Right(BearerToken("token", "id", "a", "b", "c", 1L))
    assert(actual == expected)
  }

  test("get return Left(UnauthorizedErr) with ClientError(401,_)") {
    (http
      .get[BearerToken](_: String, _: Map[String, String], _: BasicCredential)(_: Decoder[BearerToken]))
      .when("endpoint", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(401, "no auth")))

    val actual   = tokenProvider.get("endpoint", BasicCredential("x", "y"))
    val expected = Left(UnauthorizedErr("no auth"))
    assert(actual == expected)
  }

  Seq(
    ClientErr(405, "It's a client error"),
    ServerErr(505, "It's a server error"),
    GenericErr(301, "It's a client error")
  ) foreach { httpErr =>
    test(s"get return Left(ExchangeErr) with $httpErr") {
      (http
        .get[BearerToken](_: String, _: Map[String, String], _: BasicCredential)(_: Decoder[BearerToken]))
        .when("endpoint", *, BasicCredential("x", "y"), *)
        .returns(Left(httpErr))

      val actual   = tokenProvider.get("endpoint", BasicCredential("x", "y"))
      val expected = Left(ExchangeErr(httpErr))
      assert(actual == expected)
    }
  }

}
