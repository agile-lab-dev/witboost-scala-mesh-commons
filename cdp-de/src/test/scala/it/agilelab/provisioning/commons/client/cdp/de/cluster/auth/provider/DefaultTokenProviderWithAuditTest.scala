package it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider.TokenProviderError.{
  ExchangeErr,
  UnauthorizedErr
}
import it.agilelab.provisioning.commons.http.Auth.{ BasicCredential, BearerToken }
import it.agilelab.provisioning.commons.http.HttpErrors.ServerErr
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

class DefaultTokenProviderWithAuditTest extends AnyFunSuite with MockFactory with BeforeAndAfterAll {

  val defaultTokenProvider: DefaultTokenProvider = stub[DefaultTokenProvider]
  val audit: Audit                               = mock[Audit]
  val tokenProvider                              = new DefaultTokenProviderWithAudit(defaultTokenProvider, audit)

  test("get return Right(BearerToken) and call audit") {
    (defaultTokenProvider.get _)
      .when(*, *)
      .returns(Right(BearerToken("token", "id", "a", "b", "c", 1L, "p")))

    (audit.info _).expects("Token Exchange completed successfully").once()

    val actual   = tokenProvider.get("endpoint", BasicCredential("x", "y"))
    val expected = Right(BearerToken("token", "id", "a", "b", "c", 1L, "p"))
    assert(actual == expected)
  }

  test("get return Left(ExchangeUnauthorizedErr) with ClientError(401,_) and call audit") {
    (defaultTokenProvider.get _)
      .when(*, *)
      .returns(Left(UnauthorizedErr("x")))

    (audit.error _)
      .expects("Token Exchange failed. Details: UnauthorizedErr(x)")
      .once()

    val actual   = tokenProvider.get("endpoint", BasicCredential("x", "y"))
    val expected = Left(UnauthorizedErr("x"))
    assert(actual == expected)
  }

  test("get return Left(ExchangeErr) with ClientError and call audit") {
    (defaultTokenProvider.get _)
      .when(*, *)
      .returns(Left(ExchangeErr(ServerErr(505, "x"))))

    (audit.error _)
      .expects("Token Exchange failed. Details: ExchangeErr(ServerErr(505,x))")
      .once()

    val actual   = tokenProvider.get("endpoint", BasicCredential("x", "y"))
    val expected = Left(ExchangeErr(ServerErr(505, "x")))
    assert(actual == expected)
  }

}
