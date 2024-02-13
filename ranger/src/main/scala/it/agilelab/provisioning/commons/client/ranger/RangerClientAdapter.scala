package it.agilelab.provisioning.commons.client.ranger

import io.circe.generic.auto._
import it.agilelab.provisioning.commons.client.ranger.RangerClientError._
import it.agilelab.provisioning.commons.client.ranger.model._
import it.agilelab.provisioning.commons.http.HttpErrors._
import org.apache.ranger
import org.apache.ranger.RangerServiceException

import scala.jdk.CollectionConverters.{ CollectionHasAsScala, MapHasAsJava }
import scala.util.{ Failure, Success, Try }

/** Default Range Client implementation
  * @param host a String containing the ranger host; the string must NOT ends with '/'
  * @param http an Http instance
  * @param credential a BasicCredential instance
  */
class RangerClientAdapter(
  rangerClient: ranger.RangerClient
  /*
  host: String,
  http: Http,
  credential: BasicCredential
   */
) extends RangerClient {

  val PARAM_SERVICE_NAME = "serviceName"
  val PARAM_ROLE_NAME    = "roleName"
  val PARAM_ZONE_NAME    = "zoneName"
  val PARAM_POLICY_NAME  = "policyName"

  /** Retrieve a specific policy by ID
    *
    * @param id : policyId
    * @return Right(RangerPolicy) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def findPolicyById(
    id: Int
  ): Either[RangerClientError, Option[RangerPolicy]] =
    Try(rangerClient.getPolicy(id)) match {
      case Success(r) => Right(Some(r))

      case Failure(err: RangerServiceException)
          if err.getStatus != null && List(400, 404).contains(err.getStatus.getStatusCode) =>
        Right(None)

      case Failure(e) => Left(FindPolicyByIdErr(id, e))
    }

  /** Retrieve a specific policy by service name and policy name
    *
    * @param service  : Service name
    * @param name     : Policy name
    * @param zoneName     : zone name, Optional
    * @return Right(Some(RangerPolicy)) if policy exists
    *         Right(None) if policy does not exists
    *         Left(RangerClientError) in case of error
    *
    *  If zone exists try to fetch the policy for the specified security zone
    */
  override def findPolicyByName(
    service: String,
    name: String,
    zoneName: Option[String]
  ): Either[RangerClientError, Option[RangerPolicy]] = {
    val params = Map(
      PARAM_SERVICE_NAME -> service,
      PARAM_POLICY_NAME  -> name
    )
    zoneName
      .map(zone => findPolicy(params + (PARAM_ZONE_NAME -> zone)))
      .getOrElse(findPolicy(params))
  }

  /** Create a new RangerPolicy
    *
    * @param policy  : RangerPolicy definition
    * @return Right(RangerPolicy) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def createPolicy(
    policy: RangerPolicy
  ): Either[RangerClientError, RangerPolicy] =
    Try(rangerClient.createPolicy(policy)) match {
      case Success(p) => Right(p)
      case Failure(e) => Left(CreatePolicyErr(policy, e))
    }

  /** Update an existing RangerPolicy
    *
    * @param policy : RangerPolicy definition
    * @return Right(RangerPolicy) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def updatePolicy(
    policy: RangerPolicy
  ): Either[RangerClientError, RangerPolicy] =
    Try(rangerClient.updatePolicy(policy.id, policy)) match {
      case Success(p) => Right(p)
      case Failure(e) => Left(UpdatePolicyErr(policy, e))
    }

  override def deletePolicy(policy: RangerPolicy): Either[RangerClientError, Unit] =
    Try(rangerClient.deletePolicy(policy.id)) match {
      case Success(_) => Right()
      case Failure(e) => Left(DeletePolicyErr(policy, e))
    }

  /** Retrieve a specific security zone by name
    * @param zoneName: the name of the zone
    * @return Right(RangerSecurityZone) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def findSecurityZoneByName(
    zoneName: String
  ): Either[RangerClientError, Option[RangerSecurityZone]] =
    Try(rangerClient.getSecurityZone(zoneName)) match {
      case Success(sz) => Right(Some(sz))

      case Failure(err: RangerServiceException)
          if err.getStatus != null && List(400, 404).contains(err.getStatus.getStatusCode) =>
        Right(None)

      case Failure(e) => Left(FindSecurityZoneByNameErr(zoneName, e))
    }

  /** Update an existing RangerSecurityZone
    * @param zone : RangerSecurityZone definition
    * @return Right(RangerSecurityZone) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def updateSecurityZone(
    zone: RangerSecurityZone
  ): Either[RangerClientError, RangerSecurityZone] =
    Try(rangerClient.updateSecurityZone(zone.id, zone)) match {
      case Success(sz) => Right(sz)
      case Failure(e)  => Left(UpdateSecurityZoneErr(zone, e))
    }

  /** Create a RangerSecurityZone
    * @param zone: RangerSecurityZone definition
    * @return Right(RangerSecurityZone) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def createSecurityZone(
    zone: RangerSecurityZone
  ): Either[RangerClientError, RangerSecurityZone] =
    Try(rangerClient.createSecurityZone(zone)) match {
      case Success(sz) => Right(sz)
      case Failure(e)  => Left(CreateSecurityZoneErr(zone, e))
    }

  /** Retrieve all the Ranger services
    * @return Right(List[RangerService]) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def findAllServices: Either[RangerClientError, Seq[RangerService]] =
    Try(rangerClient.findServices(Map.empty[String, String].asJava)) match {
      case Success(r) => Right(r)
      case Failure(e) => Left(FindAllServicesErr(e))
    }

  private def findPolicy(
    searchParams: Map[String, String]
  ): Either[RangerClientError, Option[RangerPolicy]] =
    Try(rangerClient.findPolicies(searchParams.asJava)) match {
      case Success(p) => Right(p.asScala.headOption.map(RangerPolicy.policyFromRangerModel))
      case Failure(e) => Left(FindPoliciesErr(searchParams, e))
    }

  // --------------------------------
  // Roles methods
  // --------------------------------

  /** Retrieve a specific role by ID
    *
    * @param id : roleId
    * @return Right(Some(RangerRole)) if role exists
    *         Right(None) if role does not exists
    *         Left(RangerClientError) in case of error
    */
  override def findRoleById(id: Int): Either[RangerClientError, Option[RangerRole]] =
    Try(rangerClient.getRole(id)) match {
      case Success(r) => Right(Some(r))

      case Failure(err: RangerServiceException)
          if err.getStatus != null && List(400, 404).contains(err.getStatus.getStatusCode) =>
        Right(None)

      case Failure(e) => Left(FindRoleByIdErr(id, e))
    }

  /** Retrieve a specific role by service name and role name
    *
    * @param service : Service name
    * @param name    : Role name
    * @return Right(Some(RangerRole)) if role exists
    *         Right(None) if role does not exists
    *         Left(RangerClientError) in case of error
    */
  override def findRoleByName(name: String): Either[RangerClientError, Option[RangerRole]] =
    Try(rangerClient.findRoles(Map(PARAM_ROLE_NAME -> name).asJava)) match {
      case Success(r) => Right(r.asScala.headOption.map(RangerRole.roleFromRangerModel))
      case Failure(e) => Left(FindRoleByNameErr(name, e))
    }

  /** Create a new RangerRole
    *
    * @param role : RangerRole definition
    * @return Right(RangerRole) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def createRole(role: RangerRole): Either[RangerClientError, RangerRole] =
    Try(rangerClient.createRole("", role)) match {
      case Success(r) => Right(r)
      case Failure(e) => Left(CreateRoleErr(role, e))
    }

  /** Update an existing RangerRole
    *
    * @param role : RangerRole definition
    * @return Right(RangerRole) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def updateRole(role: RangerRole): Either[RangerClientError, RangerRole] =
    Try(rangerClient.updateRole(role.id, role)) match {
      case Success(r) => Right(r)
      case Failure(e) => Left(UpdateRoleErr(role, e))
    }

  /** Delete an existing RangerRole
    *
    * @param role : RangerRole definition
    * @return Right() if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def deleteRole(role: RangerRole): Either[RangerClientError, Unit] =
    Try(rangerClient.deleteRole(role.id)) match {
      case Success(_) => Right()
      case Failure(e) => Left(DeleteRoleErr(role, e))
    }
}
