package it.agilelab.provisioning.commons.validator

import cats.Show
import cats.implicits._
import io.circe.{ Encoder, Json }
import it.agilelab.provisioning.commons.showable.ShowableOps._

final case class ValidatorError[A](entity: A, throwable: Throwable) extends Exception

object ValidatorError {

  implicit def showValidatorError[A]: Show[ValidatorError[A]] =
    Show.show(e => show"ValidatorError(${e.entity.toString},${e.throwable})")

}
