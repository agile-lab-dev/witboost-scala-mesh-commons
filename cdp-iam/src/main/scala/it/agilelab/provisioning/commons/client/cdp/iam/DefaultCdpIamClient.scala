package it.agilelab.provisioning.commons.client.cdp.iam
import com.cloudera.cdp.CdpServiceException
import com.cloudera.cdp.iam.model._
import it.agilelab.provisioning.commons.client.cdp.iam.CdpIamClientError._
import it.agilelab.provisioning.commons.client.cdp.iam.model.AccessKeyCredential
import it.agilelab.provisioning.commons.client.cdp.iam.wrapper.CdpIamClientWrapper

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

class DefaultCdpIamClient(iamClientWrapper: CdpIamClientWrapper) extends CdpIamClient {

  override def getMachineUser(machineUserName: String): Either[CdpIamClientError, Option[MachineUser]]              =
    try Right(recursivelyFindMachineUser(machineUserName, None))
    catch { case e: Throwable => Left(GetMachineUserErr(machineUserName, e)) }

  override def createMachineUser(machineUserName: String): Either[CdpIamClientError, MachineUser]                   =
    try {
      val req = new CreateMachineUserRequest()
      req.setMachineUserName(machineUserName)
      Right(iamClientWrapper.createMachineUser(req))
    } catch { case e: Throwable => Left(CreateMachineUserErr(machineUserName, e)) }

  override def setMachineUserWorkloadPassword(
    machineUserCrn: String,
    password: String
  ): Either[CdpIamClientError, Unit]                                                                                =
    try {
      val req = new SetWorkloadPasswordRequest()
      req.setActorCrn(machineUserCrn)
      req.setPassword(password)
      iamClientWrapper.setMachineUserWorkloadPassword(req)
      Right()
    } catch { case e: Throwable => Left(SetMachineUserWorkloadPasswordErr(machineUserCrn, e)) }

  override def addMachineUserToGroup(machineUserName: String, groupName: String): Either[CdpIamClientError, Unit]   =
    try {
      val req = new AddMachineUserToGroupRequest()
      req.setMachineUserName(machineUserName)
      req.setGroupName(groupName)
      iamClientWrapper.addMachineUserToGroup(req)
      Right()
    } catch { case e: Throwable => Left(AddMachineUserToGroupErr(machineUserName, groupName, e)) }

  override def isMachineUserInGroup(machineUserName: String, groupName: String): Either[CdpIamClientError, Boolean] =
    try Right(recursivelyFindMachineUserGroup(machineUserName, groupName, None))
    catch { case e: Throwable => Left(IsMachineUserInGroupErr(machineUserName, groupName, e)) }

  override def getGroup(groupName: String): Either[CdpIamClientError, Option[Group]]                                =
    try Right(recursivelyFindGroup(groupName, None))
    catch { case e: Throwable => Left(GetGroupErr(groupName, e)) }

  override def createGroup(groupName: String): Either[CdpIamClientError, Group]                                     =
    try {
      val req = new CreateGroupRequest()
      req.setGroupName(groupName)
      Right(iamClientWrapper.createGroup(req))
    } catch { case e: Throwable => Left(CreateGroupErr(groupName, e)) }

  override def listResourceRoles(): Either[CdpIamClientError, Seq[String]]                                          =
    try Right(recursivelyListResourceRoles(Seq.empty[String], None))
    catch { case e: Throwable => Left(ListResourceRolesErr(e)) }

  override def listResourceRoleAssignmentsInGroup(
    groupName: String
  ): Either[CdpIamClientError, Seq[ResourceAssignment]]                                                             =
    try Right(recursivelyListResourceRolesInGroup(groupName, Seq.empty[ResourceAssignment], None))
    catch { case e: Throwable => Left(ListResourceRoleAssignmentsInGroupErr(groupName, e)) }

  override def assignResourceRoleToGroup(
    groupName: String,
    resourceAssignment: ResourceAssignment
  ): Either[CdpIamClientError, Unit]                                                                                =
    try {
      val req = new AssignGroupResourceRoleRequest()
      req.setGroupName(groupName)
      req.setResourceCrn(resourceAssignment.getResourceCrn)
      req.setResourceRoleCrn(resourceAssignment.getResourceRoleCrn)
      iamClientWrapper.assignGroupResourceRole(req)
      Right()
    } catch {
      case e: Throwable =>
        Left(
          AssignResourceRoleToGroupErr(
            groupName,
            resourceAssignment.getResourceCrn,
            resourceAssignment.getResourceRoleCrn,
            e
          )
        )
    }

