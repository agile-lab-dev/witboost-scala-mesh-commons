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

}
