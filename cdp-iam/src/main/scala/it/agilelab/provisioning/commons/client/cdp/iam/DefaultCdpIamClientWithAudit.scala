package it.agilelab.provisioning.commons.client.cdp.iam

import com.cloudera.cdp.iam.model.{ Group, MachineUser, ResourceAssignment }
import cats.implicits._
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.iam.model.AccessKeyCredential

class DefaultCdpIamClientWithAudit(client: CdpIamClient, audit: Audit) extends CdpIamClient {

  override def getMachineUser(machineUserName: String): Either[CdpIamClientError, Option[MachineUser]] = {
    val result = client.getMachineUser(machineUserName)
    auditWithinResult(result, s"GetMachineUser($machineUserName)")
    result
  }

  override def createMachineUser(machineUserName: String): Either[CdpIamClientError, MachineUser] = {
    val result = client.createMachineUser(machineUserName)
    auditWithinResult(result, s"CreateMachineUser($machineUserName)")
    result
  }

  override def setMachineUserWorkloadPassword(
    machineUserCrn: String,
    password: String
  ): Either[CdpIamClientError, Unit] = {
    val result = client.setMachineUserWorkloadPassword(machineUserCrn, password)
    auditWithinResult(result, s"SetMachineUserWorkloadPassword($machineUserCrn,*****)")
    result
  }

  override def addMachineUserToGroup(machineUserName: String, groupName: String): Either[CdpIamClientError, Unit] = {
    val result = client.addMachineUserToGroup(machineUserName, groupName)
    auditWithinResult(result, s"AddMachineUserToGroup($machineUserName,$groupName)")
    result
  }

  override def getGroup(groupName: String): Either[CdpIamClientError, Option[Group]] = {
    val result = client.getGroup(groupName)
    auditWithinResult(result, s"GetGroup($groupName)")
    result
  }

  override def isMachineUserInGroup(machineUserName: String, groupName: String): Either[CdpIamClientError, Boolean] = {
    val result = client.isMachineUserInGroup(machineUserName, groupName)
    auditWithinResult(result, s"IsMachineUserInGroup($machineUserName,$groupName)")
    result
  }

  override def createGroup(groupName: String): Either[CdpIamClientError, Group] = {
    val result = client.createGroup(groupName)
    auditWithinResult(result, s"CreateGroup($groupName)")
    result
  }

  override def listResourceRoles(): Either[CdpIamClientError, Seq[String]] = {
    val result = client.listResourceRoles()
    auditWithinResult(result, s"ListResourceRoles")
    result
  }

  override def listResourceRoleAssignmentsInGroup(
    groupName: String
  ): Either[CdpIamClientError, Seq[ResourceAssignment]] = {
    val result = client.listResourceRoleAssignmentsInGroup(groupName)
    auditWithinResult(result, s"ListResourceRoleAssignmentsInGroup($groupName)")
    result
  }

  override def assignResourceRoleToGroup(
    groupName: String,
    resourceAssignment: ResourceAssignment
  ): Either[CdpIamClientError, Unit] = {
    val result = client.assignResourceRoleToGroup(groupName, resourceAssignment)
    auditWithinResult(
      result,
      s"AssignResourceRoleToGroup($groupName,${resourceAssignment.getResourceCrn},${resourceAssignment.getResourceRoleCrn})"
    )
    result
  }

  override def unassignResourceRoleFromGroup(
    groupName: String,
    resourceAssignment: ResourceAssignment
  ): Either[CdpIamClientError, Unit] = {
    val result = client.unassignResourceRoleFromGroup(groupName, resourceAssignment)
    auditWithinResult(
      result,
      s"UnassignResourceRoleToGroup($groupName,${resourceAssignment.getResourceCrn},${resourceAssignment.getResourceRoleCrn})"
    )
    result
  }

  override def createMachineUserAccessKey(machineUserName: String): Either[CdpIamClientError, AccessKeyCredential] = {
    val result = client.createMachineUserAccessKey(machineUserName)
    auditWithinResult(
      result,
      s"CreateMachineUserAccessKey($machineUserName)"
    )
    result
  }

  override def accessKeyExists(accessKeyId: String): Either[CdpIamClientError, Boolean] = {
    val result = client.accessKeyExists(accessKeyId)
    auditWithinResult(
      result,
      s"AccessKeyExists($accessKeyId)"
    )
    result
  }

  override def destroyMachineUser(machineUserName: String): Either[CdpIamClientError, Unit] = {
    val result = client.destroyMachineUser(machineUserName)
    auditWithinResult(
      result,
      s"DestroyMachineUser($machineUserName)"
    )
    result
  }

  override def destroyGroup(groupName: String): Either[CdpIamClientError, Unit] = {
    val result = client.destroyGroup(groupName)
    auditWithinResult(
      result,
      s"DestroyGroup($groupName)"
    )
    result
  }

  private def auditWithinResult[A](
    result: Either[CdpIamClientError, A],
    action: String
  ): Unit =
    result match {
      case Right(_) => audit.info(show"$action completed successfully")
      case Left(l)  => audit.error(show"$action failed. Details: $l")
    }

}
