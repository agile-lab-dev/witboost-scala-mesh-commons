package it.agilelab.provisioning.aws.s3.batch.model

final case class S3BatchJobManifest(
  arn: String,
  eTag: String,
  versionId: Option[String],
  spec: S3BatchJobManifestSchema
)
