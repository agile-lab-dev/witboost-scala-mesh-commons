package it.agilelab.provisioning.commons.client.cdp.dl

import com.cloudera.cdp.datalake.model.{ Datalake, DatalakeDetails, DescribeDatalakeRequest, ListDatalakesRequest }
import it.agilelab.provisioning.commons.client.cdp.dl.CdpDlClientError.{ DescribeDlErr, FindAllDlErr }
import it.agilelab.provisioning.commons.client.cdp.dl.wrapper.DataLakeClientWrapper

class DefaultCdpDlClient(dataLakeClientWrapper: DataLakeClientWrapper) extends CdpDlClient {

  override def findAllDl(): Either[CdpDlClientError, Seq[Datalake]]                  =
    try Right(dataLakeClientWrapper.listDatalakes(new ListDatalakesRequest()))
    catch { case e: Throwable => Left(FindAllDlErr(e)) }

  override def describeDl(dlName: String): Either[CdpDlClientError, DatalakeDetails] =
    try Right(dataLakeClientWrapper.describeDatalake(describeDlReq(dlName)))
    catch { case e: Throwable => Left(DescribeDlErr(dlName, e)) }

  private def describeDlReq(dlName: String): DescribeDatalakeRequest = {
    val req = new DescribeDatalakeRequest()
    req.setDatalakeName(dlName)
    req
  }
}
