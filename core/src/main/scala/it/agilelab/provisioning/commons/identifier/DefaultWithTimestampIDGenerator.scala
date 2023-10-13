package it.agilelab.provisioning.commons.identifier

import it.agilelab.provisioning.commons.datetime.DateTimeProvider

import java.lang.String._
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter._

/** An instance of ID Generator that attach timestamp to the end of ID
  * @param idGenerator: An ID Generator instance that is used to generate the ID
  * @param dateTimeProvider: A DateTimeProvider instance that is used to retrieve current timestamp
  */
class DefaultWithTimestampIDGenerator(
  idGenerator: IDGenerator,
  dateTimeProvider: DateTimeProvider
) extends IDGenerator {

  private val dateTimeFormatter: DateTimeFormatter = ofPattern("yyyyMMddHHmmssSSSSSS")

  override def random(): String =
    join("_", idGenerator.random(), dateTimeProvider.get().format(dateTimeFormatter))

  override def randomFromStr(value: String): String =
    join("_", idGenerator.randomFromStr(value), dateTimeProvider.get().format(dateTimeFormatter))

}
