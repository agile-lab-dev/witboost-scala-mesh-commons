package it.agilelab.provisioning.aws.s3.batch.gateway

import it.agilelab.provisioning.aws.s3.batch.gateway.S3BatchOperationsError.{ CreateJobErr, GetJobStatusErr, RunJobErr }
import org.scalatest.EitherValues._

trait S3BatchOperationsGatewayTestSupport {
  def assertCreateJobErr[A](
    actual: Either[S3BatchOperationsError, A],
    jobDescription: String,
    jobToken: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[CreateJobErr])
    assert(actual.left.value.asInstanceOf[CreateJobErr].jobDescription == jobDescription)
    assert(actual.left.value.asInstanceOf[CreateJobErr].jobToken == jobToken)
    assert(actual.left.value.asInstanceOf[CreateJobErr].error.getMessage == error)
  }

  def assertGetJobStatusErr[A](
    actual: Either[S3BatchOperationsError, A],
    jobId: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[GetJobStatusErr])
    assert(actual.left.value.asInstanceOf[GetJobStatusErr].jobId == jobId)
    assert(actual.left.value.asInstanceOf[GetJobStatusErr].error.getMessage == error)
  }

  def assertRunJobErr[A](
    actual: Either[S3BatchOperationsError, A],
    jobId: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[RunJobErr])
    assert(actual.left.value.asInstanceOf[RunJobErr].jobId == jobId)
    assert(actual.left.value.asInstanceOf[RunJobErr].error.getMessage == error)
  }

}
