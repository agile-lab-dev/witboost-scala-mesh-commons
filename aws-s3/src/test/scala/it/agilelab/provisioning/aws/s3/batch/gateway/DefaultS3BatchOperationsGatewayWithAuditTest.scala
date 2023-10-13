package it.agilelab.provisioning.aws.s3.batch.gateway

import it.agilelab.provisioning.aws.s3.batch.gateway.S3BatchOperationsError.{ CreateJobErr, GetJobStatusErr, RunJobErr }
import it.agilelab.provisioning.aws.s3.batch.model.{ S3BatchJobManifest, S3BatchJobReport }
import it.agilelab.provisioning.aws.s3.batch.model.S3BatchJobManifestSchema.BucketKeyVersionIdJobManifestSchema
import it.agilelab.provisioning.aws.s3.batch.model.S3BatchJobOperation.LambdaInvokeJob
import it.agilelab.provisioning.commons.audit.Audit
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.core.exception.SdkClientException

class DefaultS3BatchOperationsGatewayWithAuditTest
    extends AnyFunSuite
    with MockFactory
    with S3BatchOperationsGatewayTestSupport {
  val audit: Audit                                    = mock[Audit]
  val defaultS3BatchGateway: S3BatchOperationsGateway = mock[S3BatchOperationsGateway]
  val s3BatchGateway: S3BatchOperationsGateway        =
    new DefaultS3BatchOperationsGatewayWithAudit(defaultS3BatchGateway, audit)

  test("createJob calls audit on success") {
    inSequence(
      (audit.info _)
        .expects(
          s"Executing CreateJob(roleArn=arn:aws:iam::account-id:role/role-name,priority=10,description=description,clientRequestToken=token,manifest=S3BatchJobManifest(arn:aws:s3:::bucket/key.csv,eTag,None,BucketKeyVersionIdJobManifestSchema()),operation=LambdaInvokeJob(functionArn),report=S3BatchJobReport(true,arn:aws:s3:::report-bucket,prefix,false))"
        )
        .once(),
      (defaultS3BatchGateway.createJob _)
        .expects(
          "arn:aws:iam::account-id:role/role-name",
          10,
          "description",
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
        .once()
        .returns(Right("job-id")),
      (audit.info _)
        .expects(
          s"CreateJob(roleArn=arn:aws:iam::account-id:role/role-name,priority=10,description=description,clientRequestToken=token,manifest=S3BatchJobManifest(arn:aws:s3:::bucket/key.csv,eTag,None,BucketKeyVersionIdJobManifestSchema()),operation=LambdaInvokeJob(functionArn),report=S3BatchJobReport(true,arn:aws:s3:::report-bucket,prefix,false)) completed successfully"
        )
        .once()
    )

    val actual = s3BatchGateway.createJob(
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

  test("createJob calls audit on failure") {
    inSequence(
      (audit.info _)
        .expects(
          s"Executing CreateJob(roleArn=arn:aws:iam::account-id:role/role-name,priority=10,description=description,clientRequestToken=token,manifest=S3BatchJobManifest(arn:aws:s3:::bucket/key.csv,eTag,None,BucketKeyVersionIdJobManifestSchema()),operation=LambdaInvokeJob(functionArn),report=S3BatchJobReport(true,arn:aws:s3:::report-bucket,prefix,false))"
        )
        .once(),
      (defaultS3BatchGateway.createJob _)
        .expects(
          "arn:aws:iam::account-id:role/role-name",
          10,
          "description",
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
        .once()
        .returns(Left(CreateJobErr("description", "token", SdkClientException.create("x")))),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "CreateJob(roleArn=arn:aws:iam::account-id:role/role-name,priority=10,description=description,clientRequestToken=token,manifest=S3BatchJobManifest(arn:aws:s3:::bucket/key.csv,eTag,None,BucketKeyVersionIdJobManifestSchema()),operation=LambdaInvokeJob(functionArn),report=S3BatchJobReport(true,arn:aws:s3:::report-bucket,prefix,false)) failed. Details: CreateJobErr(description,token,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )

    val actual = s3BatchGateway.createJob(
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

    assertCreateJobErr(actual, "description", "token", "x")
  }

  test("getJobStatus calls audit on success") {
    inSequence(
      (audit.info _)
        .expects(s"Executing GetJobStatus(jobId=job-id)")
        .once(),
      (defaultS3BatchGateway.getJobStatus _)
        .expects("job-id")
        .once()
        .returns(Right("NEW")),
      (audit.info _)
        .expects(s"GetJobStatus(jobId=job-id) completed successfully")
        .once()
    )

    val actual = s3BatchGateway.getJobStatus("job-id")

    assert(actual == Right("NEW"))
  }

  test("getJobStatus calls audit on failure") {
    inSequence(
      (audit.info _)
        .expects(s"Executing GetJobStatus(jobId=job-id)")
        .once(),
      (defaultS3BatchGateway.getJobStatus _)
        .expects("job-id")
        .once()
        .returns(Left(GetJobStatusErr("job-id", SdkClientException.create("x")))),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "GetJobStatus(jobId=job-id) failed. Details: GetJobStatusErr(job-id,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )

    val actual = s3BatchGateway.getJobStatus("job-id")
    assertGetJobStatusErr(actual, "job-id", "x")
  }

  test("runJob calls audit on success") {
    inSequence(
      (audit.info _)
        .expects(s"Executing RunJob(jobId=job-id)")
        .once(),
      (defaultS3BatchGateway.runJob _)
        .expects("job-id")
        .once()
        .returns(Right()),
      (audit.info _)
        .expects(s"RunJob(jobId=job-id) completed successfully")
        .once()
    )

    val actual = s3BatchGateway.runJob("job-id")

    assert(actual == Right())
  }

  test("runJob calls audit on failure") {
    inSequence(
      (audit.info _)
        .expects(s"Executing RunJob(jobId=job-id)")
        .once(),
      (defaultS3BatchGateway.runJob _)
        .expects("job-id")
        .once()
        .returns(Left(RunJobErr("job-id", SdkClientException.create("x")))),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "RunJob(jobId=job-id) failed. Details: RunJobErr(job-id,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )

    val actual = s3BatchGateway.runJob("job-id")
    assertRunJobErr(actual, "job-id", "x")
  }

}
