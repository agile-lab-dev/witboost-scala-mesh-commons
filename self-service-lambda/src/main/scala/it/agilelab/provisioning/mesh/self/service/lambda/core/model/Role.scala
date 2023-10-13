package it.agilelab.provisioning.mesh.self.service.lambda.core.model

/** Role model
  *
  * @param name       : role name
  * @param domain     : domain name
  * @param iamRole    : iam role
  * @param iamRoleArn : iam role arn
  * @param cdpRole    : cdp role
  * @param cdpRoleCrn : cdp role arn
  */
final case class Role(
  name: String,
  domain: String,
  iamRole: String,
  iamRoleArn: String,
  cdpRole: String,
  cdpRoleCrn: String
)
