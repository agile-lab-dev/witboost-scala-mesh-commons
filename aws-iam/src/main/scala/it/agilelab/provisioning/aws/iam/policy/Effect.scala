package it.agilelab.provisioning.aws.iam.policy

import cats.Show

/** IamPolicyEffect
  */
sealed trait Effect

object Effect {

  /** Allow effect
    */
  final case object ALLOW extends Effect

  /** Deny effect
    */
  final case object DENY extends Effect

  implicit val showEffect: Show[Effect] = Show.show {
    case ALLOW => "Allow"
    case DENY  => "Deny"
  }
}
