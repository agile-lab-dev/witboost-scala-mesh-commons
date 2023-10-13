package it.agilelab.provisioning.commons.client.cdp.iam

import com.cloudera.cdp.iam.model._
import com.cloudera.cdp.{ CdpClientException, CdpServiceException }
import it.agilelab.provisioning.commons.client.cdp.iam.model.AccessKeyCredential
import it.agilelab.provisioning.commons.client.cdp.iam.wrapper.CdpIamClientWrapper
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

import java.util
import scala.jdk.CollectionConverters.MapHasAsJava

class DefaultCdpIamClientTest extends AnyFunSuite with MockFactory with CdpIamClientTestSupport {
  val wrapper: CdpIamClientWrapper = stub[CdpIamClientWrapper]
  val client                       = new DefaultCdpIamClient(wrapper)

  test("createMachineUser return Right") {
    val req = new CreateMachineUserRequest()
    req.setMachineUserName("my-machine-user-name")

    val machineUser = new MachineUser()
    machineUser.setMachineUserName("my-machine-user-name")

    (wrapper.createMachineUser _)
      .when(req)
      .returns(machineUser)

    val actual = client.createMachineUser("my-machine-user-name")
    assert(actual == Right(machineUser))
  }

  test("createMachineUser return Left(CdpIamClientError)") {
    val req = new CreateMachineUserRequest()
    req.setMachineUserName("my-machine-user-name")

    (wrapper.createMachineUser _)
      .when(req)
      .throws(new CdpClientException("x"))

    val actual = client.createMachineUser("my-machine-user-name")
    assertCreateMachineUserErr(actual, "my-machine-user-name", "x")
  }

  test("setMachineUserWorkloadPassword return Right") {
    val req = new SetWorkloadPasswordRequest()
    req.setActorCrn("my-machine-user-crn")
    req.setPassword("my-machine-user-password")

    (wrapper.setMachineUserWorkloadPassword _)
      .when(req)
      .returns()

    val actual = client.setMachineUserWorkloadPassword("my-machine-user-crn", "my-machine-user-password")
    assert(actual == Right())
  }

  test("setMachineUserWorkloadPassword return Left(CdpIamClientError)") {
    val req = new SetWorkloadPasswordRequest()
    req.setActorCrn("my-machine-user-crn")
    req.setPassword("my-machine-user-password")

    (wrapper.setMachineUserWorkloadPassword _)
      .when(req)
      .throws(new CdpClientException("x"))

    val actual = client.setMachineUserWorkloadPassword("my-machine-user-crn", "my-machine-user-password")
    assertSetMachineUserWorkloadPasswordErr(actual, "my-machine-user-crn", "x")
  }

  test("getMachineUser return Right(Some(MachineUser))") {
    val response1    = new ListMachineUsersResponse()
    val machineUser1 = new MachineUser()
    machineUser1.setMachineUserName("machine-user-1")
    val machineUser2 = new MachineUser()
    machineUser2.setMachineUserName("machine-user-2")
    response1.setMachineUsers(util.Arrays.asList(machineUser1, machineUser2))
    response1.setNextToken("next-token-1")
    val request1     = new ListMachineUsersRequest()

    val response2    = new ListMachineUsersResponse()
    val machineUser3 = new MachineUser()
    machineUser3.setMachineUserName("machine-user-3")
    val machineUser4 = new MachineUser()
    machineUser4.setMachineUserName("machine-user-4")
    response2.setMachineUsers(util.Arrays.asList(machineUser3, machineUser4))
    response2.setNextToken("next-token-2")
    val request2     = new ListMachineUsersRequest()
    request2.setStartingToken("next-token-1")

    val response3    = new ListMachineUsersResponse()
    val machineUser5 = new MachineUser()
    machineUser5.setMachineUserName("my-machine-user-name")
    val machineUser6 = new MachineUser()
    machineUser6.setMachineUserName("machine-user-6")
    response3.setMachineUsers(util.Arrays.asList(machineUser5, machineUser6))
    response3.setNextToken("next-token-3")
    val request3     = new ListMachineUsersRequest()
    request3.setStartingToken("next-token-2")

    inSequence(
      (wrapper.listMachineUsers _)
        .when(request1)
        .returns(response1),
      (wrapper.listMachineUsers _)
        .when(request2)
        .returns(response2),
      (wrapper.listMachineUsers _)
        .when(request3)
        .returns(response3)
    )

    val actual = client.getMachineUser("my-machine-user-name")
    assert(actual == Right(Some(machineUser5)))
  }

