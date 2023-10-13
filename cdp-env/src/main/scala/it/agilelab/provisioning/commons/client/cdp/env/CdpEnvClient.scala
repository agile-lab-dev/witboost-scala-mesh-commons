package it.agilelab.provisioning.commons.client.cdp.env

import com.cloudera.cdp.environments.api.EnvironmentsClientBuilder
import com.cloudera.cdp.environments.model._
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.env.CdpEnvClientError.CdpEnvClientInitError
import it.agilelab.provisioning.commons.client.cdp.env.wrapper.EnvironmentsClientWrapper

/** CdpEnvClient trait
  */
trait CdpEnvClient {

  /** List Environments
    *
    * @return Right(Seq[[EnvironmentSummary]]) if list environments completed successful
    *         Left([[CdpEnvClientError]]) otherwise
    */
  def listEnvironments(): Either[CdpEnvClientError, Seq[EnvironmentSummary]]

  /** Describe an environment
    *
    * @param envName: Environment name
    * @return Right(Environment) if describe environment completed successful
    *         Left([[CdpEnvClientError]]) otherwise
    */
  def describeEnvironment(envName: String): Either[CdpEnvClientError, Environment]

  /** Synchronize all the users for a specific environment
    * The request is performed asynchronously; the status of the operation is retrieved by polling an API.
    * The polling will be performed until a request status different from RUNNING is returned or if the maximum number
    * of checks is exceeded
    * @param envName the name of the environment
    * @param maxChecks max number of checks to be performed to retrieve the sync operation status
    */
  def syncAllUsers(envName: String, maxChecks: Int = 25): Either[CdpEnvClientError, Unit]

}

/** CdpEnvClient companion object
  */
object CdpEnvClient {

  /** Create a default CdpEnvClient
    * @return CdpEnvClient
    */
  def default(): Either[CdpEnvClientError, CdpEnvClient]          =
    try {
      val client = EnvironmentsClientBuilder.defaultBuilder().build()
      Right(new DefaultCdpEnvClient(new EnvironmentsClientWrapper(client)))
    } catch { case t: Throwable => Left(CdpEnvClientInitError(t)) }

  /** Create a default CdpEnvClient with audit
    * @return CdpEnvClient
    */
  def defaultWithAudit(): Either[CdpEnvClientError, CdpEnvClient] =
    default().map(new DefaultCdpEnvClientWithAudit(_, Audit.default("CdpEnvClient")))
}
