package it.agilelab.provisioning.aws.iam.policy

import cats.Show

/** IamPolicyAction
  */
sealed trait Action

object Action {

  /** S3 all action
    */
  final case object S3_ALL extends Action

  /** S3 get object action
    */
  final case object S3_GET_OBJECT extends Action

  implicit val showAction: Show[Action] = Show.show {
    case S3_ALL        => "s3:*"
    case S3_GET_OBJECT => "s3:GetObject"
  }
}