  test("getMachineUser return Right(None)") {
    val response1    = new ListMachineUsersResponse()
    val machineUser1 = new MachineUser()
    machineUser1.setMachineUserName("machine-user-1")
    val machineUser2 = new MachineUser()
    machineUser2.setMachineUserName("machine-user-2")
    response1.setMachineUsers(util.Arrays.asList(machineUser1, machineUser2))
    response1.setNextToken("next-token-1")
    val request1     = new ListMachineUsersRequest()

    val response2    = new ListMachineUsersResponse()
    val machineUser3 = new MachineUser()
    machineUser3.setMachineUserName("machine-user-3")
    val machineUser4 = new MachineUser()
    machineUser4.setMachineUserName("machine-user-4")
    response2.setMachineUsers(util.Arrays.asList(machineUser3, machineUser4))
    response2.setNextToken("next-token-2")
    val request2     = new ListMachineUsersRequest()
    request2.setStartingToken("next-token-1")

    val response3    = new ListMachineUsersResponse()
    val machineUser5 = new MachineUser()
    machineUser5.setMachineUserName("machine-user-5")
    val machineUser6 = new MachineUser()
    machineUser6.setMachineUserName("machine-user-6")
    response3.setMachineUsers(util.Arrays.asList(machineUser5, machineUser6))
    response3.setNextToken(null)
    val request3     = new ListMachineUsersRequest()
    request3.setStartingToken("next-token-2")

    inSequence(
      (wrapper.listMachineUsers _)
        .when(request1)
        .returns(response1),
      (wrapper.listMachineUsers _)
        .when(request2)
        .returns(response2),
      (wrapper.listMachineUsers _)
        .when(request3)
        .returns(response3)
    )

    val actual = client.getMachineUser("my-machine-user-name")
    assert(actual == Right(None))
  }

  test("getMachineUser return Left(CdpIamClientError)") {
    inSequence(
      (wrapper.listMachineUsers _)
        .when(new ListMachineUsersRequest())
        .throws(new CdpClientException("x"))
    )

    val actual = client.getMachineUser("my-machine-user-name")
    assertGetMachineUserErr(actual, "my-machine-user-name", "x")
  }

  test("addMachineUserToGroup return Right") {
    val req = new AddMachineUserToGroupRequest()
    req.setMachineUserName("my-machine-user-name")
    req.setGroupName("my-group-name")

    (wrapper.addMachineUserToGroup _)
      .when(req)
      .returns()

    val actual = client.addMachineUserToGroup("my-machine-user-name", "my-group-name")
    assert(actual == Right())
  }

  test("addMachineUserToGroup return Left(CdpIamClientError)") {
    val req = new AddMachineUserToGroupRequest()
    req.setMachineUserName("my-machine-user-name")
    req.setGroupName("my-group-name")

    (wrapper.addMachineUserToGroup _)
      .when(req)
      .throws(new CdpClientException("x"))

    val actual = client.addMachineUserToGroup("my-machine-user-name", "my-group-name")
    assertAddMachineUserToGroupErr(actual, "my-machine-user-name", "my-group-name", "x")
  }

  test("isMachineUserInGroup return Right(true)") {
    val response1 = new ListGroupsForMachineUserResponse()
    response1.setGroupCrns(
      util.Arrays.asList("crn:altus:iam:us-west-1:id:group:group1/id", "crn:altus:iam:us-west-1:id:group:group2/id")
    )
    response1.setNextToken("next-token-1")
    val request1  = new ListGroupsForMachineUserRequest()
    request1.setMachineUserName("my-machine-user-name")

    val response2 = new ListGroupsForMachineUserResponse()
    response2.setGroupCrns(
      util.Arrays.asList("crn:altus:iam:us-west-1:id:group:group3/id", "crn:altus:iam:us-west-1:id:group:group4/id")
    )
    response2.setNextToken("next-token-2")
    val request2  = new ListGroupsForMachineUserRequest()
    request2.setMachineUserName("my-machine-user-name")
    request2.setStartingToken("next-token-1")

    val response3 = new ListGroupsForMachineUserResponse()
    response3.setGroupCrns(
      util.Arrays
        .asList("crn:altus:iam:us-west-1:id:group:my-group-name/id", "crn:altus:iam:us-west-1:id:group:group6/id")
    )
    response3.setNextToken("next-token-3")
    val request3  = new ListGroupsForMachineUserRequest()
    request3.setMachineUserName("my-machine-user-name")
    request3.setStartingToken("next-token-2")

    inSequence(
      (wrapper.listGroupsForMachineUser _)
        .when(request1)
        .returns(response1),
      (wrapper.listGroupsForMachineUser _)
        .when(request2)
        .returns(response2),
      (wrapper.listGroupsForMachineUser _)
        .when(request3)
        .returns(response3)
    )

    val actual = client.isMachineUserInGroup("my-machine-user-name", "my-group-name")
    assert(actual == Right(true))
  }

