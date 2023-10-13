package it.agilelab.provisioning.aws.iam.policy

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.aws.iam.policy.Resource._

/** Defines a generic IAM Policy statement
  * @param effect policy effect
  * @param action list of policy actions
  * @param resource list of policy resources
  */
final case class Statement(
  effect: Effect,
  action: Seq[Action],
  resource: Seq[Resource]
)

object Statement {
  private val STRING_WRAP = "\"%s\""

  implicit val showStatement: Show[Statement] = Show.show(policyStmt =>
    """{"Effect":"%s","Action":[%s],"Resource":[%s]}""".format(
      policyStmt.effect.show,
      policyStmt.action.map(a => STRING_WRAP.format(a.show)).mkString(","),
      policyStmt.resource.map(r => STRING_WRAP.format(r.show)).mkString(",")
    )
  )
}
