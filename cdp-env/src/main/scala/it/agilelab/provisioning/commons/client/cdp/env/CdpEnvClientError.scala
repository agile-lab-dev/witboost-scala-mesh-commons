package it.agilelab.provisioning.commons.client.cdp.env

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.showable.ShowableOps._

sealed trait CdpEnvClientError extends Exception with Product with Serializable

object CdpEnvClientError {
  final case class CdpEnvClientInitError(error: Throwable)                   extends CdpEnvClientError
  final case class DescribeEnvironmentErr(envName: String, error: Throwable) extends CdpEnvClientError
  final case class ListEnvironmentsErr(error: Throwable)                     extends CdpEnvClientError
  final case class SyncAllUsersErr(envName: String, error: Throwable)        extends CdpEnvClientError
  final case class SyncStatusErr(envName: String, error: String)             extends CdpEnvClientError

  implicit val showCdpEnvError: Show[CdpEnvClientError] = Show.show {
    case e: CdpEnvClientInitError  => show"CdpEnvClientErr(${e.error})"
    case e: DescribeEnvironmentErr => show"DescribeEnvironmentErr(${e.envName},${e.error})"
    case e: ListEnvironmentsErr    => show"ListEnvironmentsErr(${e.error})"
    case e: SyncAllUsersErr        => show"SyncAllUsersErr(${e.envName},${e.error})"
    case e: SyncStatusErr          => show"SyncStatusErr(${e.envName},${e.error})"
  }
}
