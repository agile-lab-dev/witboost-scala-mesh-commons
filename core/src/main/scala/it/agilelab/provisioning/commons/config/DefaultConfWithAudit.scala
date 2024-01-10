package it.agilelab.provisioning.commons.config

import cats.implicits._
import com.typesafe.config.Config
import it.agilelab.provisioning.commons.audit.Audit

/** Default Conf with Auditing enable Instance.
  *
  * A Decorator pattern that wrap any Conf method with audit action.
  *
  * @param conf: an instance of [[Conf]]
  * @param audit: an instance of [[Audit]]
  */
class DefaultConfWithAudit(conf: Conf, audit: Audit) extends Conf {

  override def get(key: String): Either[ConfError, String] = {
    audit.info(show"Retrieving config: $key")
    val result = conf.get(key)
    result match {
      case Right(_) => audit.info(show"Config $key retrieved successfully")
      case Left(e)  => audit.error(show"Config $key retrieve failed. Details: $e")
    }
    result
  }

  override def getConfig(key: String): Either[ConfError, Config] = {
    audit.info(show"Retrieving config object: $key")
    val result = conf.getConfig(key)
    result match {
      case Right(_) => audit.info(show"Config object $key retrieved successfully")
      case Left(e)  => audit.error(show"Config object $key retrieve failed. Details: $e")
    }
    result
  }
}
