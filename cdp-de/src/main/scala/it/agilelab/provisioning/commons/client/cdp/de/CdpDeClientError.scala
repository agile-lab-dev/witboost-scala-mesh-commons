package it.agilelab.provisioning.commons.client.cdp.de

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.showable.ShowableOps._

sealed trait CdpDeClientError extends Exception with Product with Serializable

object CdpDeClientError {
  final case class CdpDeClientInitError(error: Throwable)                       extends CdpDeClientError
  final case class FindAllServiceErr(error: Throwable)                          extends CdpDeClientError
  final case class DescribeServiceErr(service: String, error: Throwable)        extends CdpDeClientError
  final case class FindAllVcsErr(service: String, error: Throwable)             extends CdpDeClientError
  final case class DescribeVcErr(service: String, vc: String, error: Throwable) extends CdpDeClientError
  final case class ServiceNotFound(serviceName: String)                         extends CdpDeClientError
  final case class VcNotFound(serviceId: String, vcName: String)                extends CdpDeClientError

  implicit val showCdpDeError: Show[CdpDeClientError] = Show.show {
    case e: CdpDeClientInitError => show"CdpDeClientInitErr(${e.error})"
    case e: FindAllServiceErr    => show"FindAllServiceErr(${e.error})"
    case e: DescribeServiceErr   => show"DescribeServiceErr(${e.service},${e.error})"
    case e: FindAllVcsErr        => show"FindAllVcsErr(${e.service},${e.error})"
    case e: DescribeVcErr        => show"DescribeVcErr(${e.service},${e.vc},${e.error})"
    case e: ServiceNotFound      => show"ServiceNotFound(${e.serviceName})"
    case e: VcNotFound           => show"VcNotFound(${e.serviceId},${e.vcName})"
  }
}
