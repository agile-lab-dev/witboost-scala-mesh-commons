package it.agilelab.provisioning.aws.s3.batch.gateway

import it.agilelab.provisioning.aws.s3.batch.model.{ S3BatchJobManifest, S3BatchJobOperation, S3BatchJobReport }
import cats.implicits._
import it.agilelab.provisioning.commons.audit.Audit

class DefaultS3BatchOperationsGatewayWithAudit(
  s3BatchGateway: S3BatchOperationsGateway,
  audit: Audit
) extends S3BatchOperationsGateway {
  private val INFO_MSG = "Executing %s"

  /** Create a S3 Batch Operation job
    *
    * Call Audit.info with an informative message if the request
    * is successfully completed otherwise call Audit.error with and error message
    *
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
  override def createJob(
    roleArn: String,
    priority: Int,
    description: String,
    clientRequestToken: String,
    manifest: S3BatchJobManifest,
    operation: S3BatchJobOperation,
    report: S3BatchJobReport
  ): Either[S3BatchOperationsError, String] = {
    val action =
      s"CreateJob(roleArn=$roleArn,priority=${priority.toString},description=$description,clientRequestToken=$clientRequestToken,manifest=${manifest.toString},operation=${operation.toString},report=${report.toString})"
    audit.info(INFO_MSG.format(action))
    val result =
      s3BatchGateway.createJob(roleArn, priority, description, clientRequestToken, manifest, operation, report)
    auditWithinResult(result, action)
    result
  }

  /** Run a job
    * the request will fail if done against an already completed job
    *
    * Call Audit.info with an informative message if the request
    * is successfully completed otherwise call Audit.error with and error message
    * @param jobId the id of the job
    * @return Either[S3BatchOperationsError,Unit]
    *         Right() if everything works fine
    *         Left(Error) if something goes wrong during the request
    */
  override def runJob(jobId: String): Either[S3BatchOperationsError, Unit] = {
    val action = s"RunJob(jobId=$jobId)"
    audit.info(INFO_MSG.format(action))
    val result = s3BatchGateway.runJob(jobId)
    auditWithinResult(result, action)
    result
  }

  /** Retrieve the status of a job
    *
    * Call Audit.info with an informative message if the request
    * is successfully completed otherwise call Audit.error with and error message
    * @param jobId job id
    * @return Either[S3BatchOperationsError,String]
    *         Right(String) the job status
    *         Left(Error) if something goes wrong during the request
    */
  override def getJobStatus(jobId: String): Either[S3BatchOperationsError, String] = {
    val action = s"GetJobStatus(jobId=$jobId)"
    audit.info(INFO_MSG.format(action))
    val result = s3BatchGateway.getJobStatus(jobId)
    auditWithinResult(result, action)
    result
  }

  private def auditWithinResult[A](
    result: Either[S3BatchOperationsError, A],
    action: String
  ): Unit =
    result match {
      case Right(_) => audit.info(show"$action completed successfully")
      case Left(l)  => audit.error(show"$action failed. Details: $l")
    }
}
