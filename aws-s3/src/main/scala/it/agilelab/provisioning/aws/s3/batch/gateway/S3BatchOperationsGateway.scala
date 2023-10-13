package it.agilelab.provisioning.aws.s3.batch.gateway

import it.agilelab.provisioning.aws.s3.batch.model.{ S3BatchJobManifest, S3BatchJobOperation, S3BatchJobReport }
import it.agilelab.provisioning.commons.audit.Audit
import software.amazon.awssdk.services.s3control.S3ControlClient

/** S3BatchOperationsGateway trait
  */
trait S3BatchOperationsGateway {

  /** Create a S3 Batch Operation job
    * @param roleArn role that will execute the job
    * @param priority job priority
    * @param description job description
    * @param clientRequestToken idempotency token
    *        when using the same job request with the same token twice, the originally created job will be returned
    *        when using different job request with the same token twice, the request will fail
    * @param manifest job manifest specification
    * @param operation job operation
    * @param report job report
    * @return Either[S3BatchOperationsError,String]
    *         Right(String) the job id
    *         Left(Error) if something goes wrong during the request
    */
  def createJob(
    roleArn: String,
    priority: Int,
    description: String,
    clientRequestToken: String,
    manifest: S3BatchJobManifest,
    operation: S3BatchJobOperation,
    report: S3BatchJobReport
  ): Either[S3BatchOperationsError, String]

  /** Run a job
    * the request will fail if done against an already completed job
    * @param jobId the id of the job
    * @return Either[S3BatchOperationsError,Unit]
    *         Right() if everything works fine
    *         Left(Error) if something goes wrong during the request
    */
  def runJob(jobId: String): Either[S3BatchOperationsError, Unit]

  /** Retrieve the status of a job
    * @param jobId job id
    * @return Either[S3BatchOperationsError,String]
    *         Right(String) the job status
    *         Left(Error) if something goes wrong during the request
    */
  def getJobStatus(jobId: String): Either[S3BatchOperationsError, String]

}

/** S3BatchOperationsGateway companion object
  */
object S3BatchOperationsGateway {

  /** Create [[DefaultS3BatchOperationsGateway]] an instance of S3BatchOperationsGateway trait
    * @param accountId: an instance of [[String]]
    * @param s3ControlClient: an instance of [[S3ControlClient]]
    * @return A default implementation of [[S3BatchOperationsGateway]]
    */
  def default(accountId: String, s3ControlClient: S3ControlClient): S3BatchOperationsGateway =
    new DefaultS3BatchOperationsGateway(accountId, s3ControlClient)

  /** Create [[DefaultS3BatchOperationsGatewayWithAudit]] an instance of S3BatchOperationsGateway trait
    * @param accountId: an instance of [[String]]
    * @param s3ControlClient: an instance of [[S3ControlClient]]
    * @return A default with audit implementation of [[S3BatchOperationsGateway]]
    */
  def defaultWithAudit(accountId: String, s3ControlClient: S3ControlClient): S3BatchOperationsGateway =
    new DefaultS3BatchOperationsGatewayWithAudit(
      new DefaultS3BatchOperationsGateway(accountId, s3ControlClient),
      Audit.default("S3BatchOperationsGateway")
    )
}
