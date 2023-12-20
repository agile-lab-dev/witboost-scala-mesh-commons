package it.agilelab.provisioning.commons.principalsmapping

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.showable.ShowableOps._

sealed trait PrincipalsMapperError

final case class ErrorMoreInfo(problems: List[String], solutions: List[String])

object ErrorMoreInfo {
  implicit val showErrorMoreInfo: Show[ErrorMoreInfo] = Show.show { info =>
    s"ErrorMoreInfo(problems = ${info.problems.mkString("[", ",", "]")}, solutions = ${info.solutions.mkString("[", ",", "]")})"
  }
}

object PrincipalsMapperError {

  /** Error thrown by the PrincipalsMapper when a mapping fails.
    *
    * @param error Must include the problem and possibly a solution.
    * @param cause Possible exception that generated the error
    */
  final case class PrincipalMappingError(error: ErrorMoreInfo, cause: Option[Throwable]) extends PrincipalsMapperError
  final case class PrincipalMappingSystemError(error: ErrorMoreInfo, cause: Throwable)   extends PrincipalsMapperError

  implicit val showPrincipalsMapperError: Show[PrincipalsMapperError] = Show.show {
    case e: PrincipalMappingError       => show"PrincipalMappingError(${e.error},${e.cause.fold("")(e => show"$e")})"
    case e: PrincipalMappingSystemError => show"PrincipalMappingSystemError(${e.error},${e.cause})"
  }
}
