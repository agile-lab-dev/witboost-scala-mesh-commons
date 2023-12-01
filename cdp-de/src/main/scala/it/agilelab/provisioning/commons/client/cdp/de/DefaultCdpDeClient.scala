package it.agilelab.provisioning.commons.client.cdp.de

import cats.implicits._
import com.cloudera.cdp.de.model._
import it.agilelab.provisioning.commons.client.cdp.de.CdpDeClientError._
import it.agilelab.provisioning.commons.client.cdp.de.wrapper.DeClientWrapper

/** Default Cdp Data Engineering client implementation
  * @param deClientWrapper: An instance of DeClientWrapper
  */

class DefaultCdpDeClient(deClientWrapper: DeClientWrapper) extends CdpDeClient {

  override def describeServiceByName(
    serviceName: String
  ): Either[CdpDeClientError, ServiceDescription] =
    for {
      optService  <- findServiceByName(serviceName)
      service     <- optService.toRight(ServiceNotFound(serviceName))
      serviceDesc <- describeService(service.getClusterId)
    } yield serviceDesc

  override def findServiceByName(
    serviceName: String
  ): Either[CdpDeClientError, Option[ServiceSummary]] =
    findAllServices().map(_.find(_.getName === serviceName))

  override def findAllServices(): Either[CdpDeClientError, Seq[ServiceSummary]]                 =
    try Right(deClientWrapper.listServices(new ListServicesRequest()))
    catch { case e: Throwable => Left(FindAllServiceErr(e)) }

  override def describeService(serviceId: String): Either[CdpDeClientError, ServiceDescription] =
    try Right(deClientWrapper.describeService(describeServiceReq(serviceId)))
    catch { case e: Throwable => Left(DescribeServiceErr(serviceId, e)) }

  override def describeVcByName(
    serviceId: String,
    vcName: String
  ): Either[CdpDeClientError, VcDescription]                                                    =
    for {
      optVc  <- findVcByName(serviceId, vcName)
      vc     <- optVc.toRight(VcNotFound(serviceId, vcName))
      vcDesc <- describeVc(serviceId, vc.getVcId)
    } yield vcDesc

  override def findVcByName(
    serviceId: String,
    vcName: String
  ): Either[CdpDeClientError, Option[VcSummary]] =
    findAllVcs(serviceId).map(_.find(_.getVcName === vcName))

  override def findAllVcs(serviceId: String): Either[CdpDeClientError, Seq[VcSummary]] =
    try Right(deClientWrapper.listVcs(listVcsReq(serviceId)))
    catch { case e: Throwable => Left(FindAllVcsErr(serviceId, e)) }

  override def describeVc(
    serviceId: String,
    vcId: String
  ): Either[CdpDeClientError, VcDescription]                                           =
    try Right(deClientWrapper.describeVc(describeVcReq(serviceId, vcId)))
    catch { case e: Throwable => Left(DescribeVcErr(serviceId, vcId, e)) }

  private def listVcsReq(serviceId: String): ListVcsRequest = {
    val req = new ListVcsRequest()
    req.setClusterId(serviceId)
    req
  }

  private def describeServiceReq(serviceId: String): DescribeServiceRequest = {
    val req = new DescribeServiceRequest()
    req.setClusterId(serviceId)
    req
  }

  private def describeVcReq(serviceId: String, vcId: String): DescribeVcRequest = {
    val req = new DescribeVcRequest()
    req.setClusterId(serviceId)
    req.setVcId(vcId)
    req
  }

}
