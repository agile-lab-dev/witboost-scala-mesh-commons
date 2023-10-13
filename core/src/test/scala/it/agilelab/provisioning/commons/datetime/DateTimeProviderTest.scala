package it.agilelab.provisioning.commons.datetime

import org.scalatest.funsuite.AnyFunSuite

class DateTimeProviderTest extends AnyFunSuite {

  test("utc") {
    val actual = DateTimeProvider.utc()
    assert(actual.isInstanceOf[DateTimeProvider])
  }
}
