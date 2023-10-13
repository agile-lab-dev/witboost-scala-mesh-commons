package it.agilelab.provisioning.commons.client.cdp.de

import cats.implicits._
import com.cloudera.cdp.de.model.{ ServiceDescription, ServiceSummary, VcDescription, VcSummary }
import it.agilelab.provisioning.commons.audit.Audit

class DefaultCdpDeClientWithAudit(cdpDeClient: CdpDeClient, audit: Audit) extends CdpDeClient {

  override def findAllServices(): Either[CdpDeClientError, Seq[ServiceSummary]] = {
    val result = cdpDeClient.findAllServices()
    auditWithinResult(result, "FindAllServices")
    result
  }

  override def findServiceByName(
    serviceName: String
  ): Either[CdpDeClientError, Option[ServiceSummary]] = {
    val result = cdpDeClient.findServiceByName(serviceName)
    auditWithinResult(result, s"FindServiceByName($serviceName)")
    result
  }

  override def describeService(serviceId: String): Either[CdpDeClientError, ServiceDescription] = {
    val result = cdpDeClient.describeService(serviceId)
    auditWithinResult(result, s"DescribeService($serviceId)")
    result
  }

  override def describeServiceByName(
    serviceName: String
  ): Either[CdpDeClientError, ServiceDescription] = {
    val result = cdpDeClient.describeServiceByName(serviceName)
    auditWithinResult(result, s"DescribeServiceByName($serviceName)")
    result
  }

  override def findAllVcs(serviceId: String): Either[CdpDeClientError, Seq[VcSummary]] = {
    val result = cdpDeClient.findAllVcs(serviceId)
    auditWithinResult(result, s"FindAllVcs($serviceId)")
    result
  }

  override def findVcByName(
    serviceId: String,
    vcName: String
  ): Either[CdpDeClientError, Option[VcSummary]] = {
    val result = cdpDeClient.findVcByName(serviceId, vcName)
    auditWithinResult(result, s"FindVcByName($serviceId,$vcName)")
    result
  }

  override def describeVc(
    serviceId: String,
    vcId: String
  ): Either[CdpDeClientError, VcDescription] = {
    val result = cdpDeClient.describeVc(serviceId, vcId)
    auditWithinResult(result, s"DescribeVc($serviceId,$vcId)")
    result
  }

  override def describeVcByName(
    serviceId: String,
    vcName: String
  ): Either[CdpDeClientError, VcDescription] = {
    val result = cdpDeClient.describeVcByName(serviceId, vcName)
    auditWithinResult(result, s"DescribeVcByName($serviceId,$vcName)")
    result
  }

  private def auditWithinResult[A](
    result: Either[CdpDeClientError, A],
    action: String
  ): Unit =
    result match {
      case Right(_) => audit.info(show"$action completed successfully")
      case Left(l)  => audit.error(show"$action failed. Details: $l")
    }
}
