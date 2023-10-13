package it.agilelab.provisioning.aws.iam.policy

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.aws.iam.policy.Statement._

/** Policy model
  * @param statement: list of IAM Policy Statement
  */
final case class Policy(statement: Seq[Statement])

object Policy {

  implicit val showPolicy: Show[Policy] =
    Show.show(policy => """{"Statement":[%s]}""".format(policy.statement.map(_.show).mkString(",")))
}
