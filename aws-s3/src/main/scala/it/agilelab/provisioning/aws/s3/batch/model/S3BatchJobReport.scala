package it.agilelab.provisioning.aws.s3.batch.model

final case class S3BatchJobReport(enabled: Boolean, bucketArn: String, prefix: String, failedTaskOnly: Boolean)
