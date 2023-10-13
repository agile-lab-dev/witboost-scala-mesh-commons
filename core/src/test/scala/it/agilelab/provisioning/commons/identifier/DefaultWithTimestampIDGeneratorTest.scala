package it.agilelab.provisioning.commons.identifier

import it.agilelab.provisioning.commons.datetime.DateTimeProvider
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

import java.time.ZonedDateTime

class DefaultWithTimestampIDGeneratorTest extends AnyFunSuite with MockFactory {
  val uuidGenerator: IDGenerator         = stub[IDGenerator]
  val dateTimeProvider: DateTimeProvider = stub[DateTimeProvider]

  val idGenerator: IDGenerator = new DefaultWithTimestampIDGenerator(uuidGenerator, dateTimeProvider)

  test("random") {
    val dt = ZonedDateTime.parse("2022-02-02T01:02:03.001241Z")

    (uuidGenerator.random _)
      .when()
      .returns("random-id")
    (dateTimeProvider.get _)
      .when()
      .returns(dt)

    assert(idGenerator.random() == "random-id_20220202010203001241")
  }

  test("randomFromString") {
    val dt = ZonedDateTime.parse("2022-02-02T01:03:03.001241Z")

    (uuidGenerator.randomFromStr _)
      .when("x")
      .returns("random-id-from-str")
    (dateTimeProvider.get _)
      .when()
      .returns(dt)

    assert(idGenerator.randomFromStr("x") == "random-id-from-str_20220202010303001241")
  }
}