  override def unassignResourceRoleFromGroup(
    groupName: String,
    resourceAssignment: ResourceAssignment
  ): Either[CdpIamClientError, Unit] =
    try {
      val req = new UnassignGroupResourceRoleRequest()
      req.setGroupName(groupName)
      req.setResourceCrn(resourceAssignment.getResourceCrn)
      req.setResourceRoleCrn(resourceAssignment.getResourceRoleCrn)
      iamClientWrapper.unassignGroupResourceRole(req)
      Right()
    } catch {
      case e: Throwable =>
        Left(
          UnassignResourceRoleToGroupErr(
            groupName,
            resourceAssignment.getResourceCrn,
            resourceAssignment.getResourceRoleCrn,
            e
          )
        )
    }

  override def createMachineUserAccessKey(machineUserName: String): Either[CdpIamClientError, AccessKeyCredential] =
    try {
      val req  = new CreateMachineUserAccessKeyRequest()
      req.setMachineUserName(machineUserName)
      val resp = iamClientWrapper.createMachineUserAccessKey(req)
      Right(AccessKeyCredential(resp.getAccessKey.getAccessKeyId, resp.getPrivateKey))
    } catch { case e: Throwable => Left(CreateMachineUserAccessKeyErr(machineUserName, e)) }

  override def accessKeyExists(accessKeyId: String): Either[CdpIamClientError, Boolean]                            =
    try {
      val req = new GetAccessKeyRequest()
      req.setAccessKeyId(accessKeyId)
      iamClientWrapper.getAccessKey(req)
      Right(true)
    } catch {
      case e: CdpServiceException if e.getHttpCode == 404 => Right(false)
      case e: Throwable                                   => Left(AccessKeyExistsErr(accessKeyId, e))
    }

  override def destroyMachineUser(machineUserName: String): Either[CdpIamClientError, Unit] =
    try {
      val req = new DeleteMachineUserRequest()
      req.setMachineUserName(machineUserName)
      Right(iamClientWrapper.deleteMachineUser(req))
    } catch {
      case e: Throwable => Left(DestroyMachineUserErr(machineUserName, e))
    }

  override def destroyGroup(groupName: String): Either[CdpIamClientError, Unit] =
    try {
      val req = new DeleteGroupRequest()
      req.setGroupName(groupName)
      Right(iamClientWrapper.deleteGroup(req))
    } catch {
      case e: Throwable => Left(DestroyGroupErr(groupName, e))
    }

  override def getUser(userId: String): Either[CdpIamClientError, Option[User]] =
    try {
      val req = new GetUserRequest()
      req.setUserId(userId)
      Right(Some(iamClientWrapper.getUser(req).getUser))
    } catch {
      case e: CdpServiceException if e.getHttpCode == 404 => Right(None)
      case e: Throwable                                   => Left(GetUserErr(userId, e))
    }

  override def getUserByWorkloadUsername(workloadUserName: String): Either[CdpIamClientError, Option[User]] =
    try Right(recursivelyFindUser(workloadUserName, None))
    catch {
      case e: CdpServiceException if e.getHttpCode == 404 => Right(None)
      case e: Throwable                                   => Left(GetUserErr(workloadUserName, e))
    }

  @tailrec
  private def recursivelyFindUser(
    workloadUserName: String,
    token: Option[String]
  ): Option[User] = {
    val req        = listUsersRequest(token)
    val res        = iamClientWrapper.listUsers(req)
    val userOption = res.getUsers.asScala.toSeq.find(mu => mu.getWorkloadUsername == workloadUserName)
    if (res.getNextToken == null || userOption.isDefined) userOption
    else recursivelyFindUser(workloadUserName, Some(res.getNextToken))
  }

  private def listUsersRequest(token: Option[String]): ListUsersRequest =
    token.map { t =>
      val req = new ListUsersRequest()
      req.setStartingToken(t)
      req
    }.getOrElse(new ListUsersRequest())

