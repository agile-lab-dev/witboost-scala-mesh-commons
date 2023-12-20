package it.agilelab.provisioning.commons.client.cdp.iam

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.showable.ShowableOps._

sealed trait CdpIamClientError extends Exception with Product with Serializable
object CdpIamClientError {
  final case class CdpIamClientInitError(error: Throwable)                                 extends CdpIamClientError
  final case class GetMachineUserErr(user: String, error: Throwable)                       extends CdpIamClientError
  final case class CreateMachineUserErr(user: String, error: Throwable)                    extends CdpIamClientError
  final case class SetMachineUserWorkloadPasswordErr(user: String, error: Throwable)       extends CdpIamClientError
  final case class AddMachineUserToGroupErr(user: String, group: String, error: Throwable) extends CdpIamClientError
  final case class IsMachineUserInGroupErr(user: String, group: String, error: Throwable)  extends CdpIamClientError
  final case class GetGroupErr(group: String, error: Throwable)                            extends CdpIamClientError
  final case class CreateGroupErr(group: String, error: Throwable)                         extends CdpIamClientError
  final case class ListResourceRolesErr(error: Throwable)                                  extends CdpIamClientError
  final case class ListResourceRoleAssignmentsInGroupErr(group: String, error: Throwable)  extends CdpIamClientError
  final case class AssignResourceRoleToGroupErr(
    group: String,
    resourceCrn: String,
    resourceRoleCrn: String,
    error: Throwable
  )                                                                                        extends CdpIamClientError
  final case class UnassignResourceRoleToGroupErr(
    group: String,
    resourceCrn: String,
    resourceRoleCrn: String,
    error: Throwable
  )                                                                                        extends CdpIamClientError
  final case class CreateMachineUserAccessKeyErr(user: String, error: Throwable)           extends CdpIamClientError
  final case class AccessKeyExistsErr(accessKeyId: String, error: Throwable)               extends CdpIamClientError
  final case class DestroyMachineUserErr(user: String, error: Throwable)                   extends CdpIamClientError
  final case class DestroyGroupErr(group: String, error: Throwable)                        extends CdpIamClientError

  final case class GetUserErr(user: String, error: Throwable) extends CdpIamClientError

  implicit val showCdpIamClientError: Show[CdpIamClientError] = Show.show {
    case e: CdpIamClientInitError                 => show"CdpIamClientInitError(${e.error})"
    case e: GetMachineUserErr                     => show"GetMachineUserErr(${e.user},${e.error})"
    case e: CreateMachineUserErr                  => show"CreateMachineUserErr(${e.user},${e.error})"
    case e: SetMachineUserWorkloadPasswordErr     => show"SetMachineUserWorkloadPasswordErr(${e.user},${e.error})"
    case e: AddMachineUserToGroupErr              => show"AddMachineUserToGroupErr(${e.user},${e.group},${e.error})"
    case e: IsMachineUserInGroupErr               => show"IsMachineUserInGroupErr(${e.user},${e.group},${e.error})"
    case e: GetGroupErr                           => show"GetGroupErr(${e.group},${e.error})"
    case e: CreateGroupErr                        => show"CreateGroupErr(${e.group},${e.error})"
    case e: ListResourceRolesErr                  => show"ListResourceRolesErr(${e.error})"
    case e: ListResourceRoleAssignmentsInGroupErr => show"ListResourceRoleAssignmentsInGroupErr(${e.group},${e.error})"
    case e: AssignResourceRoleToGroupErr          =>
      show"AssignResourceRoleToGroupErr(${e.group},${e.resourceCrn},${e.resourceRoleCrn},${e.error})"
    case e: UnassignResourceRoleToGroupErr        =>
      show"UnassignResourceRoleToGroupErr(${e.group},${e.resourceCrn},${e.resourceRoleCrn},${e.error})"
    case e: CreateMachineUserAccessKeyErr         => show"CreateMachineUserAccessKeyErr(${e.user},${e.error})"
    case e: AccessKeyExistsErr                    => show"AccessKeyExistsErr(${e.accessKeyId},${e.error})"
    case e: DestroyMachineUserErr                 => show"DestroyMachineUserErr(${e.user},${e.error})"
    case e: DestroyGroupErr                       => show"DestroyGroupErr(${e.group},${e.error})"
    case e: GetUserErr                            => show"GetUserErr(${e.user},${e.error})"
  }
}
