package it.agilelab.provisioning.commons.client.cdp.iam

import com.cloudera.cdp.iam.api.IamClientBuilder
import com.cloudera.cdp.iam.model.{ Group, MachineUser, ResourceAssignment, User }
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.iam.CdpIamClientError.CdpIamClientInitError
import it.agilelab.provisioning.commons.client.cdp.iam.model.AccessKeyCredential
import it.agilelab.provisioning.commons.client.cdp.iam.wrapper.CdpIamClientWrapper

/** CdpIamClient trait
  */
trait CdpIamClient {

  /** Retrieve a machine user
    * @param machineUserName machine user name
    * @return Right(Option[MachinUser]) if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def getMachineUser(machineUserName: String): Either[CdpIamClientError, Option[MachineUser]]

  /** Create a machine user
    * @param machineUserName machine user name
    * @return Right(MachineUser) if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def createMachineUser(machineUserName: String): Either[CdpIamClientError, MachineUser]

  /** Set the workload password of an already existing machine user
    * @param machineUserCrn the crn of a machine user
    * @param password the password to be set
    * @return Right() if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def setMachineUserWorkloadPassword(machineUserCrn: String, password: String): Either[CdpIamClientError, Unit]

  /** Add a machine user to a group
    * @param machineUserName machine user name
    * @param groupName group name
    * @return Right() if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def addMachineUserToGroup(machineUserName: String, groupName: String): Either[CdpIamClientError, Unit]

  /** Get all the groups for a machine user
    * @param machineUserName machine user name
    * @return Right(Seq[String]) if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def isMachineUserInGroup(machineUserName: String, groupName: String): Either[CdpIamClientError, Boolean]

  /** Retrieve a group
    * @param groupName name of the group
    * @return Right(Option[Group]) if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def getGroup(groupName: String): Either[CdpIamClientError, Option[Group]]

  /** Create a group
    * @param groupName the name of the group
    * @return Right(Group) if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def createGroup(groupName: String): Either[CdpIamClientError, Group]

  /** List the resource roles available
    * @return Right(Seq[String]) if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def listResourceRoles(): Either[CdpIamClientError, Seq[String]]

  /** List the resource roles assigned to a group
    * @param groupName the name of the group
    * @return Right(Seq[ResourceAssignment]) if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def listResourceRoleAssignmentsInGroup(groupName: String): Either[CdpIamClientError, Seq[ResourceAssignment]]

  /** Attach a resource role assignment to a group
    * @param groupName the name of the group
    * @param resourceAssignment ResourceAssignment
    * @return Right() if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def assignResourceRoleToGroup(
    groupName: String,
    resourceAssignment: ResourceAssignment
  ): Either[CdpIamClientError, Unit]

  /** Remove a resource role assignment to a group
    * @param groupName the name of the group
    * @param resourceAssignment ResourceAssignment
    * @return Right() if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def unassignResourceRoleFromGroup(
    groupName: String,
    resourceAssignment: ResourceAssignment
  ): Either[CdpIamClientError, Unit]

  /** Create access key for a machine user
    * @param machineUserName the name of the machine user
    * @return Right(AccessKeyCredential) if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def createMachineUserAccessKey(machineUserName: String): Either[CdpIamClientError, AccessKeyCredential]

  /** Check whether an access key exists or not
    * @param accessKeyId the access key id
    * @return Right(Boolean) if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def accessKeyExists(accessKeyId: String): Either[CdpIamClientError, Boolean]

  /** Destroy a machine user
    * @param machineUserName the name of the user
    * @return Right() if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def destroyMachineUser(machineUserName: String): Either[CdpIamClientError, Unit]

  /** Delete a group
    *
    * @param groupName the name of the group
    * @return Right() if the operation succeed
    *         Left(CdpIamClientError) otherwise
    */
  def destroyGroup(groupName: String): Either[CdpIamClientError, Unit]

  /** Gets user by searching it through its workload username
    * @param workloadUserName Workload username
    * @return Either an error if something goes wrong, or an Option with the resulting user if it exists
    */
  def getUserByWorkloadUsername(workloadUserName: String): Either[CdpIamClientError, Option[User]]

  /** Gets user using its uuid
    * @param userId uuid of the user
    * @return Either an error if something goes wrong, or an Option with the resulting user if it exists
    */
  def getUser(userId: String): Either[CdpIamClientError, Option[User]]
}

object CdpIamClient {

  /** Create a default CdpIamClient
    * @return CdpIamClient
    */
  def default(): Either[CdpIamClientError, CdpIamClient]          =
    try {
      val client = IamClientBuilder.defaultBuilder().build()
      Right(new DefaultCdpIamClient(new CdpIamClientWrapper(client)))
    } catch { case t: Throwable => Left(CdpIamClientInitError(t)) }

  /** Create a default CdpIamClient with audit
    * @return CdpIamClient
    */
  def defaultWithAudit(): Either[CdpIamClientError, CdpIamClient] =
    default().map(new DefaultCdpIamClientWithAudit(_, Audit.default("CdpIamClient")))

}