  @tailrec
  private def recursivelyListResourceRolesInGroup(
    groupName: String,
    resourceAssignments: Seq[ResourceAssignment],
    token: Option[String]
  ): Seq[ResourceAssignment] = {
    val req       = listResourceRolesInGroupRequest(groupName, token)
    val res       = iamClientWrapper.listResourceRoleAssignmentsInGroup(req)
    val retrieved = res.getResourceAssignments.asScala.toSeq
    if (res.getNextToken == null) resourceAssignments ++ retrieved
    else recursivelyListResourceRolesInGroup(groupName, resourceAssignments ++ retrieved, Some(res.getNextToken))
  }

  private def listResourceRolesInGroupRequest(
    groupName: String,
    token: Option[String]
  ): ListGroupAssignedResourceRolesRequest = {
    val req = new ListGroupAssignedResourceRolesRequest()
    req.setGroupName(groupName)
    token.map { t => req.setStartingToken(t); req }.getOrElse(req)
  }

  @tailrec
  private def recursivelyListResourceRoles(resourceRoles: Seq[String], token: Option[String]): Seq[String] = {
    val req       = listResourceRolesRequest(token)
    val res       = iamClientWrapper.listResourceRoles(req)
    val retrieved = res.getResourceRoles.asScala.toSeq.map(_.getCrn)

    if (res.getNextToken == null) resourceRoles ++ retrieved
    else recursivelyListResourceRoles(resourceRoles ++ retrieved, Some(res.getNextToken))
  }

  private def listResourceRolesRequest(token: Option[String]): ListResourceRolesRequest =
    token.map { t =>
      val req = new ListResourceRolesRequest()
      req.setStartingToken(t)
      req
    }.getOrElse(new ListResourceRolesRequest())

  @tailrec
  private def recursivelyFindGroup(groupName: String, token: Option[String]): Option[Group] = {
    val req         = listGroupRequest(token)
    val res         = iamClientWrapper.listGroups(req)
    val groupOption = res.getGroups.asScala.toSeq.find(g => g.getGroupName == groupName)
    if (res.getNextToken == null || groupOption.isDefined) groupOption
    else recursivelyFindGroup(groupName, Some(res.getNextToken))
  }

  private def listGroupRequest(token: Option[String]): ListGroupsRequest =
    token.map { t =>
      val req = new ListGroupsRequest()
      req.setStartingToken(t)
      req
    }.getOrElse(new ListGroupsRequest())

  @tailrec
  private def recursivelyFindMachineUser(
    user: String,
    token: Option[String]
  ): Option[MachineUser] = {
    val req        = listMachineUsersRequest(token)
    val res        = iamClientWrapper.listMachineUsers(req)
    val userOption = res.getMachineUsers.asScala.toSeq.find(mu => mu.getMachineUserName == user)
    if (res.getNextToken == null || userOption.isDefined) userOption
    else recursivelyFindMachineUser(user, Some(res.getNextToken))
  }

  private def listMachineUsersRequest(token: Option[String]): ListMachineUsersRequest =
    token.map { t =>
      val req = new ListMachineUsersRequest()
      req.setStartingToken(t)
      req
    }.getOrElse(new ListMachineUsersRequest())

  @tailrec
  private def recursivelyFindMachineUserGroup(
    user: String,
    group: String,
    token: Option[String]
  ): Boolean = {
    val req        = listGroupsForMachineUserRequest(user, token)
    val res        = iamClientWrapper.listGroupsForMachineUser(req)
    val crns       = res.getGroupCrns.asScala.toSeq.map(extractGroupName)
    val groupMatch = crns.contains(group)
    if (res.getNextToken == null || groupMatch) groupMatch
    else recursivelyFindMachineUserGroup(user, group, Some(res.getNextToken))
  }

  private def listGroupsForMachineUserRequest(
    machineUserName: String,
    token: Option[String]
  ): ListGroupsForMachineUserRequest = {
    val req = new ListGroupsForMachineUserRequest()
    req.setMachineUserName(machineUserName)
    token.map { t =>
      req.setStartingToken(t)
      req
    }.getOrElse(req)
  }

  private def extractGroupName(groupCrn: String) = groupCrn match {
    case s"crn:altus:iam:$_:$_:group:$groupName/$_" => groupName
    case _                                          => groupCrn
  }

}
