package it.agilelab.provisioning.aws.iam.gateway

import cats.implicits._
import it.agilelab.provisioning.commons.audit.Audit

/** Default IamGateway implementation with Audit
  * @param iamGateway an Instance of [[IamGateway]]
  * @param audit an Instance of [[Audit]]
  */
class DefaultIamGatewayWithAudit(iamGateway: IamGateway, audit: Audit) extends IamGateway {

  /** attach to the specified role the given policy.
    *
    * Call Audit.info with an informative message if put role request process
    * are successful completed otherwise call Audit.error with and error message
    * @param roleName the Iam role name
    * @param policyName the policy name
    * @param policyDocument the policy document
    * @return Either[Error,Unit]
    *         Right() if successful put role policy
    *         Left(Error) if something goes wrong while process the request
    */
  override def putRolePolicy(
    roleName: String,
    policyName: String,
    policyDocument: String
  ): Either[IamGatewayError, Unit] = {
    val action = s"PutRolePolicy(role=$roleName,policy=$policyName,policyDocument=$policyDocument)"
    val result = iamGateway.putRolePolicy(roleName, policyName, policyDocument)
    auditWithinResult(result, action)
    result
  }

  /** Detach from the specified role the given policy
    *
    * Call Audit.info with an informative message if delete role request process
    * are successful completed otherwise call Audit.error with and error message
    * @param roleName the Iam role name
    * @param policyName the policy name
    * @return Either[Error,Unit]
    *         Right() if successful delete role policy
    *         Left(Error) if something goes wrong while process the request
    */
  override def deleteRolePolicy(
    roleName: String,
    policyName: String
  ): Either[IamGatewayError, Unit] = {
    val action = s"DeleteRolePolicy(role=$roleName,policy=$policyName)"
    val result = iamGateway.deleteRolePolicy(roleName, policyName)
    auditWithinResult(result, action)
    result
  }

  /** Safe Delete from the specified role the given policy
    *
    * Compared to the deleteRolePolicy method, this method does
    * not return an error if the policy does not exists for the related user
    *
    * @param roleName      : the Iam role name
    * @param policyName  : the policy name
    * @return Either[IamGatewayError,Unit]
    *         Right() if successful delete role policy or policye does not exist for the users
    *         Left(Error) if something goes wrong while process the request
    */
  override def safeDeleteRolePolicy(roleName: String, policyName: String): Either[IamGatewayError, Unit] = {
    val action = s"SafeDeleteRolePolicy(role=$roleName,policy=$policyName)"
    val result = iamGateway.safeDeleteRolePolicy(roleName, policyName)
    auditWithinResult(result, action)
    result
  }

  /** Policy existence for a specific role
    *
    * Call Audit.info with an informative message if exists role policy process
    * are successful completed otherwise call Audit.error with and error message
    *
    * @param roleName Role name
    * @param policyName policy Name
    * @return Either[Error, Boolean]
    *         Right(true) if policy exist for the specified role
    *         Right(false) if policy does not exist for the specified role
    *         Left(Error) if something goes wrong while process the request
    */
  override def existsRolePolicy(
    roleName: String,
    policyName: String
  ): Either[IamGatewayError, Boolean] = {
    val action = s"ExistsRolePolicy(role=$roleName,policy=$policyName)"
    val result = iamGateway.existsRolePolicy(roleName, policyName)
    auditWithinResult(result, action)
    result
  }

  private def auditWithinResult[A](
    result: Either[IamGatewayError, A],
    action: String
  ): Unit =
    result match {
      case Right(_) => audit.info(show"$action completed successfully")
      case Left(l)  => audit.error(show"$action failed. Details: $l")
    }

}
