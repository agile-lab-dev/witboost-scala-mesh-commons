package it.agilelab.provisioning.aws.s3.batch.gateway

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.showable.ShowableOps._

sealed trait S3BatchOperationsError extends Exception with Product with Serializable

object S3BatchOperationsError {

  /** CreateJobErr
    * Define an error during the CreateJob process
    * @param jobDescription: job description
    * @param jobToken: job token
    * @param error: Throwable instance that generate this error
    */
  final case class CreateJobErr(
    jobDescription: String,
    jobToken: String,
    error: Throwable
  ) extends S3BatchOperationsError

  /** RunJobErr
    * Define an error during the RunJob process
    * @param jobId: job id
    * @param error: Throwable instance that generate this error
    */
  final case class RunJobErr(
    jobId: String,
    error: Throwable
  ) extends S3BatchOperationsError

  /** GetJobStatusErr
    * Define an error during the GetJobStatus process
    * @param jobId: job id
    * @param error: Throwable instance that generate this error
    */
  final case class GetJobStatusErr(
    jobId: String,
    error: Throwable
  ) extends S3BatchOperationsError

  /** Implicit cats.Show implementation for S3BatchOperationsError
    */
  implicit val showS3BatchOperationsError: Show[S3BatchOperationsError] = Show.show {
    case e: CreateJobErr    => show"CreateJobErr(${e.jobDescription},${e.jobToken},${e.error})"
    case e: RunJobErr       => show"RunJobErr(${e.jobId},${e.error})"
    case e: GetJobStatusErr => show"GetJobStatusErr(${e.jobId},${e.error})"
  }

}
