package it.agilelab.provisioning.commons.principalsmapping.impl.freeipa

import org.scalatest.funsuite.AnyFunSuite

class FreeIpaIdentityPrincipalsMapperFactoryTest extends AnyFunSuite {
  val factory = new FreeIpaIdentityPrincipalsMapperFactory

  test("free ipa factory should have the correct identifier") {
    assert(factory.configIdentifier.equalsIgnoreCase("freeipa-identity"))
  }
}
