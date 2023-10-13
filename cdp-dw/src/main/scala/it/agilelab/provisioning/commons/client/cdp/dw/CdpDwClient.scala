package it.agilelab.provisioning.commons.client.cdp.dw

import com.cloudera.cdp.dw.api.DwClientBuilder
import com.cloudera.cdp.dw.model.{ ClusterSummary, VwSummary }
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.dw.CdpDwClientError.CdpDwClientInitClientError
import it.agilelab.provisioning.commons.client.cdp.dw.wrapper.DwClientWrapper

/** CdpDeClient trait
  */
trait CdpDwClient {

  /** Retrieve all DWX Cluster
    * @return Right(Seq[ClusterSummary]) if all works fine
    *         Left(CdpDwClientError) otherwise
    */
  def findAllClusters(): Either[CdpDwClientError, Seq[ClusterSummary]]

  /** Retrieve a DWX Cluster by name
    * @param crnEnvironment: environment CRN
    * @return Right(Option[ClusterSummary]) if all works fin
    *         Left(CdpDwClientError) otherwise
    */
  def findClusterByEnvironmentCrn(crnEnvironment: String): Either[CdpDwClientError, Option[ClusterSummary]]

  /** Retrieve all vw of a specific DWX Cluster
    * @param clusterId: DWX Cluster id
    * @return Right(Seq[VwSummary]) if all works fine
    *         Left(CdpDwClientError) otherwise
    */
  def findAllVw(clusterId: String): Either[CdpDwClientError, Seq[VwSummary]]

  /** Retrieve a specific vw by name
    * @param clusterId: DWX ClusterId
    * @param vwName: Virtual Warehouse name
    * @return Right(Option[VwSummary]) if all works fine
    *         Left(CdpDwClientError) otherwise
    */
  def findVwByName(clusterId: String, vwName: String): Either[CdpDwClientError, Option[VwSummary]]
}

object CdpDwClient {

  def default(): Either[CdpDwClientError, CdpDwClient]          =
    try {
      val dwClient = DwClientBuilder.defaultBuilder().build()
      Right(new DefaultCdpDwClient(new DwClientWrapper(dwClient)))
    } catch { case t: Throwable => Left(CdpDwClientInitClientError(t)) }

  def defaultWithAudit(): Either[CdpDwClientError, CdpDwClient] =
    default().map(new DefaultCdpDwClientWithAudit(_, Audit.default("CdpDwClient")))
}
