package it.agilelab.provisioning.aws.iam.gateway

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.showable.ShowableOps._

/** IamGatewayError
  * A sealed trait for IamGatewayError(s)
  */
sealed trait IamGatewayError extends Exception with Product with Serializable

object IamGatewayError {

  /** IamGatewayInitError
    * @param error: throwable instance that generate this error
    */
  final case class IamGatewayInitErr(
    error: Throwable
  ) extends IamGatewayError

  /** PutRolePolicyErr
    * @param role: role name used on put role policy method
    * @param policy: policy name used on put role policy method
    * @param policyDocument: policy document used on put role policy method
    * @param error: throwable instance that generate this error
    */
  final case class PutRolePolicyErr(
    role: String,
    policy: String,
    policyDocument: String,
    error: Throwable
  ) extends IamGatewayError

  /** DeleteRolePolicyErr
    * @param role: role name used on delete role policy method
    * @param policy: policy name used on delete role policy method
    * @param error: throwable instance that generate this error
    */
  final case class DeleteRolePolicyErr(
    role: String,
    policy: String,
    error: Throwable
  ) extends IamGatewayError

  /** ExistsRolePolicyErr
    * @param role: role name used on exists role policy method
    * @param policy: policy name used on exists role policy method
    * @param error: throwable instance that generate this error
    */
  final case class ExistsRolePolicyErr(
    role: String,
    policy: String,
    error: Throwable
  ) extends IamGatewayError

  /** implicit show implementation for IamGatewayError sum type
    */
  implicit val showIamGatewayError: Show[IamGatewayError] = Show.show {
    case e: IamGatewayInitErr   => show"IamGatewayInitErr(${e.error})"
    case e: PutRolePolicyErr    => show"PutRolePolicyErr(${e.role},${e.policy},${e.policyDocument},${e.error})"
    case e: DeleteRolePolicyErr => show"DeleteRolePolicyErr(${e.role},${e.policy},${e.error})"
    case e: ExistsRolePolicyErr => show"ExistsRolePolicyErr(${e.role},${e.policy},${e.error})"
  }

}
