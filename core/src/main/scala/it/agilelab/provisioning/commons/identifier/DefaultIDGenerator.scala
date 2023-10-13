package it.agilelab.provisioning.commons.identifier

import java.nio.charset.StandardCharsets._
import java.util.UUID

/** An instance of IDGenerator that use UUID to provide unique ID
  */
class DefaultIDGenerator extends IDGenerator {
  override def random(): String                     = UUID.randomUUID().toString
  override def randomFromStr(value: String): String = UUID.nameUUIDFromBytes(value.getBytes(UTF_8)).toString
}
