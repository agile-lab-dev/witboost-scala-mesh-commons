package it.agilelab.provisioning.aws.s3.batch.model

sealed trait S3BatchJobOperation

object S3BatchJobOperation {
  final case class S3CopyInStandardStorageClassAES256SSEJob(targetResource: String) extends S3BatchJobOperation
  final case class LambdaInvokeJob(functionArn: String)                             extends S3BatchJobOperation
}
