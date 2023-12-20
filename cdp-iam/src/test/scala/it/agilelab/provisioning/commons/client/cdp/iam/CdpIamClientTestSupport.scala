package it.agilelab.provisioning.commons.client.cdp.iam

import com.cloudera.cdp.iam.model.ResourceAssignment
import it.agilelab.provisioning.commons.client.cdp.iam.CdpIamClientError._
import org.scalatest.EitherValues._

trait CdpIamClientTestSupport {
  def assertGetMachineUserErr[A](actual: Either[CdpIamClientError, A], user: String, error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[GetMachineUserErr])
    assert(actual.left.value.asInstanceOf[GetMachineUserErr].user == user)
    assert(actual.left.value.asInstanceOf[GetMachineUserErr].error.getMessage == error)
  }

  def assertCreateMachineUserErr[A](actual: Either[CdpIamClientError, A], user: String, error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[CreateMachineUserErr])
    assert(actual.left.value.asInstanceOf[CreateMachineUserErr].user == user)
    assert(actual.left.value.asInstanceOf[CreateMachineUserErr].error.getMessage == error)
  }

  def assertSetMachineUserWorkloadPasswordErr[A](
    actual: Either[CdpIamClientError, A],
    user: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[SetMachineUserWorkloadPasswordErr])
    assert(actual.left.value.asInstanceOf[SetMachineUserWorkloadPasswordErr].user == user)
    assert(actual.left.value.asInstanceOf[SetMachineUserWorkloadPasswordErr].error.getMessage == error)
  }

  def assertAddMachineUserToGroupErr[A](
    actual: Either[CdpIamClientError, A],
    user: String,
    group: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[AddMachineUserToGroupErr])
    assert(actual.left.value.asInstanceOf[AddMachineUserToGroupErr].user == user)
    assert(actual.left.value.asInstanceOf[AddMachineUserToGroupErr].group == group)
    assert(actual.left.value.asInstanceOf[AddMachineUserToGroupErr].error.getMessage == error)
  }

  def assertIsMachineUserInGroupErr[A](
    actual: Either[CdpIamClientError, A],
    user: String,
    group: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[IsMachineUserInGroupErr])
    assert(actual.left.value.asInstanceOf[IsMachineUserInGroupErr].user == user)
    assert(actual.left.value.asInstanceOf[IsMachineUserInGroupErr].group == group)
    assert(actual.left.value.asInstanceOf[IsMachineUserInGroupErr].error.getMessage == error)
  }

  def assertGetGroupErr[A](
    actual: Either[CdpIamClientError, A],
    group: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[GetGroupErr])
    assert(actual.left.value.asInstanceOf[GetGroupErr].group == group)
    assert(actual.left.value.asInstanceOf[GetGroupErr].error.getMessage == error)
  }

  def assertCreateGroupErr[A](
    actual: Either[CdpIamClientError, A],
    group: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[CreateGroupErr])
    assert(actual.left.value.asInstanceOf[CreateGroupErr].group == group)
    assert(actual.left.value.asInstanceOf[CreateGroupErr].error.getMessage == error)
  }

  def assertListResourceRolesErr[A](
    actual: Either[CdpIamClientError, A],
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[ListResourceRolesErr])
    assert(actual.left.value.asInstanceOf[ListResourceRolesErr].error.getMessage == error)
  }

  def assertListResourceRolesInGroupErr[A](
    actual: Either[CdpIamClientError, A],
    groupName: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[ListResourceRoleAssignmentsInGroupErr])
    assert(actual.left.value.asInstanceOf[ListResourceRoleAssignmentsInGroupErr].group == groupName)
    assert(actual.left.value.asInstanceOf[ListResourceRoleAssignmentsInGroupErr].error.getMessage == error)
  }

  def assertAssignResourceRoleToGroupErr[A](
    actual: Either[CdpIamClientError, A],
    groupName: String,
    ra: ResourceAssignment,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[AssignResourceRoleToGroupErr])
    assert(actual.left.value.asInstanceOf[AssignResourceRoleToGroupErr].group == groupName)
    assert(actual.left.value.asInstanceOf[AssignResourceRoleToGroupErr].resourceCrn == ra.getResourceCrn)
    assert(actual.left.value.asInstanceOf[AssignResourceRoleToGroupErr].resourceRoleCrn == ra.getResourceRoleCrn)
    assert(actual.left.value.asInstanceOf[AssignResourceRoleToGroupErr].error.getMessage == error)
  }

  def assertUnassignResourceRoleToGroupErr[A](
    actual: Either[CdpIamClientError, A],
    groupName: String,
    ra: ResourceAssignment,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[UnassignResourceRoleToGroupErr])
    assert(actual.left.value.asInstanceOf[UnassignResourceRoleToGroupErr].group == groupName)
    assert(actual.left.value.asInstanceOf[UnassignResourceRoleToGroupErr].resourceCrn == ra.getResourceCrn)
    assert(actual.left.value.asInstanceOf[UnassignResourceRoleToGroupErr].resourceRoleCrn == ra.getResourceRoleCrn)
    assert(actual.left.value.asInstanceOf[UnassignResourceRoleToGroupErr].error.getMessage == error)
  }

  def assertCreateMachineUserAccessKeyErr[A](
    actual: Either[CdpIamClientError, A],
    user: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[CreateMachineUserAccessKeyErr])
    assert(actual.left.value.asInstanceOf[CreateMachineUserAccessKeyErr].user == user)
    assert(actual.left.value.asInstanceOf[CreateMachineUserAccessKeyErr].error.getMessage == error)
  }

  def assertAccessKeyExistsErr[A](
    actual: Either[CdpIamClientError, A],
    accessKey: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[AccessKeyExistsErr])
    assert(actual.left.value.asInstanceOf[AccessKeyExistsErr].accessKeyId == accessKey)
    assert(actual.left.value.asInstanceOf[AccessKeyExistsErr].error.getMessage == error)
  }

  def assertDestroyMachineUserErr[A](
    actual: Either[CdpIamClientError, A],
    user: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[DestroyMachineUserErr])
    assert(actual.left.value.asInstanceOf[DestroyMachineUserErr].user == user)
    assert(actual.left.value.asInstanceOf[DestroyMachineUserErr].error.getMessage == error)
  }

  def assertDestroyGroupErr[A](
    actual: Either[CdpIamClientError, A],
    group: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[DestroyGroupErr])
    assert(actual.left.value.asInstanceOf[DestroyGroupErr].group == group)
    assert(actual.left.value.asInstanceOf[DestroyGroupErr].error.getMessage == error)
  }

  def assertGetUserErr[A](
    actual: Either[CdpIamClientError, A],
    user: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[GetUserErr])
    assert(actual.left.value.asInstanceOf[GetUserErr].user == user)
    assert(actual.left.value.asInstanceOf[GetUserErr].error.getMessage == error)
  }

}
