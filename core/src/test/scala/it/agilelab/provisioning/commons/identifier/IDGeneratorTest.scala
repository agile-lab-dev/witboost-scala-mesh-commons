package it.agilelab.provisioning.commons.identifier

import org.scalatest.funsuite.AnyFunSuite

class IDGeneratorTest extends AnyFunSuite {

  test("default") {
    assert(IDGenerator.default().isInstanceOf[DefaultIDGenerator])
  }

  test("defaultWithTimestamp") {
    assert(IDGenerator.defaultWithTimestamp().isInstanceOf[DefaultWithTimestampIDGenerator])
  }
}
