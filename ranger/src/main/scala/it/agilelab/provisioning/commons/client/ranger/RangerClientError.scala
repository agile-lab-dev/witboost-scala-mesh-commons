package it.agilelab.provisioning.commons.client.ranger

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.client.ranger.model.{ RangerPolicy, RangerRole, RangerSecurityZone }
import it.agilelab.provisioning.commons.http.HttpErrors

sealed trait RangerClientError extends Exception with Product with Serializable

object RangerClientError {

  final case class FindPolicyByIdErr(policyId: Int, error: HttpErrors)                        extends RangerClientError
  final case class FindPolicyByNameErr(policyName: String, error: HttpErrors)                 extends RangerClientError
  final case class CreatePolicyErr(policy: RangerPolicy, error: HttpErrors)                   extends RangerClientError
  final case class CreatePolicyEmptyResponseErr(policy: RangerPolicy)                         extends RangerClientError
  final case class UpdatePolicyErr(policy: RangerPolicy, error: HttpErrors)                   extends RangerClientError
  final case class UpdatePolicyEmptyResponseErr(policy: RangerPolicy)                         extends RangerClientError
  final case class FindSecurityZoneByNameErr(securityZoneName: String, error: HttpErrors)     extends RangerClientError
  final case class UpdateSecurityZoneErr(securityZone: RangerSecurityZone, error: HttpErrors) extends RangerClientError
  final case class UpdateSecurityZoneEmptyResponseErr(securityZone: RangerSecurityZone)       extends RangerClientError
  final case class CreateSecurityZoneErr(securityZone: RangerSecurityZone, error: HttpErrors) extends RangerClientError
  final case class CreateSecurityZoneEmptyResponseErr(securityZone: RangerSecurityZone)       extends RangerClientError
  final case class FindAllServicesErr(error: HttpErrors)                                      extends RangerClientError
  final case class DeletePolicyErr(policy: RangerPolicy, error: HttpErrors)                   extends RangerClientError
  final case class FindRoleByIdErr(roleId: Int, error: HttpErrors)                            extends RangerClientError
  final case class FindRoleByNameErr(roleName: String, error: HttpErrors)                     extends RangerClientError
  final case class CreateRoleErr(role: RangerRole, error: HttpErrors)                         extends RangerClientError
  final case class CreateRoleEmptyResponseErr(role: RangerRole)                               extends RangerClientError
  final case class UpdateRoleErr(role: RangerRole, error: HttpErrors)                         extends RangerClientError
  final case class UpdateRoleEmptyResponseErr(role: RangerRole)                               extends RangerClientError
  final case class DeleteRoleErr(role: RangerRole, error: HttpErrors)                         extends RangerClientError

  implicit val showRangerClientError: Show[RangerClientError] = Show.show {
    case e: FindPolicyByIdErr                  => show"FindPolicyByIdErr(${e.policyId},${e.error})"
    case e: FindPolicyByNameErr                => show"FindPolicyByNameErr(${e.policyName},${e.error})"
    case e: CreatePolicyErr                    => show"CreatePolicyErr(${e.policy.toString},${e.error})"
    case e: CreatePolicyEmptyResponseErr       => show"CreatePolicyEmptyResponseErr(${e.policy.toString})"
    case e: UpdatePolicyErr                    => show"UpdatePolicyErr(${e.policy.toString},${e.error})"
    case e: UpdatePolicyEmptyResponseErr       => show"UpdatePolicyEmptyResponseErr(${e.policy.toString})"
    case e: FindSecurityZoneByNameErr          => show"FindSecurityZoneByNameErr(${e.securityZoneName},${e.error})"
    case e: UpdateSecurityZoneErr              => show"UpdateSecurityZoneErr(${e.securityZone.toString},${e.error})"
    case e: UpdateSecurityZoneEmptyResponseErr => show"UpdateSecurityZoneEmptyResponseErr(${e.securityZone.toString})"
    case e: CreateSecurityZoneErr              => show"CreateSecurityZoneErr(${e.securityZone.toString},${e.error})"
    case e: CreateSecurityZoneEmptyResponseErr => show"CreateSecurityZoneEmptyResponseErr(${e.securityZone.toString})"
    case e: FindAllServicesErr                 => show"FindAllServicesErr(${e.error})"
    case e: DeletePolicyErr                    => show"DeletePolicyErr(${e.policy.toString},${e.error})"
    case e: FindRoleByIdErr                    => show"FindRoleByIdErr(${e.roleId},${e.error})"
    case e: FindRoleByNameErr                  => show"FindRoleByNameErr(${e.roleName},${e.error})"
    case e: CreateRoleErr                      => show"CreateRoleErr(${e.role.toString},${e.error})"
    case e: CreateRoleEmptyResponseErr         => show"CreateRoleEmptyResponseErr(${e.role.toString})"
    case e: UpdateRoleErr                      => show"UpdateRoleErr(${e.role.toString},${e.error})"
    case e: UpdateRoleEmptyResponseErr         => show"UpdateRoleEmptyResponseErr(${e.role.toString})"
    case e: DeleteRoleErr                      => show"DeleteRoleErr(${e.role.toString},${e.error})"
  }

}