  test("isMachineUserInGroup return Right(false)") {
    val response1 = new ListGroupsForMachineUserResponse()
    response1.setGroupCrns(
      util.Arrays.asList("crn:altus:iam:us-west-1:id:group:group1/id", "crn:altus:iam:us-west-1:id:group:group2/id")
    )
    response1.setNextToken("next-token-1")
    val request1  = new ListGroupsForMachineUserRequest()
    request1.setMachineUserName("my-machine-user-name")

    val response2 = new ListGroupsForMachineUserResponse()
    response2.setGroupCrns(
      util.Arrays.asList("crn:altus:iam:us-west-1:id:group:group3/id", "crn:altus:iam:us-west-1:id:group:group4/id")
    )
    response2.setNextToken("next-token-2")
    val request2  = new ListGroupsForMachineUserRequest()
    request2.setMachineUserName("my-machine-user-name")
    request2.setStartingToken("next-token-1")

    val response3 = new ListGroupsForMachineUserResponse()
    response3.setGroupCrns(
      util.Arrays.asList("crn:altus:iam:us-west-1:id:group:group5/id", "crn:altus:iam:us-west-1:id:group:group6/id")
    )
    response3.setNextToken(null)
    val request3  = new ListGroupsForMachineUserRequest()
    request3.setMachineUserName("my-machine-user-name")
    request3.setStartingToken("next-token-2")

    inSequence(
      (wrapper.listGroupsForMachineUser _)
        .when(request1)
        .returns(response1),
      (wrapper.listGroupsForMachineUser _)
        .when(request2)
        .returns(response2),
      (wrapper.listGroupsForMachineUser _)
        .when(request3)
        .returns(response3)
    )

    val actual = client.isMachineUserInGroup("my-machine-user-name", "my-group-name")
    assert(actual == Right(false))
  }

  test("isMachineUserInGroup return Left(CdpIamClientError)") {
    val request1 = new ListGroupsForMachineUserRequest()
    request1.setMachineUserName("my-machine-user-name")

    inSequence(
      (wrapper.listGroupsForMachineUser _)
        .when(request1)
        .throws(new CdpClientException("x"))
    )

    val actual = client.isMachineUserInGroup("my-machine-user-name", "my-group-name")
    assertIsMachineUserInGroupErr(actual, "my-machine-user-name", "my-group-name", "x")
  }

  test("getGroup return Right(Some(Group))") {
    val response1 = new ListGroupsResponse()
    val group1    = new Group()
    group1.setGroupName("group-1")
    val group2    = new Group()
    group2.setGroupName("group-2")
    response1.setGroups(util.Arrays.asList(group1, group2))
    response1.setNextToken("next-token-1")
    val request1  = new ListGroupsRequest()

    val response2 = new ListGroupsResponse()
    val group3    = new Group()
    group3.setGroupName("group-3")
    val group4    = new Group()
    group4.setGroupName("group-4")
    response2.setGroups(util.Arrays.asList(group3, group4))
    response2.setNextToken("next-token-2")
    val request2  = new ListGroupsRequest()
    request2.setStartingToken("next-token-1")

    val response3 = new ListGroupsResponse()
    val group5    = new Group()
    group5.setGroupName("my-group-name")
    val group6    = new Group()
    group6.setGroupName("group-6")
    response3.setGroups(util.Arrays.asList(group5, group6))
    response3.setNextToken("next-token-3")
    val request3  = new ListGroupsRequest()
    request3.setStartingToken("next-token-2")

    inSequence(
      (wrapper.listGroups _)
        .when(request1)
        .returns(response1),
      (wrapper.listGroups _)
        .when(request2)
        .returns(response2),
      (wrapper.listGroups _)
        .when(request3)
        .returns(response3)
    )

    val actual = client.getGroup("my-group-name")
    assert(actual == Right(Some(group5)))
  }

