package it.agilelab.provisioning.commons.datetime

import org.scalatest.funsuite.AnyFunSuite

import java.time.Clock._
import java.time.Instant._
import java.time.{ ZoneId, ZonedDateTime }

class DefaultDateTimeProviderTest extends AnyFunSuite {

  test("get return fixed zoned date time") {
    val clock = fixed(parse("2022-03-10T08:35:25.000001Z"), ZoneId.of("UTC"))
    assert(new DefaultDateTimeProvider(clock).get() == ZonedDateTime.parse("2022-03-10T08:35:25.000001Z[UTC]"))
  }

}
