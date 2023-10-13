package it.agilelab.provisioning.commons.identifier

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultIdGeneratorTest extends AnyFunSuite with MockFactory {
  val idGen = new DefaultIDGenerator()

  test("random returns String") {
    val randomString = idGen.random()
    assert(randomString.isInstanceOf[String])
    assert(randomString.nonEmpty)
  }

  test("randomFromStr returns String") {
    val randomString = idGen.randomFromStr("x")
    assert(randomString.isInstanceOf[String])
    assert(randomString.nonEmpty)
  }

}
