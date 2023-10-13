package it.agilelab.provisioning.commons.audit

import com.typesafe.scalalogging.Logger

/** Default Audit Instance that use typesafe Logger as underlying logger interface
  * @param logger: an instance of [[Logger]]
  */
class DefaultAudit(logger: Logger) extends Audit {

  override def info(message: String): Unit =
    logger.info(message)

  override def error(message: String): Unit =
    logger.error(message)

  override def warning(message: String): Unit =
    logger.warn(message)

}
