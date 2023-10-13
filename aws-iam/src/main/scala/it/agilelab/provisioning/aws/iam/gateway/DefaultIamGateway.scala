package it.agilelab.provisioning.aws.iam.gateway

import it.agilelab.provisioning.aws.iam.gateway.IamGatewayError.{
  DeleteRolePolicyErr,
  ExistsRolePolicyErr,
  PutRolePolicyErr
}
import software.amazon.awssdk.services.iam.IamClient
import software.amazon.awssdk.services.iam.model.{
  DeleteRolePolicyRequest,
  ListRolePoliciesRequest,
  PutRolePolicyRequest
}

import scala.annotation.tailrec
import scala.jdk.CollectionConverters.CollectionHasAsScala

/** Default IamGateway implementation
  * @param iamClient an Instance of IamClient
  */
class DefaultIamGateway(iamClient: IamClient) extends IamGateway {

  /** attach to the specified role the given policy
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
  ): Either[IamGatewayError, Unit] =
    try {
      iamClient.putRolePolicy(
        PutRolePolicyRequest
          .builder()
          .roleName(roleName)
          .policyName(policyName)
          .policyDocument(policyDocument)
          .build()
      )
      Right()
    } catch { case t: Throwable => Left(PutRolePolicyErr(roleName, policyName, policyDocument, t)) }

  /** Detach from the specified role the given policy
    * @param roleName the Iam role name
    * @param policyName the policy name
    * @return Either[Error,Unit]
    *         Right() if successful delete role policy
    *         Left(Error) if something goes wrong while process the request
    */
  override def deleteRolePolicy(
    roleName: String,
    policyName: String
  ): Either[IamGatewayError, Unit] =
    try {
      iamClient.deleteRolePolicy(
        DeleteRolePolicyRequest
          .builder()
          .roleName(roleName)
          .policyName(policyName)
          .build()
      )
      Right()
    } catch { case t: Throwable => Left(DeleteRolePolicyErr(roleName, policyName, t)) }

  /** Safe Delete from the specified role the given policy
    *
    * Compared to the deleteRolePolicy method, this method does
    * not return an error if the policy does not exists for the related user
    *
    * @param roleName      : the Iam role name
    * @param policyName  : the policy name
    * @return Either[IamGatewayError,Unit]
    *         Right() if successful delete role policy or policy does not exist for the users
    *         Left(Error) if something goes wrong while process the request
    */
  override def safeDeleteRolePolicy(
    roleName: String,
    policyName: String
  ): Either[IamGatewayError, Unit] =
    existsRolePolicy(roleName, policyName).flatMap(exists =>
      if (exists) deleteRolePolicy(roleName, policyName) else Right()
    )

  /** Policy existence for a specific role
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
  ): Either[IamGatewayError, Boolean] =
    try Right(recursiveFindRolePolicy(roleName, policyName, None))
    catch { case t: Throwable => Left(ExistsRolePolicyErr(roleName, policyName, t)) }

  @tailrec
  private def recursiveFindRolePolicy(
    roleName: String,
    policyName: String,
    marker: Option[String]
  ): Boolean = {
    val req         = listRolePoliciesReq(roleName, marker)
    val res         = iamClient.listRolePolicies(req)
    val policyMatch = res.policyNames().asScala.toSeq.contains(policyName)

    if (policyMatch || !res.isTruncated) policyMatch
    else recursiveFindRolePolicy(roleName, policyName, Some(res.marker()))
  }

  private def listRolePoliciesReq(roleName: String, marker: Option[String]): ListRolePoliciesRequest =
    marker
      .map(m => ListRolePoliciesRequest.builder().roleName(roleName).marker(m).build())
      .getOrElse(ListRolePoliciesRequest.builder().roleName(roleName).build())

}
