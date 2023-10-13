package it.agilelab.provisioning.commons.client.cdp.dw

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.showable.ShowableOps._

sealed trait CdpDwClientError extends Exception with Product with Serializable

object CdpDwClientError {
  final case class CdpDwClientInitClientError(error: Throwable)     extends CdpDwClientError
  final case class FindAllClustersErr(error: Throwable)             extends CdpDwClientError
  final case class FindAllVwsErr(cluster: String, error: Throwable) extends CdpDwClientError

  implicit val showCdpDwError: Show[CdpDwClientError] = Show.show {
    case e: CdpDwClientInitClientError => show"CdpDwClientError(${e.error})"
    case e: FindAllClustersErr         => show"FindAllClustersErr(${e.error})"
    case e: FindAllVwsErr              => show"FindAllVwsErr(${e.cluster},${e.error})"
  }
}
