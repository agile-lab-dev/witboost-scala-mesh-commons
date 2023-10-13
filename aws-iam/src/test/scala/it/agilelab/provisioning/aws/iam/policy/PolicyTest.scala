package it.agilelab.provisioning.aws.iam.policy

import cats.implicits._
import it.agilelab.provisioning.aws.iam.policy.Policy._
import it.agilelab.provisioning.aws.iam.policy.Action.{ S3_ALL, S3_GET_OBJECT }
import it.agilelab.provisioning.aws.iam.policy.Effect.{ ALLOW, DENY }
import it.agilelab.provisioning.aws.iam.policy.Resource.S3Resource
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class PolicyTest extends AnyFunSuite with MockFactory {

  Seq(
    (
      Policy(Seq(Statement(ALLOW, Seq(S3_ALL), Seq(S3Resource("b1", "p1"), S3Resource("b1", "p2"))))),
      """{"Statement":[{"Effect":"Allow","Action":["s3:*"],"Resource":["arn:aws:s3:::b1/p1","arn:aws:s3:::b1/p2"]}]}"""
    ),
    (
      Policy(Seq(Statement(ALLOW, Seq(S3_ALL, S3_GET_OBJECT), Seq(S3Resource("b2", "p1"), S3Resource("b2", "p2"))))),
      """{"Statement":[{"Effect":"Allow","Action":["s3:*","s3:GetObject"],"Resource":["arn:aws:s3:::b2/p1","arn:aws:s3:::b2/p2"]}]}"""
    ),
    (
      Policy(Seq(Statement(DENY, Seq(S3_GET_OBJECT), Seq(S3Resource("b3", "p1/p2/p3"))))),
      """{"Statement":[{"Effect":"Deny","Action":["s3:GetObject"],"Resource":["arn:aws:s3:::b3/p1/p2/p3"]}]}"""
    ),
    (
      Policy(
        Seq(
          Statement(ALLOW, Seq(S3_ALL, S3_GET_OBJECT), Seq(S3Resource("b2", "p1"), S3Resource("b2", "p2"))),
          Statement(DENY, Seq(S3_GET_OBJECT), Seq(S3Resource("b3", "p1/p2/p3")))
        )
      ),
      """{"Statement":[{"Effect":"Allow","Action":["s3:*","s3:GetObject"],"Resource":["arn:aws:s3:::b2/p1","arn:aws:s3:::b2/p2"]},{"Effect":"Deny","Action":["s3:GetObject"],"Resource":["arn:aws:s3:::b3/p1/p2/p3"]}]}"""
    )
  ) foreach { case (iamPolicy: Policy, expected: String) =>
    test(s"show return $expected") {
      assert(iamPolicy.show == expected)
    }
  }

}
