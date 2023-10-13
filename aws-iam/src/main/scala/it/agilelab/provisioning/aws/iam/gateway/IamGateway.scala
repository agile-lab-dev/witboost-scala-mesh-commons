package it.agilelab.provisioning.aws.iam.gateway
import it.agilelab.provisioning.aws.iam.gateway.IamGatewayError.IamGatewayInitErr
import it.agilelab.provisioning.commons.audit.Audit
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.iam.IamClient

/** IamGateway
  * Provide High level useful method(s) to manage IAM
  */
trait IamGateway {

  /** Put to the specified role the given policy
    * @param roleName the Iam role name
    * @param policyName the policy name
    * @param policyDocument the policy document
    * @return Either[IamGatewayError,Unit]
    *         Right() if successful put role policy
    *         Left(Error) if something goes wrong while process the request
    */
  def putRolePolicy(roleName: String, policyName: String, policyDocument: String): Either[IamGatewayError, Unit]

  /** Delete from the specified role the given policy
    * @param roleName the Iam role name
    * @param policyName the policy name
    * @return Either[IamGatewayError,Unit]
    *         Right() if successful delete role policy
    *         Left(Error) if something goes wrong while process the request
    */
  def deleteRolePolicy(roleName: String, policyName: String): Either[IamGatewayError, Unit]

  /** Safe Delete from the specified role the given policy
    *
    * Compared to the deleteRolePolicy method, this method does
    * not return an error if the policy does not exists for the related user
    *
    * @param roleName: the Iam role name
    * @param policyName: the policy name
    * @return Either[IamGatewayError,Unit]
    *         Right() if successful delete role policy or policye does not exist for the users
    *         Left(Error) if something goes wrong while process the request
    */
  def safeDeleteRolePolicy(roleName: String, policyName: String): Either[IamGatewayError, Unit]

  /** Policy existence for a specific role
    * @param roleName Role name
    * @param policyName policy Name
    * @return Either[IamGatewayError, Boolean]
    *         Right(true) if policy exist for the specified role
    *         Right(false) if policy does not exist for the specified role
    *         Left(Error) if something goes wrong while process the request
    */
  def existsRolePolicy(roleName: String, policyName: String): Either[IamGatewayError, Boolean]

}

/** IamGateway companion object
  */
object IamGateway {

  /** Create a [[DefaultIamGateway]]
    *
    * Automatically create an instance of [[IamClient]] that will be used by the IamGateway.
    * The [[IamClient]] is configured with [[Region.AWS_GLOBAL]] as region
    *
    * @return Right(IamGateway)
    *         Left(IamGatewayInitErr) otherwise
    */
  def default(): Either[IamGatewayError, IamGateway]          =
    try {
      lazy val iamClient = IamClient.builder().region(Region.AWS_GLOBAL).build()
      Right(new DefaultIamGateway(iamClient))
    } catch { case t: Throwable => Left(IamGatewayInitErr(t)) }

  /** Create a [[DefaultIamGatewayWithAudit]]
    *
    * Automatically create an instance of [[IamClient]] and [[Audit]] that will be used by the IamGateway.
    * The [[IamClient]] is configured with [[Region.AWS_GLOBAL]] as region
    * The [[Audit]] is configured with IamGateway as logger name
    *
    * @return Right(IamGateway)
    *         Left(IamGatewayInitErr) otherwise
    */
  def defaultWithAudit(): Either[IamGatewayError, IamGateway] =
    default().map(new DefaultIamGatewayWithAudit(_, Audit.default("IamGateway")))
}
