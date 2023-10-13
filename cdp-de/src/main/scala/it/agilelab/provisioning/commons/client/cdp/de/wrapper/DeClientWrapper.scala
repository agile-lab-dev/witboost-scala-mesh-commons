package it.agilelab.provisioning.commons.client.cdp.de.wrapper

import com.cloudera.cdp.de.api.DeClient
import com.cloudera.cdp.de.model.{
  DescribeServiceRequest,
  DescribeVcRequest,
  ListServicesRequest,
  ListVcsRequest,
  ServiceDescription,
  ServiceSummary,
  VcDescription,
  VcSummary
}

import scala.jdk.CollectionConverters._

/** A DeClient Wrapper
  *
  * This wrapper was written to workaround the DeClient provided by the CDP SDK for Java
  * The CDP SDK can't be mocked or stubbed and this can create some issue while try to develop some feature.
  *
  * The purpose of this wrapper is just to call the DeClient given as a constructor parameters.
  * The only logic applied on this wrapper is just a conversion from java to scala collection.
  *
  * This can allow us to easily integrate datalakeClient features
  *
  * @param deClient: DeClient
  */
class DeClientWrapper(deClient: DeClient) {

  /** Execute a DeClient.listServices
    * @param req ListServicesRequest
    * @return Seq[ServiceSummary]
    */
  def listServices(req: ListServicesRequest): Seq[ServiceSummary] =
    deClient.listServices(req).getServices.asScala.toSeq

  /** Execute a DeClient.listVcs
    * @param req ListVcsRequest
    * @return Seq[VcSummary]
    */
  def listVcs(req: ListVcsRequest): Seq[VcSummary] =
    deClient.listVcs(req).getVcs.asScala.toSeq

  /** Execute a DeClient.describeService
    * @param req DescribeServiceRequest
    * @return ServiceDescription
    */
  def describeService(req: DescribeServiceRequest): ServiceDescription =
    deClient.describeService(req).getService

  /** Execute a DeClient.describeVc
    * @param req DescribeVcRequest
    * @return VcDescription
    */
  def describeVc(req: DescribeVcRequest): VcDescription =
    deClient.describeVc(req).getVc

}
