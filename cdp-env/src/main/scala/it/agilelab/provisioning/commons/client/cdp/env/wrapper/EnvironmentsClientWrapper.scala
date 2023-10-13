package it.agilelab.provisioning.commons.client.cdp.env.wrapper

import com.cloudera.cdp.environments.api.EnvironmentsClient
import com.cloudera.cdp.environments.model.{
  DescribeEnvironmentRequest,
  Environment,
  EnvironmentSummary,
  ListEnvironmentsRequest,
  SyncAllUsersRequest,
  SyncAllUsersResponse,
  SyncStatusRequest,
  SyncStatusResponse
}

import scala.jdk.CollectionConverters._

/** A EnvironmentsClient Wrapper
  *
  * This wrapper was written to workaround the EnvironmentsClient provided by the CDP SDK for Java
  * The CDP SDK can't be mocked or stubbed and this can create some issue while try to develop some feature.
  *
  * The purpose of this wrapper is just to call the EnvironmentsClient given as a constructor parameters.
  * The only logic applied on this wrapper is just a conversion from java to scala collection.
  *
  * This can allow us to easily integrate datalakeClient features
  *
  * @param environmentsClient: EnvironmentsClient
  */
class EnvironmentsClientWrapper(environmentsClient: EnvironmentsClient) {

  /** execute EnvironmentsClient.listEnvironments
    * @param req ListEnvironmentsRequest
    * @return Seq of getEnvironments
    */
  def listEnvironments(req: ListEnvironmentsRequest): Seq[EnvironmentSummary] =
    environmentsClient.listEnvironments(req).getEnvironments.asScala.toSeq

  /** Execute an EnvironmentsClient.describeEnvironment
    * @param req: DescribeEnvironmentRequest
    * @return Environment
    */
  def describeEnvironment(req: DescribeEnvironmentRequest): Environment =
    environmentsClient.describeEnvironment(req).getEnvironment

  /** Synchronize all users
    * @param req SyncAllUsersRequest
    * @return SyncAllUsersResponse
    */
  def syncAllUsers(req: SyncAllUsersRequest): SyncAllUsersResponse =
    environmentsClient.syncAllUsers(req)

  /** Retrieve the status of a users synchronization operation
    * @param req SyncStatusRequest
    * @return SyncStatusResponse
    */
  def syncStatus(req: SyncStatusRequest): SyncStatusResponse =
    environmentsClient.syncStatus(req)

}
