package it.agilelab.provisioning.commons.client.cdp.de

import com.cloudera.cdp.de.api.DeClientBuilder
import com.cloudera.cdp.de.model.{ ServiceDescription, ServiceSummary, VcDescription, VcSummary }
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.de.CdpDeClientError.CdpDeClientInitError
import it.agilelab.provisioning.commons.client.cdp.de.wrapper.DeClientWrapper

/** CdpDeClient trait
  */
trait CdpDeClient {

  /** Retrieve all CDE Services
    * @return Right(Seq[ServiceSumary]) if find all services completes successful
    *         Left[Error] otherwise
    */
  def findAllServices(): Either[CdpDeClientError, Seq[ServiceSummary]]

  /** Retrive a specific CDE service given a service name
    * @param serviceName the service name that you want to retrive
    * @return Right(Some(ServiceSummary)) if service was found
    *         Right(None) if service does not exists
    *         Left(Error) otherwise
    */
  def findServiceByName(serviceName: String): Either[CdpDeClientError, Option[ServiceSummary]]

  /** Describe a specific CDE service given their serviceId
    * @param serviceId a serviceId
    * @return Right(ServiceDescription) if describe service are successful
    *         Left(Error) otherwise
    */
  def describeService(serviceId: String): Either[CdpDeClientError, ServiceDescription]

  /** Describe a specific CDE service given their service name
    * @param serviceName a service name
    * @return Right(ServiceDescription) if describe service are successful
    *         Left(Error) otherwise
    */
  def describeServiceByName(serviceName: String): Either[CdpDeClientError, ServiceDescription]

  /** Retrieve all virtual cluster within a specific serviceId
    * @param serviceId the serviceId
    * @return Right(VcSummary) if successful find all vc within the service
    *         Left(Error) otherwise
    */
  def findAllVcs(serviceId: String): Either[CdpDeClientError, Seq[VcSummary]]

  /** Retrieve a specific virtual cluster by virtual cluster name within a specific serviceId
    * @param serviceId the serviceId
    * @param vcName the virtual cluster name
    * @return Right(Some(VcSummary)) if virtual cluster exists within service
    *         Right(None) if virtual cluster does not exists
    *         Left(Error) otherwise
    */
  def findVcByName(serviceId: String, vcName: String): Either[CdpDeClientError, Option[VcSummary]]

  /** Describe a specific virtual cluster
    * @param serviceId serviceId
    * @param vcId virtual cluster id
    * @return Right(VcDescription) if describe virtual cluster successful
    *         Left(Error) otherwise
    */
  def describeVc(serviceId: String, vcId: String): Either[CdpDeClientError, VcDescription]

  /** Describe a specific virtual cluster by vcName
    * @param serviceId service Id
    * @param vcName vc name
    * @return Right(VcDescription) if describe virtual cluster successful
    *         Left(Error) otherwise
    */
  def describeVcByName(serviceId: String, vcName: String): Either[CdpDeClientError, VcDescription]
}

/** CdpDeClient comanion object
  */
object CdpDeClient {

  /** Create a [[DefaultCdpDeClient]]
    * @return Right(CdpDeClient)
    *         Left(CdpDeClientError)
    */
  def default(): Either[CdpDeClientError, CdpDeClient]          =
    try {
      val deClient = DeClientBuilder.defaultBuilder().build()
      Right(new DefaultCdpDeClient(new DeClientWrapper(deClient)))
    } catch { case t: Throwable => Left(CdpDeClientInitError(t)) }

  /** Create a [[DefaultCdpDeClientWithAudit]]
    * @return Right(CdpDeClient)
    *         Left(CdpDeClientError)
    */
  def defaultWithAudit(): Either[CdpDeClientError, CdpDeClient] =
    default().map(new DefaultCdpDeClientWithAudit(_, Audit.default("CdpDeClient")))

}
