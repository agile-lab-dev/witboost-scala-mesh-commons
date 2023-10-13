package it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class TokenProviderTest extends AnyFunSuite with MockFactory {

  test("bearer") {
    val actual = TokenProvider.default()
    assert(actual.isRight)
    assert(actual.getOrElse(fail()).isInstanceOf[DefaultTokenProvider])
  }

  test("bearerWithAudit") {
    val actual = TokenProvider.defaultWithAudit()
    assert(actual.isRight)
    assert(actual.getOrElse(fail()).isInstanceOf[DefaultTokenProviderWithAudit])
  }

}
