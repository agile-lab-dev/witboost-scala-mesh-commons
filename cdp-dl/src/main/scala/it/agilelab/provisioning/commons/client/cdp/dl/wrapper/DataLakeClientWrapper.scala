package it.agilelab.provisioning.commons.client.cdp.dl.wrapper

import com.cloudera.cdp.datalake.api.DatalakeClient
import com.cloudera.cdp.datalake.model.{ Datalake, DatalakeDetails, DescribeDatalakeRequest, ListDatalakesRequest }

import scala.jdk.CollectionConverters._

/** A DataLakeClientWrapper
  *
  * This wrapper was written to workaround the DataLakeClient provided by the CDP SDK for Java
  * The CDP SDK can't be mocked or stubbed and this can create some issue while try to develop some feature.
  *
  * The purpose of this wrapper is just to call the DataLakeClient given as a constructor parameters.
  * The only logic applied on this wrapper is just a conversion from java to scala collection.
  *
  * This can allow us to easily integrate datalakeClient features
  *
  * @param datalakeClient: DatalakeClient
  */
class DataLakeClientWrapper(datalakeClient: DatalakeClient) {

  /** execute DataLakeClient.listDatalakes
    * @param req ListDataLakesRequest
    * @return Seq of DataLake
    */
  def listDatalakes(req: ListDatalakesRequest): Seq[Datalake] =
    datalakeClient.listDatalakes(req).getDatalakes.asScala.toSeq

  /** execute DataLakeClient.describeDatalake
    * @param req DescribeDatalakeRequest
    * @return DatalakeDetails
    */
  def describeDatalake(req: DescribeDatalakeRequest): DatalakeDetails =
    datalakeClient.describeDatalake(req).getDatalake
}
