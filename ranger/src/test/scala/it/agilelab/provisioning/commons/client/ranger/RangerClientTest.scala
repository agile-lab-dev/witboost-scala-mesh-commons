package it.agilelab.provisioning.commons.client.ranger

import it.agilelab.provisioning.commons.http.Auth.BasicCredential
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class RangerClientTest extends AnyFunSuite with MockFactory {

  test("default") {
    val client = RangerClient.default("x", BasicCredential("x", "y"))
    assert(client.isInstanceOf[RangerClientAdapter])
  }

  test("defaultWithAudit") {
    val client = RangerClient.defaultWithAudit("x", BasicCredential("x", "y"))
    assert(client.isInstanceOf[RangerClientAdapterWithAudit])
  }

  test("defaultWithKerberos") {
    val client = RangerClient.defaultWithKerberos("x", "principal", "keytab/path")
    assert(client.isInstanceOf[RangerClientAdapter])
  }

  test("defaultWithKerberosWithAudit") {
    val client = RangerClient.defaultWithKerberosWithAudit("x", "principal", "keytab/path")
    assert(client.isInstanceOf[RangerClientAdapterWithAudit])
  }

  test("parsing kerberos auth type") {
    assert(RangerAuthType.RangerAuthType.withName("simple") == RangerAuthType.RangerAuthType.Simple)
    assert(RangerAuthType.RangerAuthType.withName("kerberos") == RangerAuthType.RangerAuthType.Kerberos)
    assertThrows[NoSuchElementException](RangerAuthType.RangerAuthType.withName("other"))
  }

}
