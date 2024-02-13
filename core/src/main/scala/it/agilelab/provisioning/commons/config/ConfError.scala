package it.agilelab.provisioning.commons.config

import cats.Show

sealed trait ConfError extends Exception with Product with Serializable

object ConfError {

  final case class ConfKeyNotFoundErr(key: String) extends ConfError
  final case class ConfDecodeErr(error: String)    extends ConfError

  implicit val showConfError: Show[ConfError] = Show.show {
    case e: ConfKeyNotFoundErr => s"ConfKeyNotFoundErr(${e.key})"
    case e: ConfDecodeErr      => s"ConfDecodeErr(${e.error})"
  }
}
