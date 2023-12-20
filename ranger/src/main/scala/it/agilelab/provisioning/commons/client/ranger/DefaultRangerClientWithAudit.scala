package it.agilelab.provisioning.commons.client.ranger

import cats.implicits._
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.ranger.model._

class DefaultRangerClientWithAudit(
  defaultRangerClient: DefaultRangerClient,
  audit: Audit
) extends RangerClient {

  private val INFO_MSG = "Executing %s"

  override def findPolicyById(id: Int): Either[RangerClientError, Option[RangerPolicy]] = {
    val action = s"FindPolicyById(${id.toString})"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.findPolicyById(id)
    auditWithinResult(result, action)
    result
  }

  override def findPolicyByName(
    service: String,
    name: String,
    zoneName: Option[String]
  ): Either[RangerClientError, Option[RangerPolicy]] = {
    val action = s"FindPolicyByName(service=$service,name=$name,zoneName=${zoneName.toString})"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.findPolicyByName(service, name, zoneName)
    auditWithinResult(result, action)
    result
  }

  override def createPolicy(
    policy: RangerPolicy
  ): Either[RangerClientError, RangerPolicy] = {
    val action = s"CreatePolicy(${policy.toString})"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.createPolicy(policy)
    auditWithinResult(result, action)
    result
  }

  override def updatePolicy(
    policy: RangerPolicy
  ): Either[RangerClientError, RangerPolicy] = {
    val action = s"UpdatePolicy(${policy.toString})"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.updatePolicy(policy)
    auditWithinResult(result, action)
    result
  }

  override def deletePolicy(policy: RangerPolicy): Either[RangerClientError, Unit] = {
    val action = s"DeletePolicy(${policy.toString})"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.deletePolicy(policy)
    auditWithinResult(result, action)
    result
  }

  override def findSecurityZoneByName(
    zoneName: String
  ): Either[RangerClientError, Option[RangerSecurityZone]] = {
    val action = s"FindSecurityZoneByName(zoneName=$zoneName)"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.findSecurityZoneByName(zoneName)
    auditWithinResult(result, action)
    result
  }

  override def updateSecurityZone(
    zone: RangerSecurityZone
  ): Either[RangerClientError, RangerSecurityZone] = {
    val action = s"UpdateSecurityZone(${zone.toString})"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.updateSecurityZone(zone)
    auditWithinResult(result, action)
    result
  }

  override def findAllServices: Either[RangerClientError, Seq[RangerService]] = {
    val action = s"FindAllServices"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.findAllServices
    auditWithinResult(result, action)
    result
  }

  override def createSecurityZone(
    zone: RangerSecurityZone
  ): Either[RangerClientError, RangerSecurityZone] = {
    val action = s"CreateSecurityZone(zone=${zone.toString})"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.createSecurityZone(zone)
    auditWithinResult(result, action)
    result
  }

  private def auditWithinResult[A](
    result: Either[RangerClientError, A],
    action: String
  ): Unit =
    result match {
      case Right(_) => audit.info(show"$action completed successfully")
      case Left(l)  => audit.error(show"$action failed. Details: $l")
    }

  // --------------------------------
  // Roles methods
  // --------------------------------

  /** Retrieve a specific role by ID
    *
    * @param id : roleId
    * @return Right(Some(RangerRole)) if role exists
    *         Right(None) if role does not exists
    *         Left(RangerClientError) in case of error
    */
  override def findRoleById(id: Int): Either[RangerClientError, Option[RangerRole]] = {
    val action = s"FindRoleById(${id.toString})"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.findRoleById(id)
    auditWithinResult(result, action)
    result
  }

  /** Retrieve a specific role by service name and role name
    *
    * @param service : Service name
    * @param name    : Role name
    * @return Right(Some(RangerRole)) if role exists
    *         Right(None) if role does not exists
    *         Left(RangerClientError) in case of error
    */
  override def findRoleByName(
    name: String
  ): Either[RangerClientError, Option[RangerRole]] = {
    val action = s"FindRoleByName(name=$name)"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.findRoleByName(name)
    auditWithinResult(result, action)
    result
  }

  /** Create a new RangerRole
    *
    * @param role : RangerRole definition
    * @return Right(RangerRole) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def createRole(role: RangerRole): Either[RangerClientError, RangerRole] = {
    val action = s"CreateRole(${role.toString})"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.createRole(role)
    auditWithinResult(result, action)
    result
  }

  /** Update an existing RangerRole
    *
    * @param role : RangerRole definition
    * @return Right(RangerRole) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def updateRole(role: RangerRole): Either[RangerClientError, RangerRole] = {
    val action = s"UpdateRole(${role.toString})"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.updateRole(role)
    auditWithinResult(result, action)
    result
  }

  /** Delete an existing RangerRole
    *
    * @param role : RangerRole definition
    * @return Right() if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def deleteRole(role: RangerRole): Either[RangerClientError, Unit] = {
    val action = s"DeleteRole(${role.toString})"
    audit.info(INFO_MSG.format(action))
    val result = defaultRangerClient.deleteRole(role)
    auditWithinResult(result, action)
    result
  }
}
