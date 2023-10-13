package it.agilelab.provisioning.commons.client.cdp.env

import com.cloudera.cdp.environments.model.{ Environment, EnvironmentSummary }
import cats.implicits._
import it.agilelab.provisioning.commons.audit.Audit

class DefaultCdpEnvClientWithAudit(
  cdpEnvClient: CdpEnvClient,
  audit: Audit
) extends CdpEnvClient {

  private val INFO_MSG = "Executing %s"

  override def listEnvironments(): Either[CdpEnvClientError, Seq[EnvironmentSummary]] = {
    val action = s"ListEnvironments"
    audit.info(INFO_MSG.format(action))
    val result = cdpEnvClient.listEnvironments()
    auditWithinResult(result, action)
    result
  }

  override def describeEnvironment(envName: String): Either[CdpEnvClientError, Environment] = {
    val action = s"DescribeEnvironment($envName)"
    audit.info(INFO_MSG.format(action))
    val result = cdpEnvClient.describeEnvironment(envName)
    auditWithinResult(result, action)
    result
  }

  override def syncAllUsers(envName: String, maxChecks: Int): Either[CdpEnvClientError, Unit] = {
    val action = s"SyncAllUsers($envName,${maxChecks.toString})"
    audit.info(INFO_MSG.format(action))
    val result = cdpEnvClient.syncAllUsers(envName, maxChecks)
    auditWithinResult(result, action)
    result
  }

  private def auditWithinResult[A](
    result: Either[CdpEnvClientError, A],
    action: String
  ): Unit =
    result match {
      case Right(_) => audit.info(show"$action completed successfully")
      case Left(l)  => audit.error(show"$action failed. Details: $l")
    }

}
