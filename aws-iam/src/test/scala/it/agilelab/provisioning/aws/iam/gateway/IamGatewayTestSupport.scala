package it.agilelab.provisioning.aws.iam.gateway

import it.agilelab.provisioning.aws.iam.gateway.IamGatewayError.{
  DeleteRolePolicyErr,
  ExistsRolePolicyErr,
  PutRolePolicyErr
}
import org.scalatest.EitherValues._

trait IamGatewayTestSupport {

  def assertPutRolePolicyErr[A](
    actual: Either[IamGatewayError, A],
    role: String,
    policy: String,
    policyDocument: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[PutRolePolicyErr])
    assert(actual.left.value.asInstanceOf[PutRolePolicyErr].role == role)
    assert(actual.left.value.asInstanceOf[PutRolePolicyErr].policy == policy)
    assert(actual.left.value.asInstanceOf[PutRolePolicyErr].policyDocument == policyDocument)
    assert(actual.left.value.asInstanceOf[PutRolePolicyErr].error.getMessage == error)
  }

  def assertDeleteRolePolicyErr[A](
    actual: Either[IamGatewayError, A],
    role: String,
    policy: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[DeleteRolePolicyErr])
    assert(actual.left.value.asInstanceOf[DeleteRolePolicyErr].role == role)
    assert(actual.left.value.asInstanceOf[DeleteRolePolicyErr].policy == policy)
    assert(actual.left.value.asInstanceOf[DeleteRolePolicyErr].error.getMessage == error)
  }

  def assertExistsRolePolicyErr[A](
    actual: Either[IamGatewayError, A],
    role: String,
    policy: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[ExistsRolePolicyErr])
    assert(actual.left.value.asInstanceOf[ExistsRolePolicyErr].role == role)
    assert(actual.left.value.asInstanceOf[ExistsRolePolicyErr].policy == policy)
    assert(actual.left.value.asInstanceOf[ExistsRolePolicyErr].error.getMessage == error)
  }

}
