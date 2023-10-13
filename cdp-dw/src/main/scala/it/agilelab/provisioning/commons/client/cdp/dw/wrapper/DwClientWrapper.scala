package it.agilelab.provisioning.commons.client.cdp.dw.wrapper

import com.cloudera.cdp.dw.api.DwClient
import com.cloudera.cdp.dw.model.{
  ClusterSummary,
  DescribeClusterRequest,
  ListClustersRequest,
  ListVwsRequest,
  VwSummary
}

import scala.jdk.CollectionConverters._

/** A DwClientWrapper
  *
  * This wrapper was written to workaround the DwClientWrapper provided by the CDP SDK for Java
  * The CDP SDK can't be mocked or stubbed and this can create some issue while try to develop some feature.
  *
  * The purpose of this wrapper is just to call the DwClient given as a constructor parameters.
  * The only logic applied on this wrapper is just a conversion from java to scala collection.
  *
  * This can allow us to easily integrate dwClient features
  *
  * @param dwClient: DatalakeClient
  */
class DwClientWrapper(dwClient: DwClient) {

  /** List all Data Warehouse clusters
    * @return Seq[ClusterSummary]
    */
  def listClusters(req: ListClustersRequest): Seq[ClusterSummary] =
    dwClient.listClusters(req).getClusters.asScala.toSeq

  /** List all Data Warehouse clusters
    * @return Seq[ClusterSummary]
    */
  def listVws(clusterId: String): Seq[VwSummary] =
    dwClient.listVws(listVwsReq(clusterId)).getVws.asScala.toSeq

  private def listVwsReq(clusterId: String): ListVwsRequest = {
    val req = new ListVwsRequest()
    req.setClusterId(clusterId)
    req
  }

  /** Describe a specific data warehouse cluster
    * @param clusterId: a data warehouse clsuter id
    * @return ClusterSummary
    */
  def describeCluster(clusterId: String): ClusterSummary =
    dwClient.describeCluster(describeClusterReq(clusterId)).getCluster

  private def describeClusterReq(clusterId: String): DescribeClusterRequest = {
    val req = new DescribeClusterRequest()
    req.setClusterId(clusterId)
    req
  }

}
