package it.agilelab.provisioning.commons.client.cdp.dw

import com.cloudera.cdp.dw.model.{ ClusterSummaryResponse, VwSummary }
import cats.implicits._
import it.agilelab.provisioning.commons.audit.Audit

class DefaultCdpDwClientWithAudit(cdpDwClient: CdpDwClient, audit: Audit) extends CdpDwClient {

  override def findAllClusters(): Either[CdpDwClientError, Seq[ClusterSummaryResponse]] = {
    val result = cdpDwClient.findAllClusters()
    auditWithinResult(result, "FindAllClusters")
    result
  }

  override def findClusterByEnvironmentCrn(
    environmentCrn: String
  ): Either[CdpDwClientError, Option[ClusterSummaryResponse]] = {
    val result = cdpDwClient.findClusterByEnvironmentCrn(environmentCrn)
    auditWithinResult(result, s"FindClusterByEnvironmentCrn($environmentCrn)")
    result
  }

  override def findAllVw(clusterId: String): Either[CdpDwClientError, Seq[VwSummary]] = {
    val result = cdpDwClient.findAllVw(clusterId)
    auditWithinResult(result, s"FindAllVw($clusterId)")
    result
  }

  override def findVwByName(
    clusterId: String,
    vwName: String
  ): Either[CdpDwClientError, Option[VwSummary]] = {
    val result = cdpDwClient.findVwByName(clusterId, vwName)
    auditWithinResult(result, s"FindVwByName($clusterId,$vwName)")
    result
  }

  private def auditWithinResult[A](
    result: Either[CdpDwClientError, A],
    action: String
  ): Unit =
    result match {
      case Right(_) => audit.info(show"$action completed successfully")
      case Left(l)  => audit.error(show"$action failed. Details: $l")
    }

}