  test("getGroup return Right(None)") {
    val response1 = new ListGroupsResponse()
    val group1    = new Group()
    group1.setGroupName("group-1")
    val group2    = new Group()
    group2.setGroupName("group-2")
    response1.setGroups(util.Arrays.asList(group1, group2))
    response1.setNextToken("next-token-1")
    val request1  = new ListGroupsRequest()

    val response2 = new ListGroupsResponse()
    val group3    = new Group()
    group3.setGroupName("group-3")
    val group4    = new Group()
    group4.setGroupName("group-4")
    response2.setGroups(util.Arrays.asList(group3, group4))
    response2.setNextToken("next-token-2")
    val request2  = new ListGroupsRequest()
    request2.setStartingToken("next-token-1")

    val response3 = new ListGroupsResponse()
    val group5    = new Group()
    group5.setGroupName("group5")
    val group6    = new Group()
    group6.setGroupName("group-6")
    response3.setGroups(util.Arrays.asList(group5, group6))
    response3.setNextToken(null)
    val request3  = new ListGroupsRequest()
    request3.setStartingToken("next-token-2")

    inSequence(
      (wrapper.listGroups _)
        .when(request1)
        .returns(response1),
      (wrapper.listGroups _)
        .when(request2)
        .returns(response2),
      (wrapper.listGroups _)
        .when(request3)
        .returns(response3)
    )

    val actual = client.getGroup("my-group-name")
    assert(actual == Right(None))
  }

  test("getGroup return Left(CdpIamClientError)") {
    inSequence(
      (wrapper.listGroups _)
        .when(new ListGroupsRequest())
        .throws(new CdpClientException("x"))
    )

    val actual = client.getGroup("my-group-name")
    assertGetGroupErr(actual, "my-group-name", "x")
  }

  test("createGroup return Right") {
    val req = new CreateGroupRequest()
    req.setGroupName("my-group-name")

    val group = new Group()
    group.setGroupName("my-group-name")

    (wrapper.createGroup _)
      .when(req)
      .returns(group)

    val actual = client.createGroup("my-group-name")
    assert(actual == Right(group))
  }

  test("createGroup return Left(CdpIamClientError)") {
    val req = new CreateGroupRequest()
    req.setGroupName("my-group-name")

    (wrapper.createGroup _)
      .when(req)
      .throws(new CdpClientException("x"))

    val actual = client.createGroup("my-group-name")
    assertCreateGroupErr(actual, "my-group-name", "x")
  }

  test("listResourceRoles return Right(Seq[String])") {
    val response1 = new ListResourceRolesResponse()
    val rr1       = new ResourceRole()
    rr1.setCrn("DataHubCreator")
    val rr2       = new ResourceRole()
    rr2.setCrn("DataSteward")
    val rr3       = new ResourceRole()
    rr3.setCrn("EnvironmentUser")
    response1.setResourceRoles(util.Arrays.asList(rr1, rr2, rr3))
    response1.setNextToken("next-token-1")
    val request1  = new ListResourceRolesRequest()

    val response2 = new ListResourceRolesResponse()
    val rr4       = new ResourceRole()
    rr4.setCrn("IamGroupAdmin")
    val rr5       = new ResourceRole()
    rr5.setCrn("MLAdmin")
    val rr6       = new ResourceRole()
    rr6.setCrn("MLBusinessUser")
    response2.setResourceRoles(util.Arrays.asList(rr4, rr5, rr6))
    response2.setNextToken(null)
    val request2  = new ListResourceRolesRequest()
    request2.setStartingToken("next-token-1")

    inSequence(
      (wrapper.listResourceRoles _)
        .when(request1)
        .returns(response1),
      (wrapper.listResourceRoles _)
        .when(request2)
        .returns(response2)
    )

    val actual = client.listResourceRoles()
    assert(
      actual == Right(
        Seq("DataHubCreator", "DataSteward", "EnvironmentUser", "IamGroupAdmin", "MLAdmin", "MLBusinessUser")
      )
    )
  }

  test("listResourceRoles return Left(CdpIamClientError)") {
    val req = new ListResourceRolesRequest()

    (wrapper.listResourceRoles _)
      .when(req)
      .throws(new CdpClientException("x"))

    val actual = client.listResourceRoles()
    assertListResourceRolesErr(actual, "x")
  }

