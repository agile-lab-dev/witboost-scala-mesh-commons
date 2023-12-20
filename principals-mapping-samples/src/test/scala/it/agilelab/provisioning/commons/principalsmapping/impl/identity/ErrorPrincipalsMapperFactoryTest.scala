package it.agilelab.provisioning.commons.principalsmapping.impl.identity

import com.typesafe.config.Config
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Success

class ErrorPrincipalsMapperFactoryTest extends AnyFunSuite {
  val factory = new ErrorPrincipalsMapperFactory

  test("identity factory should have the correct identifier") {
    assert(factory.configIdentifier.equalsIgnoreCase("error"))
  }

  test("creating an mapper from config") {
    val config: Config = null
    factory.create(config) match {
      case Success(_: ErrorPrincipalsMapper) => succeed
      case _                                 => fail()
    }
  }
}
