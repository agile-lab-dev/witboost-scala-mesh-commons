package it.agilelab.provisioning.commons.client.cdp.iam

import com.cloudera.cdp.iam.model.{ MachineUser, ResourceAssignment }
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.iam.CdpIamClientError._
import it.agilelab.provisioning.commons.client.cdp.iam.model.AccessKeyCredential
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultCdpIamClientWithAuditTest extends AnyFunSuite with MockFactory with CdpIamClientTestSupport {
  val audit: Audit                = mock[Audit]
  val client: DefaultCdpIamClient = stub[DefaultCdpIamClient]
  val clientWithAudit             = new DefaultCdpIamClientWithAudit(client, audit)

  test("getMachineUser logs success info") {
    (client.getMachineUser _).when("user").returns(Right(None))
    (audit.info _).expects("GetMachineUser(user) completed successfully")
    val actual = clientWithAudit.getMachineUser("user")
    assert(actual == Right(None))
  }

  test("getMachineUser logs error info") {
    (client.getMachineUser _).when("user").returns(Left(GetMachineUserErr("user", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith("GetMachineUser(user) failed. Details: GetMachineUserErr(user,java.lang.IllegalArgumentException: x")
    })
    assertGetMachineUserErr(clientWithAudit.getMachineUser("user"), "user", "x")
  }

  test("createMachineUser logs success info") {
    val machineUser = new MachineUser()
    machineUser.setMachineUserName("user")
    (client.createMachineUser _).when("user").returns(Right(machineUser))
    (audit.info _).expects("CreateMachineUser(user) completed successfully")
    val actual      = clientWithAudit.createMachineUser("user")
    assert(actual == Right(machineUser))
  }

  test("createMachineUser logs error info") {
    (client.createMachineUser _)
      .when("user")
      .returns(Left(CreateMachineUserErr("user", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "CreateMachineUser(user) failed. Details: CreateMachineUserErr(user,java.lang.IllegalArgumentException: x"
      )
    })
    assertCreateMachineUserErr(clientWithAudit.createMachineUser("user"), "user", "x")
  }

  test("setMachineUserWorkloadPassword logs success info") {
    (client.setMachineUserWorkloadPassword _).when("user", "password").returns(Right())
    (audit.info _).expects("SetMachineUserWorkloadPassword(user,*****) completed successfully")
    val actual = clientWithAudit.setMachineUserWorkloadPassword("user", "password")
    assert(actual == Right())
  }

  test("setMachineUserWorkloadPassword logs error info") {
    (client.setMachineUserWorkloadPassword _)
      .when("user", "password")
      .returns(Left(SetMachineUserWorkloadPasswordErr("user", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "SetMachineUserWorkloadPassword(user,*****) failed. Details: SetMachineUserWorkloadPasswordErr(user,java.lang.IllegalArgumentException: x"
      )
    })
    assertSetMachineUserWorkloadPasswordErr(
      clientWithAudit.setMachineUserWorkloadPassword("user", "password"),
      "user",
      "x"
    )
  }

  test("addMachineUserToGroup logs success info") {
    (client.addMachineUserToGroup _).when("user", "group").returns(Right())
    (audit.info _).expects("AddMachineUserToGroup(user,group) completed successfully")
    val actual = clientWithAudit.addMachineUserToGroup("user", "group")
    assert(actual == Right())
  }

  test("addMachineUserToGroup logs error info") {
    (client.addMachineUserToGroup _)
      .when("user", "group")
      .returns(Left(AddMachineUserToGroupErr("user", "group", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "AddMachineUserToGroup(user,group) failed. Details: AddMachineUserToGroupErr(user,group,java.lang.IllegalArgumentException: x"
      )
    })
    val actual = clientWithAudit.addMachineUserToGroup("user", "group")
    assertAddMachineUserToGroupErr(actual, "user", "group", "x")
  }

  test("isMachineUserInGroup logs success info") {
    (client.isMachineUserInGroup _).when("user", "group").returns(Right(true))
    (audit.info _).expects("IsMachineUserInGroup(user,group) completed successfully")
    val actual = clientWithAudit.isMachineUserInGroup("user", "group")
    assert(actual == Right(true))
  }

  test("isMachineUserInGroup logs error info") {
    (client.isMachineUserInGroup _)
      .when("user", "group")
      .returns(Left(IsMachineUserInGroupErr("user", "group", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "IsMachineUserInGroup(user,group) failed. Details: IsMachineUserInGroupErr(user,group,java.lang.IllegalArgumentException: x"
      )
    })
    val actual = clientWithAudit.isMachineUserInGroup("user", "group")
    assertIsMachineUserInGroupErr(actual, "user", "group", "x")
  }

  test("getGroup logs success info") {
    (client.getGroup _).when("group").returns(Right(None))
    (audit.info _).expects("GetGroup(group) completed successfully")
    val actual = clientWithAudit.getGroup("group")
    assert(actual == Right(None))
  }

  test("getGroup logs error info") {
    (client.getGroup _)
      .when("group")
      .returns(Left(GetGroupErr("group", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "GetGroup(group) failed. Details: GetGroupErr(group,java.lang.IllegalArgumentException: x"
      )
    })
    val actual = clientWithAudit.getGroup("group")
    assertGetGroupErr(actual, "group", "x")
  }

  test("listResourceRoles logs success info") {
    (client.listResourceRoles _).when().returns(Right(Seq.empty[String]))
    (audit.info _).expects("ListResourceRoles completed successfully")
    val actual = clientWithAudit.listResourceRoles()
    assert(actual == Right(Seq.empty[String]))
  }

  test("listResourceRoles logs error info") {
    (client.listResourceRoles _)
      .when()
      .returns(Left(ListResourceRolesErr(new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "ListResourceRoles failed. Details: ListResourceRolesErr(java.lang.IllegalArgumentException: x"
      )
    })
    assertListResourceRolesErr(clientWithAudit.listResourceRoles(), "x")
  }

  test("listResourceRoleAssignmentsInGroup logs success info") {
    (client.listResourceRoleAssignmentsInGroup _).when("group").returns(Right(Seq.empty[ResourceAssignment]))
    (audit.info _).expects("ListResourceRoleAssignmentsInGroup(group) completed successfully")
    val actual = clientWithAudit.listResourceRoleAssignmentsInGroup("group")
    assert(actual == Right(Seq.empty[ResourceAssignment]))
  }

  test("listResourceRoleAssignmentsInGroup logs error info") {
    (client.listResourceRoleAssignmentsInGroup _)
      .when("group")
      .returns(Left(ListResourceRoleAssignmentsInGroupErr("group", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "ListResourceRoleAssignmentsInGroup(group) failed. Details: ListResourceRoleAssignmentsInGroupErr(group,java.lang.IllegalArgumentException: x"
      )
    })
    assertListResourceRolesInGroupErr(clientWithAudit.listResourceRoleAssignmentsInGroup("group"), "group", "x")
  }

  test("assignResourceRoleToGroup logs success info") {
    val ra     = new ResourceAssignment()
    ra.setResourceCrn("res-crn")
    ra.setResourceRoleCrn("rr-crn")
    (client.assignResourceRoleToGroup _).when("group", ra).returns(Right())
    (audit.info _).expects("AssignResourceRoleToGroup(group,res-crn,rr-crn) completed successfully")
    val actual = clientWithAudit.assignResourceRoleToGroup("group", ra)
    assert(actual == Right())
  }

  test("assignResourceRoleToGroup logs error info") {
    val ra = new ResourceAssignment()
    ra.setResourceCrn("res-crn")
    ra.setResourceRoleCrn("rr-crn")
    (client.assignResourceRoleToGroup _)
      .when("group", ra)
      .returns(Left(AssignResourceRoleToGroupErr("group", "res-crn", "rr-crn", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "AssignResourceRoleToGroup(group,res-crn,rr-crn) failed. Details: AssignResourceRoleToGroupErr(group,res-crn,rr-crn,java.lang.IllegalArgumentException: x"
      )
    })
    assertAssignResourceRoleToGroupErr(clientWithAudit.assignResourceRoleToGroup("group", ra), "group", ra, "x")
  }

  test("unassignResourceRoleToGroup logs success info") {
    val ra     = new ResourceAssignment()
    ra.setResourceCrn("res-crn")
    ra.setResourceRoleCrn("rr-crn")
    (client.unassignResourceRoleFromGroup _).when("group", ra).returns(Right())
    (audit.info _).expects("UnassignResourceRoleToGroup(group,res-crn,rr-crn) completed successfully")
    val actual = clientWithAudit.unassignResourceRoleFromGroup("group", ra)
    assert(actual == Right())
  }

  test("unassignResourceRoleToGroup logs error info") {
    val ra = new ResourceAssignment()
    ra.setResourceCrn("res-crn")
    ra.setResourceRoleCrn("rr-crn")
    (client.unassignResourceRoleFromGroup _)
      .when("group", ra)
      .returns(Left(UnassignResourceRoleToGroupErr("group", "res-crn", "rr-crn", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "UnassignResourceRoleToGroup(group,res-crn,rr-crn) failed. Details: UnassignResourceRoleToGroupErr(group,res-crn,rr-crn,java.lang.IllegalArgumentException: x"
      )
    })
    assertUnassignResourceRoleToGroupErr(clientWithAudit.unassignResourceRoleFromGroup("group", ra), "group", ra, "x")
  }

  test("createMachineUserAccessKey logs success info") {
    (client.createMachineUserAccessKey _).when("mu").returns(Right(AccessKeyCredential("access-key", "private-key")))
    (audit.info _).expects("CreateMachineUserAccessKey(mu) completed successfully")
    val actual = clientWithAudit.createMachineUserAccessKey("mu")
    assert(actual == Right(AccessKeyCredential("access-key", "private-key")))
  }

  test("createMachineUserAccessKey logs error info") {
    (client.createMachineUserAccessKey _)
      .when("mu")
      .returns(Left(CreateMachineUserAccessKeyErr("mu", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "CreateMachineUserAccessKey(mu) failed. Details: CreateMachineUserAccessKeyErr(mu,java.lang.IllegalArgumentException: x"
      )
    })
    val actual = clientWithAudit.createMachineUserAccessKey("mu")
    assertCreateMachineUserAccessKeyErr(actual, "mu", "x")
  }

  test("accessKeyExists logs success info") {
    (client.accessKeyExists _).when("access-key").returns(Right(true))
    (audit.info _).expects("AccessKeyExists(access-key) completed successfully")
    val actual = clientWithAudit.accessKeyExists("access-key")
    assert(actual == Right(true))
  }

  test("accessKeyExists logs error info") {
    (client.accessKeyExists _)
      .when("access-key")
      .returns(Left(AccessKeyExistsErr("access-key", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "AccessKeyExists(access-key) failed. Details: AccessKeyExistsErr(access-key,java.lang.IllegalArgumentException: x"
      )
    })
    val actual = clientWithAudit.accessKeyExists("access-key")
    assertAccessKeyExistsErr(actual, "access-key", "x")
  }

  test("destroyMachineUser logs success info") {
    (client.destroyMachineUser _).when("machine-user").returns(Right())
    (audit.info _).expects("DestroyMachineUser(machine-user) completed successfully")
    val actual = clientWithAudit.destroyMachineUser("machine-user")
    assert(actual == Right())
  }

  test("destroyMachineUser logs error info") {
    (client.destroyMachineUser _)
      .when("machine-user")
      .returns(Left(DestroyMachineUserErr("machine-user", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "DestroyMachineUser(machine-user) failed. Details: DestroyMachineUserErr(machine-user,java.lang.IllegalArgumentException: x"
      )
    })
    val actual = clientWithAudit.destroyMachineUser("machine-user")
    assertDestroyMachineUserErr(actual, "machine-user", "x")
  }

  test("destroyGroup logs success info") {
    (client.destroyGroup _).when("group-name").returns(Right())
    (audit.info _).expects("DestroyGroup(group-name) completed successfully")
    val actual = clientWithAudit.destroyGroup("group-name")
    assert(actual == Right())
  }

  test("destroyGroup logs error info") {
    (client.destroyGroup _)
      .when("group-name")
      .returns(Left(DestroyGroupErr("group-name", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "DestroyGroup(group-name) failed. Details: DestroyGroupErr(group-name,java.lang.IllegalArgumentException: x"
      )
    })
    val actual = clientWithAudit.destroyGroup("group-name")
    assertDestroyGroupErr(actual, "group-name", "x")
  }

  test("getUserByWorkloadUsername logs success info") {
    (client.getUserByWorkloadUsername _).when("user").returns(Right(None))
    (audit.info _).expects("GetUserByWorkloadUserName(user) completed successfully")
    val actual = clientWithAudit.getUserByWorkloadUsername("user")
    assert(actual == Right(None))
  }

  test("getUserByWorkloadUsername logs error info") {
    (client.getUserByWorkloadUsername _)
      .when("user")
      .returns(Left(GetUserErr("user", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "GetUserByWorkloadUserName(user) failed. Details: GetUserErr(user,java.lang.IllegalArgumentException: x"
      )
    })
    assertGetUserErr(clientWithAudit.getUserByWorkloadUsername("user"), "user", "x")
  }

  test("getUser logs success info") {
    (client.getUserByWorkloadUsername _).when("user").returns(Right(None))
    (audit.info _).expects("GetUserByWorkloadUserName(user) completed successfully")
    val actual = clientWithAudit.getUserByWorkloadUsername("user")
    assert(actual == Right(None))
  }

  test("getUser logs error info") {
    (client.getUser _)
      .when("user")
      .returns(Left(GetUserErr("user", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith("GetUser(user) failed. Details: GetUserErr(user,java.lang.IllegalArgumentException: x")
    })
    assertGetUserErr(clientWithAudit.getUser("user"), "user", "x")
  }
}
