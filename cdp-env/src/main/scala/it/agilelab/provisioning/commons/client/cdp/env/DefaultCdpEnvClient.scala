package it.agilelab.provisioning.commons.client.cdp.env

import com.cloudera.cdp.environments.model._
import it.agilelab.provisioning.commons.client.cdp.env.CdpEnvClientError._
import it.agilelab.provisioning.commons.client.cdp.env.wrapper.EnvironmentsClientWrapper

import java.io.ObjectInputFilter.Status
import java.util
import scala.annotation.tailrec

/** Default CdpEnvClient
  * @param environmentsClientWrapper: an [[EnvironmentsClientWrapper]]
  */
class DefaultCdpEnvClient(environmentsClientWrapper: EnvironmentsClientWrapper) extends CdpEnvClient {

  /** List Environments
    *
    * @return Right(Seq[[EnvironmentSummary]]) if list environments completed successful
    *         Left([[CdpEnvClientError]]) otherwise
    */
  override def listEnvironments(): Either[CdpEnvClientError, Seq[EnvironmentSummary]]         =
    try Right(environmentsClientWrapper.listEnvironments(new ListEnvironmentsRequest))
    catch { case e: Throwable => Left(ListEnvironmentsErr(e)) }

  /** Describe an environment
    * @param envName: Environment name
    *  @return Right(Environment) if describe environment completed successful
    *         Left(Error) otherwise
    */
  override def describeEnvironment(envName: String): Either[CdpEnvClientError, Environment]   =
    try Right(environmentsClientWrapper.describeEnvironment(describeEnvReq(envName)))
    catch { case e: Throwable => Left(DescribeEnvironmentErr(envName, e)) }

  override def syncAllUsers(envName: String, maxChecks: Int): Either[CdpEnvClientError, Unit] =
    try {
      val req             = new SyncAllUsersRequest()
      req.setEnvironmentNames(util.Arrays.asList(envName))
      val syncAllUsersRes = environmentsClientWrapper.syncAllUsers(req)
      recursivelyPollSyncStatus(
        syncAllUsersRes.getOperationId,
        syncAllUsersRes.getStatus,
        maxChecks,
        1,
        envName,
        syncAllUsersRes.getError
      )
    } catch { case e: Throwable => Left(SyncAllUsersErr(envName, e)) }

  private def describeEnvReq(envName: String): DescribeEnvironmentRequest = {
    val req = new DescribeEnvironmentRequest()
    req.setEnvironmentName(envName)
    req
  }

  @tailrec
  private def recursivelyPollSyncStatus(
    operationId: String,
    status: String,
    maxChecks: Int,
    checkNumber: Int,
    envName: String,
    error: String
  ): Either[SyncStatusErr, Unit] =
    if (status == "COMPLETED") Right()
    else if (status == "FAILED" || status == "REJECTED" || status == "TIMEDOUT") {
      Left(SyncStatusErr(envName, s"SyncAllUsers($envName) returned status $status; Details: $error"))
    } else if (maxChecks < checkNumber) {
      Left(SyncStatusErr(envName, s"Max number of checks ${maxChecks.toString} have been exceeded"))
    } else {
      val req = new SyncStatusRequest()
      req.setOperationId(operationId)
      val res = environmentsClientWrapper.syncStatus(req)
      recursivelyPollSyncStatus(operationId, res.getStatus, maxChecks, checkNumber + 1, envName, res.getError)
    }

}
