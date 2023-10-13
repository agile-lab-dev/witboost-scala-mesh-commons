package it.agilelab.provisioning.commons.identifier

import it.agilelab.provisioning.commons.datetime.DateTimeProvider

trait IDGenerator {

  /** Generate a random ID
    * @return String random ID
    */
  def random(): String

  /** Generate a random ID Starting from the given value
    * @param value: Starting value from ID generation
    * @return String random ID
    */
  def randomFromStr(value: String): String
}

object IDGenerator {

  /** Create an instance of ID Generator that use UUID to provide unique id generator
    * @return IDGenerator
    */
  def default(): IDGenerator = new DefaultIDGenerator()

  /** Create an instance of ID Generator that use UUID and timestamp to provide unique id generator
    * @return IDGenerator
    */
  def defaultWithTimestamp(): IDGenerator = new DefaultWithTimestampIDGenerator(default(), DateTimeProvider.utc())
}
