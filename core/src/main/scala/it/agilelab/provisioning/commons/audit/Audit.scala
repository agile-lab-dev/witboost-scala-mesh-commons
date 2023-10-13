package it.agilelab.provisioning.commons.audit

import com.typesafe.scalalogging.Logger

/** Audit trait
  *
  * Provide useful method to manage logging
  */
trait Audit {

  /** Info message on logging system
    * @param message string message value
    */
  def info(message: String): Unit

  /** Error message on logging system
    * @param message string message value
    */
  def error(message: String): Unit

  /** Warning message on logging system
    * @param message string message value
    */
  def warning(message: String): Unit

}

object Audit {

  /** Create a [[DefaultAudit]] Instance
    * @param name: logger name
    * @return [[Audit]]
    */
  def default(name: String): Audit =
    new DefaultAudit(Logger(name))

}
