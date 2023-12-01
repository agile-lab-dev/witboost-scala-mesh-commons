package it.agilelab.provisioning.commons.client.cdp.dw

import cats.implicits.catsSyntaxEq
import com.cloudera.cdp.dw.model.{ ClusterSummaryResponse, ListClustersRequest, VwSummary }

import it.agilelab.provisioning.commons.client.cdp.dw.CdpDwClientError._
import it.agilelab.provisioning.commons.client.cdp.dw.wrapper.DwClientWrapper

class DefaultCdpDwClient(
  dwClientWrapper: DwClientWrapper
) extends CdpDwClient {

  override def findClusterByEnvironmentCrn(
    crnEnvironment: String
  ): Either[CdpDwClientError, Option[ClusterSummaryResponse]] =
    for {
      clusters <- findAllClusters()
    } yield clusters.find(_.getEnvironmentCrn === crnEnvironment)

  override def findAllClusters(): Either[CdpDwClientError, Seq[ClusterSummaryResponse]] =
    try Right(dwClientWrapper.listClusters(new ListClustersRequest()))
    catch { case e: Throwable => Left(FindAllClustersErr(e)) }

  override def findVwByName(
    clusterId: String,
    vwName: String
  ): Either[CdpDwClientError, Option[VwSummary]]                                        =
    findAllVw(clusterId).map(_.find(_.getName === vwName))

  override def findAllVw(clusterId: String): Either[CdpDwClientError, Seq[VwSummary]] =
    try Right(dwClientWrapper.listVws(clusterId))
    catch { case e: Throwable => Left(FindAllVwsErr(clusterId, e)) }
}
