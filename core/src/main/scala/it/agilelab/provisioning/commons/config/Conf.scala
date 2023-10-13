package it.agilelab.provisioning.commons.config

import com.typesafe.config.ConfigFactory
import it.agilelab.provisioning.commons.audit.Audit

/** Conf trait
  *
  * provide useful method to retrieve configuration
  */
trait Conf {

  /** Retrieve a value from a specific key
    * @param key: Configuration key
    * @return String
    */
  def get(key: String): Either[ConfError, String]
}

object Conf {

  /** Create Conf based on environment variables
    * @return [[Conf]]
    */
  def env(): Conf = new DefaultConf(ConfigFactory.systemEnvironment())

  /** Create Conf based on environment variables with Audit enabled
    * @return [[Conf]]
    */
  def envWithAudit(): Conf = new DefaultConfWithAudit(env(), Audit.default("Conf"))
}
