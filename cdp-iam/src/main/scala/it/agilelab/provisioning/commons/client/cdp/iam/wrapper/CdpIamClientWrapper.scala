package it.agilelab.provisioning.commons.client.cdp.iam.wrapper

import com.cloudera.cdp.iam.api.IamClient
import com.cloudera.cdp.iam.model._

/** A IamClientWrapper
  *
  * This wrapper was written to workaround the IamClient provided by the CDP SDK for Java
  * The CDP SDK can't be mocked or stubbed and this can create some issue while try to develop some feature.
  *
  * The purpose of this wrapper is just to call the IamClient given as a constructor parameters.
  * The only logic applied on this wrapper is just a conversion from java to scala collection.
  *
  * This can allow us to easily integrate IamClient features
  *
  * @param iamClient: IamClient
  */
class CdpIamClientWrapper(iamClient: IamClient) {

  /** Create a machine user
    * @param req CreateMachineUserRequest
    * @return the machine user created
    */
  def createMachineUser(req: CreateMachineUserRequest): MachineUser =
    iamClient.createMachineUser(req).getMachineUser

  /** Set the workload password for an already existing machine user
    * @param req SetWorkloadPasswordRequest
    */
  def setMachineUserWorkloadPassword(req: SetWorkloadPasswordRequest): Unit = {
    iamClient.setWorkloadPassword(req)
    ()
  }

  /** List machine users
    * @param req ListMachineUsersRequest
    * @return ListMachineUsersResponse
    */
  def listMachineUsers(req: ListMachineUsersRequest): ListMachineUsersResponse =
    iamClient.listMachineUsers(req)

  /** Add a machine user to a group
    * @param req AddMachineUserToGroupRequest
    */
  def addMachineUserToGroup(req: AddMachineUserToGroupRequest): Unit = {
    iamClient.addMachineUserToGroup(req)
    ()
  }

  /** List groups for a machine user
    * @param req ListGroupsForMachineUserRequest
    * @return ListGroupsForMachineUserResponse
    */
  def listGroupsForMachineUser(req: ListGroupsForMachineUserRequest): ListGroupsForMachineUserResponse =
    iamClient.listGroupsForMachineUser(req)

  /** List groups
    * @param req ListGroupsRequest
    * @return ListGroupsResponse
    */
  def listGroups(req: ListGroupsRequest): ListGroupsResponse =
    iamClient.listGroups(req)

  /** Create a group
    * @param req CreateGroupRequest
    * @return Group
    */
  def createGroup(req: CreateGroupRequest): Group =
    iamClient.createGroup(req).getGroup

  /** List the resource roles
    * @param req ListResourceRolesRequest
    * @return ListResourceRolesResponse
    */
  def listResourceRoles(req: ListResourceRolesRequest): ListResourceRolesResponse =
    iamClient.listResourceRoles(req)

  /** List the resource roles assigned to a group
    * @param req ListGroupAssignedResourceRolesRequest
    * @return ListGroupAssignedResourceRolesResponse
    */
  def listResourceRoleAssignmentsInGroup(
    req: ListGroupAssignedResourceRolesRequest
  ): ListGroupAssignedResourceRolesResponse =
    iamClient.listGroupAssignedResourceRoles(req)

  /** Assign a resource role to a group
    * @param req AssignGroupResourceRoleRequest
    * @return AssignGroupResourceRoleResponse
    */
  def assignGroupResourceRole(req: AssignGroupResourceRoleRequest): AssignGroupResourceRoleResponse =
    iamClient.assignGroupResourceRole(req)

  /** Unassign a resource role from a group
    * @param req UnassignGroupResourceRoleRequest
    * @return UnassignGroupResourceRoleResponse
    */
  def unassignGroupResourceRole(req: UnassignGroupResourceRoleRequest): UnassignGroupResourceRoleResponse =
    iamClient.unassignGroupResourceRole(req)

  /** Create machine user access key
    * @param req CreateMachineUserAccessKeyRequest
    * @return CreateMachineUserAccessKeyResponse
    */
  def createMachineUserAccessKey(req: CreateMachineUserAccessKeyRequest): CreateMachineUserAccessKeyResponse =
    iamClient.createMachineUserAccessKey(req)

  /** Retrieve an access key
    * @param req GetAccessKeyRequest
    * @return GetAccessKeyResponse
    */
  def getAccessKey(req: GetAccessKeyRequest): GetAccessKeyResponse =
    iamClient.getAccessKey(req)

  /** Delete a machine user
    * @param req DeleteMachineUserRequest
    * @return DeleteMachineUserResponse
    */
  def deleteMachineUser(req: DeleteMachineUserRequest): DeleteMachineUserResponse =
    iamClient.deleteMachineUser(req)

  /** Delete a group
    * @param req DeleteGroupRequest
    * @return DeleteGroupResponse
    */
  def deleteGroup(req: DeleteGroupRequest): DeleteGroupResponse =
    iamClient.deleteGroup(req)

  def getUser(req: GetUserRequest): GetUserResponse =
    iamClient.getUser(req)

  def listUsers(req: ListUsersRequest): ListUsersResponse = iamClient.listUsers(req)
}
