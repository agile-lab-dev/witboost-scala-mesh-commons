package it.agilelab.provisioning.commons.client.cdp.dl

import com.cloudera.cdp.datalake.model.{ Datalake, DatalakeDetails }
import cats.implicits._
import it.agilelab.provisioning.commons.audit.Audit

class DefaultCdpDlClientWithAudit(cdpDlClient: CdpDlClient, audit: Audit) extends CdpDlClient {

  override def findAllDl(): Either[CdpDlClientError, Seq[Datalake]] = {
    val result = cdpDlClient.findAllDl()
    auditWithinResult(result, "FindAllDl")
    result
  }

  override def describeDl(dlName: String): Either[CdpDlClientError, DatalakeDetails] = {
    val result = cdpDlClient.describeDl(dlName)
    auditWithinResult(result, s"DescribeDl($dlName)")
    result
  }

  private def auditWithinResult[A](
    result: Either[CdpDlClientError, A],
    action: String
  ): Unit =
    result match {
      case Right(_) => audit.info(show"$action completed successfully")
      case Left(l)  => audit.error(show"$action failed. Details: $l")
    }

}
