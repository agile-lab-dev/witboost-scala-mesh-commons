package it.agilelab.provisioning.aws.s3.batch.gateway

import it.agilelab.provisioning.aws.s3.batch.model.{ S3BatchJobManifest, S3BatchJobReport }
import it.agilelab.provisioning.aws.s3.batch.model.S3BatchJobManifestSchema.BucketKeyVersionIdJobManifestSchema
import it.agilelab.provisioning.aws.s3.batch.model.S3BatchJobOperation.{
  LambdaInvokeJob,
  S3CopyInStandardStorageClassAES256SSEJob
}
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.s3control.S3ControlClient
import software.amazon.awssdk.services.s3control.model._

class DefaultS3BatchOperationsGatewayTest
    extends AnyFunSuite
    with MockFactory
    with S3BatchOperationsGatewayTestSupport {
  val accountId: String                               = "account-id"
  val s3ControlClient: S3ControlClient                = mock[S3ControlClient]
  val defaultGateway: DefaultS3BatchOperationsGateway = new DefaultS3BatchOperationsGateway(accountId, s3ControlClient)

  test("createJob InvokeLambda returns Right(String)") {
    val token       = "token"
    val jobManifest = JobManifest
      .builder()
      .spec(
        JobManifestSpec
          .builder()
          .format(JobManifestFormat.S3_BATCH_OPERATIONS_CSV_20180820)
          .fields(JobManifestFieldName.BUCKET, JobManifestFieldName.KEY, JobManifestFieldName.VERSION_ID)
          .build()
      )
      .location(
        JobManifestLocation
          .builder()
          .objectArn("arn:aws:s3:::bucket/key.csv")
          .eTag("eTag")
          .build()
      )
      .build()

    val operation = JobOperation
      .builder()
      .lambdaInvoke(
        LambdaInvokeOperation
          .builder()
          .functionArn("functionArn")
          .build()
      )
      .build()
    val jobReport = JobReport
      .builder()
      .bucket("arn:aws:s3:::report-bucket")
      .prefix("prefix")
      .format(JobReportFormat.REPORT_CSV_20180820)
      .enabled(true)
      .reportScope(JobReportScope.ALL_TASKS)
      .build()

    (s3ControlClient
      .createJob(_: CreateJobRequest))
      .expects(
        CreateJobRequest
          .builder()
          .accountId(accountId)
          .roleArn("arn:aws:iam::account-id:role/role-name")
          .priority(10)
          .description(s"description")
          .confirmationRequired(true)
          .manifest(jobManifest)
          .operation(operation)
          .clientRequestToken(token)
          .report(jobReport)
          .build()
      )
      .once()
      .returns(CreateJobResponse.builder().jobId("job-id").build())

    val actual = defaultGateway.createJob(
      "arn:aws:iam::account-id:role/role-name",
      10,
      s"description",
      "token",
      S3BatchJobManifest(
        "arn:aws:s3:::bucket/key.csv",
        "eTag",
        None,
        BucketKeyVersionIdJobManifestSchema()
      ),
      LambdaInvokeJob("functionArn"),
      S3BatchJobReport(
        enabled = true,
        "arn:aws:s3:::report-bucket",
        "prefix",
        failedTaskOnly = false
      )
    )
    assert(actual == Right("job-id"))
  }

  test("createJob CopyJob returns Right(String)") {
    val token       = "token"
    val jobManifest = JobManifest
      .builder()
      .spec(
        JobManifestSpec
          .builder()
          .format(JobManifestFormat.S3_BATCH_OPERATIONS_CSV_20180820)
          .fields(JobManifestFieldName.BUCKET, JobManifestFieldName.KEY, JobManifestFieldName.VERSION_ID)
          .build()
      )
      .location(
        JobManifestLocation
          .builder()
          .objectArn("arn:aws:s3:::bucket/key.csv")
          .eTag("eTag")
          .build()
      )
      .build()

    val operation = JobOperation
      .builder()
      .s3PutObjectCopy(
        S3CopyObjectOperation
          .builder()
          .targetResource("arn:aws:s3:::target-bucket")
          .metadataDirective("COPY")
          .newObjectMetadata(S3ObjectMetadata.builder().sseAlgorithm("AES256").build())
          .storageClass(S3StorageClass.STANDARD)
          .build()
      )
      .build()

    val jobReport = JobReport
      .builder()
      .bucket("arn:aws:s3:::report-bucket")
      .prefix("prefix")
      .format(JobReportFormat.REPORT_CSV_20180820)
      .enabled(true)
      .reportScope(JobReportScope.ALL_TASKS)
      .build()

    (s3ControlClient
      .createJob(_: CreateJobRequest))
      .expects(
        CreateJobRequest
          .builder()
          .accountId(accountId)
          .roleArn("arn:aws:iam::account-id:role/role-name")
          .priority(10)
          .description(s"description")
          .confirmationRequired(true)
          .manifest(jobManifest)
          .operation(operation)
          .clientRequestToken(token)
          .report(jobReport)
          .build()
      )
      .once()
      .returns(CreateJobResponse.builder().jobId("job-id").build())

    val actual = defaultGateway.createJob(
      "arn:aws:iam::account-id:role/role-name",
      10,
      s"description",
      "token",
      S3BatchJobManifest(
        "arn:aws:s3:::bucket/key.csv",
        "eTag",
        None,
        BucketKeyVersionIdJobManifestSchema()
      ),
      S3CopyInStandardStorageClassAES256SSEJob("arn:aws:s3:::target-bucket"),
      S3BatchJobReport(
        enabled = true,
        "arn:aws:s3:::report-bucket",
        "prefix",
        failedTaskOnly = false
      )
    )
    assert(actual == Right("job-id"))
  }

  test("createJob returns Left(CreateJobErr)") {
    (s3ControlClient
      .createJob(_: CreateJobRequest))
      .expects(*)
      .once()
      .throws(SdkClientException.create("x"))

    val actual = defaultGateway.createJob(
      "arn:aws:iam::account-id:role/role-name",
      10,
      s"description",
      "token",
      S3BatchJobManifest(
        "arn:aws:s3:::bucket/key.csv",
        "eTag",
        None,
        BucketKeyVersionIdJobManifestSchema()
      ),
      S3CopyInStandardStorageClassAES256SSEJob("arn:aws:s3:::target-bucket"),
      S3BatchJobReport(
        enabled = true,
        "arn:aws:s3:::report-bucket",
        "prefix",
        failedTaskOnly = false
      )
    )
    assertCreateJobErr(actual, "description", "token", "x")
  }

  test("getJobStatus returns Right(String)") {
    (s3ControlClient
      .describeJob(_: DescribeJobRequest))
      .expects(
        DescribeJobRequest
          .builder()
          .accountId(accountId)
          .jobId("job-id")
          .build()
      )
      .once()
      .returns(
        DescribeJobResponse
          .builder()
          .job(
            JobDescriptor
              .builder()
              .status(JobStatus.NEW)
              .build()
          )
          .build()
      )

    val actual = defaultGateway.getJobStatus("job-id")
    assert(actual == Right("NEW"))
  }

  test("getJobStatus returns Left(GetJobStatusErr)") {
    (s3ControlClient
      .describeJob(_: DescribeJobRequest))
      .expects(
        DescribeJobRequest
          .builder()
          .accountId(accountId)
          .jobId("job-id")
          .build()
      )
      .once()
      .throws(SdkClientException.create("x"))

    val actual = defaultGateway.getJobStatus("job-id")
    assertGetJobStatusErr(actual, "job-id", "x")
  }

  test("runJob returns Right(String)") {
    (s3ControlClient
      .updateJobStatus(_: UpdateJobStatusRequest))
      .expects(
        UpdateJobStatusRequest
          .builder()
          .accountId(accountId)
          .jobId("job-id")
          .requestedJobStatus(RequestedJobStatus.READY)
          .build()
      )
      .once()
      .returns(
        UpdateJobStatusResponse
          .builder()
          .jobId("job-id")
          .status(JobStatus.READY)
          .build()
      )

    val actual = defaultGateway.runJob("job-id")
    assert(actual == Right())
  }

  test("runJob returns Left(RunJobErr)") {
    (s3ControlClient
      .updateJobStatus(_: UpdateJobStatusRequest))
      .expects(
        UpdateJobStatusRequest
          .builder()
          .accountId(accountId)
          .jobId("job-id")
          .requestedJobStatus(RequestedJobStatus.READY)
          .build()
      )
      .once()
      .throws(SdkClientException.create("x"))

    val actual = defaultGateway.runJob("job-id")
    assertRunJobErr(actual, "job-id", "x")
  }

}
