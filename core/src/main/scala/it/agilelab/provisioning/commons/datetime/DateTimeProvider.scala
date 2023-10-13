package it.agilelab.provisioning.commons.datetime

import java.time.{ Clock, ZonedDateTime }

/** DateTimeProvider
  */
trait DateTimeProvider {

  /** Get a ZonedDateTime
    * @return
    */
  def get(): ZonedDateTime
}

object DateTimeProvider {

  /** Create an UTC DateTimeProvider
    * @return
    */
  def utc(): DateTimeProvider = new DefaultDateTimeProvider(Clock.systemUTC())
}
