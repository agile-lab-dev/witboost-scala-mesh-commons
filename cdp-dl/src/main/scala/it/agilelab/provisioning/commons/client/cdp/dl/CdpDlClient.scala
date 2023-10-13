package it.agilelab.provisioning.commons.client.cdp.dl

import com.cloudera.cdp.datalake.api.{ DatalakeClient, DatalakeClientBuilder }
import com.cloudera.cdp.datalake.model.{ Datalake, DatalakeDetails }
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.dl.CdpDlClientError.CdpDlClientInitErr
import it.agilelab.provisioning.commons.client.cdp.dl.wrapper.DataLakeClientWrapper

/** CdpDlClient
  */
trait CdpDlClient {

  /** Retrieve all datalake
    * @return Right(Seq[Datalake]) if find all datalake completed successful
    *         Left(Error) otherwise
    */
  def findAllDl(): Either[CdpDlClientError, Seq[Datalake]]

  /** Describe a specific Datalake
    * @param dlName: datalakeName
    * @return Right(DatalakeDetails) if find all datalake completed successful
    *         Left(Error) otherwise
    */
  def describeDl(dlName: String): Either[CdpDlClientError, DatalakeDetails]
}

/** CdpDlClient companion object
  */
object CdpDlClient {

  /** Create a default CdpDlClient
    *
    * @return Right(CdpDlClient)
    *         Left(CdpDlClientError)
    */
  def default(): Either[CdpDlClientError, CdpDlClient]          =
    try {
      val datalakeClient = DatalakeClientBuilder.defaultBuilder().build()
      Right(new DefaultCdpDlClient(new DataLakeClientWrapper(datalakeClient)))
    } catch { case t: Throwable => Left(CdpDlClientInitErr(t)) }

  /** Crate a default CdpDlClient with auditing
    *
    * @return Right(CdpDlClient)
    *         Left(CdpDlClientError)
    */
  def defaultWithAudit(): Either[CdpDlClientError, CdpDlClient] =
    default().map(new DefaultCdpDlClientWithAudit(_, Audit.default("CdpDlClient")))

}
