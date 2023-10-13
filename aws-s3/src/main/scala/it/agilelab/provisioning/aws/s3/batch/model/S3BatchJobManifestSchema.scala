package it.agilelab.provisioning.aws.s3.batch.model

sealed trait S3BatchJobManifestSchema

object S3BatchJobManifestSchema {
  final case class BucketKeyJobManifestSchema()          extends S3BatchJobManifestSchema
  final case class BucketKeyVersionIdJobManifestSchema() extends S3BatchJobManifestSchema
}