  test("listResourceRoleAssignmentsInGroup return Right(Seq[String])") {
    val response1 = new ListGroupAssignedResourceRolesResponse()
    val rr1       = new ResourceAssignment()
    rr1.setResourceCrn("r-crn1")
    rr1.setResourceRoleCrn("rr-crn1")
    val rr2       = new ResourceAssignment()
    rr2.setResourceCrn("r-crn2")
    rr2.setResourceRoleCrn("rr-crn2")
    response1.setResourceAssignments(util.Arrays.asList(rr1, rr2))
    response1.setNextToken("next-token-1")
    val request1  = new ListGroupAssignedResourceRolesRequest()
    request1.setGroupName("group-name")

    val response2 = new ListGroupAssignedResourceRolesResponse()
    val rr3       = new ResourceAssignment()
    rr3.setResourceCrn("r-crn3")
    rr3.setResourceRoleCrn("rr-crn3")
    val rr4       = new ResourceAssignment()
    rr4.setResourceCrn("r-crn4")
    rr4.setResourceRoleCrn("rr-crn4")
    response2.setResourceAssignments(util.Arrays.asList(rr3, rr4))
    response2.setNextToken(null)
    val request2  = new ListGroupAssignedResourceRolesRequest()
    request2.setGroupName("group-name")
    request2.setStartingToken("next-token-1")

    inSequence(
      (wrapper.listResourceRoleAssignmentsInGroup _)
        .when(request1)
        .returns(response1),
      (wrapper.listResourceRoleAssignmentsInGroup _)
        .when(request2)
        .returns(response2)
    )

    val actual = client.listResourceRoleAssignmentsInGroup("group-name")
    assert(actual == Right(Seq(rr1, rr2, rr3, rr4)))
  }

  test("listResourceRoleAssignmentsInGroup return Left(CdpIamClientError)") {
    val req = new ListGroupAssignedResourceRolesRequest()
    req.setGroupName("group-name")
    (wrapper.listResourceRoleAssignmentsInGroup _)
      .when(req)
      .throws(new CdpClientException("x"))

    val actual = client.listResourceRoleAssignmentsInGroup("group-name")
    assertListResourceRolesInGroupErr(actual, "group-name", "x")
  }

  test("assignResourceRoleToGroup return Right") {
    val req = new AssignGroupResourceRoleRequest()
    req.setGroupName("group")
    req.setResourceCrn("res-crn")
    req.setResourceRoleCrn("rr-crn")

    val resp = new AssignGroupResourceRoleResponse()

    val ra = new ResourceAssignment()
    ra.setResourceCrn("res-crn")
    ra.setResourceRoleCrn("rr-crn")

    (wrapper.assignGroupResourceRole _)
      .when(req)
      .returns(resp)

    val actual = client.assignResourceRoleToGroup("group", ra)
    assert(actual == Right())
  }

  test("assignResourceRoleToGroup return Left(CdpIamClientError)") {
    val req = new AssignGroupResourceRoleRequest()
    req.setGroupName("group")
    req.setResourceCrn("res-crn")
    req.setResourceRoleCrn("rr-crn")

    val ra = new ResourceAssignment()
    ra.setResourceCrn("res-crn")
    ra.setResourceRoleCrn("rr-crn")

    (wrapper.assignGroupResourceRole _)
      .when(req)
      .throws(new CdpClientException("x"))

    val actual = client.assignResourceRoleToGroup("group", ra)
    assertAssignResourceRoleToGroupErr(actual, "group", ra, "x")
  }

  test("unassignResourceRoleToGroup return Right") {
    val req = new UnassignGroupResourceRoleRequest()
    req.setGroupName("group")
    req.setResourceCrn("res-crn")
    req.setResourceRoleCrn("rr-crn")

    val resp = new UnassignGroupResourceRoleResponse()

    val ra = new ResourceAssignment()
    ra.setResourceCrn("res-crn")
    ra.setResourceRoleCrn("rr-crn")

    (wrapper.unassignGroupResourceRole _)
      .when(req)
      .returns(resp)

    val actual = client.unassignResourceRoleFromGroup("group", ra)
    assert(actual == Right())
  }

  test("unassignResourceRoleToGroup return Left(CdpIamClientError)") {
    val req = new UnassignGroupResourceRoleRequest()
    req.setGroupName("group")
    req.setResourceCrn("res-crn")
    req.setResourceRoleCrn("rr-crn")

    val ra = new ResourceAssignment()
    ra.setResourceCrn("res-crn")
    ra.setResourceRoleCrn("rr-crn")

    (wrapper.unassignGroupResourceRole _)
      .when(req)
      .throws(new CdpClientException("x"))

    val actual = client.unassignResourceRoleFromGroup("group", ra)
    assertUnassignResourceRoleToGroupErr(actual, "group", ra, "x")
  }

