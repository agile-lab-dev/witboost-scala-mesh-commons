package it.agilelab.provisioning.aws.s3.batch.gateway

import it.agilelab.provisioning.aws.s3.batch.gateway.S3BatchOperationsError.{ CreateJobErr, GetJobStatusErr, RunJobErr }
import it.agilelab.provisioning.aws.s3.batch.model.{
  S3BatchJobManifest,
  S3BatchJobManifestSchema,
  S3BatchJobOperation,
  S3BatchJobReport
}
import software.amazon.awssdk.services.s3control.S3ControlClient
import software.amazon.awssdk.services.s3control.model.{
  CreateJobRequest,
  DescribeJobRequest,
  JobManifest,
  JobManifestFieldName,
  JobManifestFormat,
  JobManifestLocation,
  JobManifestSpec,
  JobOperation,
  JobReport,
  JobReportFormat,
  JobReportScope,
  LambdaInvokeOperation,
  RequestedJobStatus,
  S3CopyObjectOperation,
  S3MetadataDirective,
  S3ObjectMetadata,
  S3StorageClass,
  UpdateJobStatusRequest
}

import scala.jdk.CollectionConverters._

class DefaultS3BatchOperationsGateway(
  val accountId: String,
  s3ControlClient: S3ControlClient
) extends S3BatchOperationsGateway {

  override def createJob(
    roleArn: String,
    priority: Int,
    description: String,
    clientRequestToken: String,
    manifest: S3BatchJobManifest,
    operation: S3BatchJobOperation,
    report: S3BatchJobReport
  ): Either[S3BatchOperationsError, String]                                        =
    try {
      val response = s3ControlClient.createJob(
        getCreateJobRequest(
          roleArn,
          priority,
          description,
          clientRequestToken,
          getJobManifest(manifest),
          getJobOperation(operation),
          getJobReport(report)
        )
      )
      Right(response.jobId())
    } catch { case t: Throwable => Left(CreateJobErr(description, clientRequestToken, t)) }

  override def getJobStatus(jobId: String): Either[S3BatchOperationsError, String] =
    try Right(
      s3ControlClient
        .describeJob(
          DescribeJobRequest
            .builder()
            .accountId(accountId)
            .jobId(jobId)
            .build()
        )
        .job()
        .status()
        .name()
    )
    catch { case t: Throwable => Left(GetJobStatusErr(jobId, t)) }

  override def runJob(jobId: String): Either[S3BatchOperationsError, Unit]         =
    try {
      s3ControlClient.updateJobStatus(
        UpdateJobStatusRequest
          .builder()
          .accountId(accountId)
          .jobId(jobId)
          .requestedJobStatus(RequestedJobStatus.READY)
          .build()
      )
      Right()
    } catch { case t: Throwable => Left(RunJobErr(jobId, t)) }

  private def getCreateJobRequest(
    roleArn: String,
    priority: Int,
    description: String,
    clientRequestToken: String,
    manifest: JobManifest,
    operation: JobOperation,
    report: JobReport
  ): CreateJobRequest                                                              = CreateJobRequest
    .builder()
    .accountId(accountId)
    .roleArn(roleArn)
    .priority(priority)
    .description(description)
    .confirmationRequired(true)
    .manifest(manifest)
    .operation(operation)
    .clientRequestToken(clientRequestToken)
    .report(report)
    .build()

  private def getJobReport(report: S3BatchJobReport): JobReport =
    JobReport
      .builder()
      .bucket(report.bucketArn)
      .prefix(report.prefix)
      .format(JobReportFormat.REPORT_CSV_20180820)
      .enabled(report.enabled)
      .reportScope(if (report.failedTaskOnly) JobReportScope.FAILED_TASKS_ONLY else JobReportScope.ALL_TASKS)
      .build()

  private def getJobOperation(operation: S3BatchJobOperation): JobOperation =
    operation match {
      case S3BatchJobOperation.S3CopyInStandardStorageClassAES256SSEJob(targetResource) =>
        JobOperation
          .builder()
          .s3PutObjectCopy(
            S3CopyObjectOperation
              .builder()
              .targetResource(targetResource)
              .metadataDirective(S3MetadataDirective.COPY)
              .newObjectMetadata(S3ObjectMetadata.builder().sseAlgorithm("AES256").build())
              .storageClass(S3StorageClass.STANDARD)
              .build()
          )
          .build()
      case S3BatchJobOperation.LambdaInvokeJob(functionArn)                             =>
        JobOperation
          .builder()
          .lambdaInvoke(
            LambdaInvokeOperation
              .builder()
              .functionArn(functionArn)
              .build()
          )
          .build()
    }

  private def getJobManifest(manifest: S3BatchJobManifest): JobManifest =
    JobManifest
      .builder()
      .spec(
        JobManifestSpec
          .builder()
          .format(JobManifestFormat.S3_BATCH_OPERATIONS_CSV_20180820)
          .fields(getJobManifestFields(manifest.spec).asJava)
          .build()
      )
      .location(getJobManifestLocation(manifest))
      .build()

  private def getJobManifestFields(manifestSpec: S3BatchJobManifestSchema): Seq[JobManifestFieldName] =
    manifestSpec match {
      case S3BatchJobManifestSchema.BucketKeyJobManifestSchema()          =>
        Seq(JobManifestFieldName.BUCKET, JobManifestFieldName.KEY)
      case S3BatchJobManifestSchema.BucketKeyVersionIdJobManifestSchema() =>
        Seq(JobManifestFieldName.BUCKET, JobManifestFieldName.KEY, JobManifestFieldName.VERSION_ID)
    }

  private def getJobManifestLocation(manifest: S3BatchJobManifest): JobManifestLocation =
    manifest.versionId
      .map(v =>
        JobManifestLocation
          .builder()
          .objectArn(manifest.arn)
          .eTag(manifest.eTag)
          .objectVersionId(v)
          .build()
      )
      .getOrElse(
        JobManifestLocation
          .builder()
          .objectArn(manifest.arn)
          .eTag(manifest.eTag)
          .build()
      )
}
