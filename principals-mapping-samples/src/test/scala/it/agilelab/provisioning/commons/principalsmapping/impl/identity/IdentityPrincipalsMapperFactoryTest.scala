package it.agilelab.provisioning.commons.principalsmapping.impl.identity

import com.typesafe.config.Config
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Success

class IdentityPrincipalsMapperFactoryTest extends AnyFunSuite {
  val factory = new IdentityPrincipalsMapperFactory

  test("identity factory should have the correct identifier") {
    assert(factory.configIdentifier.equalsIgnoreCase("identity"))
  }

  test("creating an mapper from config") {
    val config: Config = null
    factory.create(config) match {
      case Success(_: IdentityPrincipalsMapper) => succeed
      case _                                    => fail()
    }
  }
}
