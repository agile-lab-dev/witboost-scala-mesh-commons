package it.agilelab.provisioning.aws.iam.policy

import cats.Show

/** IamPolicyResource
  */
sealed trait Resource

object Resource {

  /** IamPolicyS3Resource
    * Describe a resource based on S3 bucket
    * @param bucket: bucket name
    * @param path: path
    */
  final case class S3Resource(bucket: String, path: String) extends Resource

  implicit val showResource: Show[Resource] = Show.show { case r: S3Resource =>
    "arn:aws:s3:::%s/%s".format(r.bucket, r.path)
  }
}
