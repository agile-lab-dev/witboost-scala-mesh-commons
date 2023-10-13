package it.agilelab.provisioning.commons.datetime

import java.time.ZonedDateTime.{ of => _, _ }
import java.time.{ Clock, ZoneId, ZonedDateTime }

class DefaultDateTimeProvider(clock: Clock) extends DateTimeProvider {

  def get(): ZonedDateTime =
    now(clock.withZone(ZoneId.of("UTC")))

}
