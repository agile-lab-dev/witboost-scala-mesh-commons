package it.agilelab.provisioning.commons.client.cdp.dl

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.showable.ShowableOps._

sealed trait CdpDlClientError extends Exception with Product with Serializable

object CdpDlClientError {

  final case class CdpDlClientInitErr(error: Throwable)              extends CdpDlClientError
  final case class FindAllDlErr(error: Throwable)                    extends CdpDlClientError
  final case class DescribeDlErr(datalake: String, error: Throwable) extends CdpDlClientError

  implicit val showCdpDlError: Show[CdpDlClientError] = Show.show {
    case e: CdpDlClientInitErr => show"CdpDlInitErr(${e.error})"
    case e: FindAllDlErr       => show"FindAllDlErr(${e.error})"
    case e: DescribeDlErr      => show"DescribeDlErr(${e.datalake},${e.error})"
  }
}
