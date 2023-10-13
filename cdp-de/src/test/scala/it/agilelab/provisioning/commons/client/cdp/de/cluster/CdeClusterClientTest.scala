package it.agilelab.provisioning.commons.client.cdp.de.cluster

import it.agilelab.provisioning.commons.http.Auth.BasicCredential
import org.scalatest.funsuite.AnyFunSuite

class CdeClusterClientTest extends AnyFunSuite {

  test("default") {
    val actual = CdeClusterClient.default(BasicCredential("x", "y"))
    assert(actual.isRight)
    assert(actual.getOrElse(fail()).isInstanceOf[DefaultCdeClusterClient])
  }

  test("defaultWithAudit") {
    val actual = CdeClusterClient.defaultWithAudit(BasicCredential("x", "y"))
    assert(actual.isRight)
    assert(actual.getOrElse(fail()).isInstanceOf[DefaultCdeClusterClientWithAudit])
  }
}