  test("createMachineUserAccessKey return Right((String, String))") {
    val req = new CreateMachineUserAccessKeyRequest()
    req.setMachineUserName("mu")

    val resp = new CreateMachineUserAccessKeyResponse()
    val ak   = new AccessKey()
    ak.setAccessKeyId("access-key-id")
    resp.setAccessKey(ak)
    resp.setPrivateKey("private-key")

    (wrapper.createMachineUserAccessKey _)
      .when(req)
      .returns(resp)

    val actual = client.createMachineUserAccessKey("mu")
    assert(actual == Right(AccessKeyCredential("access-key-id", "private-key")))
  }

  test("createMachineUserAccessKey return Left(CdpIamClientError)") {
    val req = new CreateMachineUserAccessKeyRequest()
    req.setMachineUserName("mu")

    (wrapper.createMachineUserAccessKey _)
      .when(req)
      .throws(new CdpClientException("x"))

    val actual = client.createMachineUserAccessKey("mu")
    assertCreateMachineUserAccessKeyErr(actual, "mu", "x")
  }

  test("accessKeyExists return Right(true)") {
    val req = new GetAccessKeyRequest()
    req.setAccessKeyId("access-key-id")

    val resp = new GetAccessKeyResponse()

    (wrapper.getAccessKey _)
      .when(req)
      .returns(resp)

    val actual = client.accessKeyExists("access-key-id")
    assert(actual == Right(true))
  }

  test("accessKeyExists return Right(false)") {
    val req = new GetAccessKeyRequest()
    req.setAccessKeyId("access-key-id")

    (wrapper.getAccessKey _)
      .when(req)
      .throws(
        new CdpServiceException(
          "requestId",
          404,
          Map("" -> util.Arrays.asList("")).asJava,
          "statusCode",
          "statusMessage"
        )
      )

    val actual = client.accessKeyExists("access-key-id")
    assert(actual == Right(false))
  }

  test("accessKeyExists return Left(CdpIamClientError)") {
    val req = new GetAccessKeyRequest()
    req.setAccessKeyId("access-key-id")

    (wrapper.getAccessKey _)
      .when(req)
      .throws(
        new CdpServiceException(
          "requestId",
          500,
          Map("" -> util.Arrays.asList("")).asJava,
          "statusCode",
          "statusMessage"
        )
      )

    val actual = client.accessKeyExists("access-key-id")
    assertAccessKeyExistsErr(
      actual,
      "access-key-id",
      "com.cloudera.cdp.CdpServiceException: 500: statusCode: statusMessage requestId"
    )
  }

  test("destroyMachineUser return Right()") {
    val req = new DeleteMachineUserRequest()
    req.setMachineUserName("my-machine-user-name")

    val resp = new DeleteMachineUserResponse()

    (wrapper.deleteMachineUser _)
      .when(req)
      .returns(resp)

    val actual = client.destroyMachineUser("my-machine-user-name")
    assert(actual == Right())
  }

  test("destroyMachineUser return Left(CdpIamClientError)") {
    val req = new DeleteMachineUserRequest()
    req.setMachineUserName("my-machine-user-name")

    (wrapper.deleteMachineUser _)
      .when(req)
      .throws(
        new CdpServiceException(
          "requestId",
          500,
          Map("" -> util.Arrays.asList("")).asJava,
          "statusCode",
          "statusMessage"
        )
      )

    val actual = client.destroyMachineUser("my-machine-user-name")
    assertDestroyMachineUserErr(
      actual,
      "my-machine-user-name",
      "com.cloudera.cdp.CdpServiceException: 500: statusCode: statusMessage requestId"
    )
  }

  test("destroyGroup return Right()") {
    val req = new DeleteGroupRequest()
    req.setGroupName("my-group-name")

    val resp = new DeleteGroupResponse()

    (wrapper.deleteGroup _)
      .when(req)
      .returns(resp)

    val actual = client.destroyGroup("my-group-name")
    assert(actual == Right())
  }

  test("destroyGroup return Left(CdpIamClientError)") {
    val req = new DeleteGroupRequest()
    req.setGroupName("my-group-name")

    (wrapper.deleteGroup _)
      .when(req)
      .throws(
        new CdpServiceException(
          "requestId",
          500,
          Map("" -> util.Arrays.asList("")).asJava,
          "statusCode",
          "statusMessage"
        )
      )

    val actual = client.destroyGroup("my-group-name")
    assertDestroyGroupErr(
      actual,
      "my-group-name",
      "com.cloudera.cdp.CdpServiceException: 500: statusCode: statusMessage requestId"
    )
  }

}
