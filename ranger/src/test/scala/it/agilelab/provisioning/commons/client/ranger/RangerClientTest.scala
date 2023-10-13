package it.agilelab.provisioning.commons.client.ranger

import it.agilelab.provisioning.commons.http.Auth.BasicCredential
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class RangerClientTest extends AnyFunSuite with MockFactory {

  test("default") {
    val client = RangerClient.default("x", BasicCredential("x", "y"))
    assert(client.isInstanceOf[DefaultRangerClient])
  }

  test("defaultWithAudit") {
    val client = RangerClient.defaultWithAudit("x", BasicCredential("x", "y"))
    assert(client.isInstanceOf[DefaultRangerClientWithAudit])
  }

}
