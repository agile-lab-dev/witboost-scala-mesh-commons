package it.agilelab.provisioning.commons.client.ranger

import io.circe.generic.auto._
import it.agilelab.provisioning.commons.client.ranger.RangerClientError._
import it.agilelab.provisioning.commons.client.ranger.model._
import it.agilelab.provisioning.commons.http.Auth.BasicCredential
import it.agilelab.provisioning.commons.http.Http
import it.agilelab.provisioning.commons.http.HttpErrors._

/** Default Range Client implementation
  * @param host a String containing the ranger host; the string must NOT ends with '/'
  * @param http an Http instance
  * @param credential a BasicCredential instance
  */
class DefaultRangerClient(
  host: String,
  http: Http,
  credential: BasicCredential
) extends RangerClient {

  private val V2_URI_BASE    = s"https://$host/service/public/v2/api"
  private val V2_POLICY_API  = s"$V2_URI_BASE/policy"
  private val V2_SERVICE_API = s"$V2_URI_BASE/service"

  /** Retrieve a specific policy by ID
    *
    * @param id : policyId
    * @return Right(RangerPolicy) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def findPolicyById(
    id: Int
  ): Either[RangerClientError, Option[RangerPolicy]] =
    http.get[RangerPolicy](s"$V2_POLICY_API/${id.toString}", Map.empty, credential) match {
      case Right(r)                      => Right(Some(r))
      case Left(ClientErr(404 | 400, _)) => Right(None)
      case Left(e)                       => Left(FindPolicyByIdErr(id, e))
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
  ): Either[RangerClientError, Option[RangerPolicy]] =
    zoneName
      .map(findPolicyByServiceZonesName(service, name, _))
      .getOrElse(findPolicyByServiceName(service, name))

  /** Create a new RangerPolicy
    *
    * @param policy  : RangerPolicy definition
    * @return Right(RangerPolicy) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def createPolicy(
    policy: RangerPolicy
  ): Either[RangerClientError, RangerPolicy] =
    http.post[RangerPolicy, RangerPolicy](s"$V2_POLICY_API", Map.empty, policy, credential) match {
      case Right(Some(p)) => Right(p)
      case Right(None)    => Left(CreatePolicyEmptyResponseErr(policy))
      case Left(e)        => Left(CreatePolicyErr(policy, e))
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
    http.put[RangerPolicy, RangerPolicy](s"$V2_POLICY_API/${policy.id.toString}", Map.empty, policy, credential) match {
      case Right(Some(p)) => Right(p)
      case Right(None)    => Left(UpdatePolicyEmptyResponseErr(policy))
      case Left(e)        => Left(UpdatePolicyErr(policy, e))
    }

  override def deletePolicy(policy: RangerPolicy): Either[RangerClientError, Unit] =
    http.delete[Unit](s"$V2_POLICY_API/${policy.id.toString}", Map.empty, credential) match {
      case Right(_) => Right()
      case Left(e)  => Left(DeletePolicyErr(policy, e))
    }

  /** Retrieve a specific security zone by name
    * @param zoneName: the name of the zone
    * @return Right(RangerSecurityZone) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def findSecurityZoneByName(
    zoneName: String
  ): Either[RangerClientError, Option[RangerSecurityZone]] =
    http.get[RangerSecurityZone](
      s"$V2_URI_BASE/zones/name/$zoneName",
      Map("Accept" -> "application/json"),
      credential
    ) match {
      case Right(p)                      => Right(Some(p))
      case Left(ClientErr(404 | 400, _)) => Right(None)
      case Left(e)                       => Left(FindSecurityZoneByNameErr(zoneName, e))
    }

  /** Update an existing RangerSecurityZone
    * @param zone : RangerSecurityZone definition
    * @return Right(RangerSecurityZone) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def updateSecurityZone(
    zone: RangerSecurityZone
  ): Either[RangerClientError, RangerSecurityZone] =
    http.put[RangerSecurityZone, RangerSecurityZone](
      s"$V2_URI_BASE/zones/${zone.id.toString}",
      Map("Accept" -> "application/json"),
      zone,
      credential
    ) match {
      case Right(Some(p)) => Right(p)
      case Right(None)    => Left(UpdateSecurityZoneEmptyResponseErr(zone))
      case Left(e)        => Left(UpdateSecurityZoneErr(zone, e))
    }

  /** Create a RangerSecurityZone
    * @param zone: RangerSecurityZone definition
    * @return Right(RangerSecurityZone) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def createSecurityZone(
    zone: RangerSecurityZone
  ): Either[RangerClientError, RangerSecurityZone] =
    http.post[RangerSecurityZone, RangerSecurityZone](
      s"$V2_URI_BASE/zones",
      Map("Accept" -> "application/json"),
      zone,
      credential
    ) match {
      case Right(Some(p)) => Right(p)
      case Right(None)    => Left(CreateSecurityZoneEmptyResponseErr(zone))
      case Left(e)        => Left(CreateSecurityZoneErr(zone, e))
    }

  /** Retrieve all the Ranger services
    * @return Right(List[RangerService]) if all works fine
    *         Left(RangerClientError) otherwise
    */
  override def findAllServices: Either[RangerClientError, Seq[RangerService]] =
    http.get[Seq[RangerService]](s"$V2_SERVICE_API", Map.empty, credential) match {
      case Right(r) => Right(r)
      case Left(e)  => Left(FindAllServicesErr(e))
    }

  private def findPolicyByServiceName(
    service: String,
    name: String
  ): Either[RangerClientError, Option[RangerPolicy]] =
    http.get[RangerPolicy](s"$V2_SERVICE_API/$service/policy/$name", Map.empty, credential) match {
      case Right(b)                => Right(Some(b))
      case Left(ClientErr(404, _)) => Right(None)
      case Left(e)                 => Left(FindPolicyByNameErr(name, e))
    }

  private def findPolicyByServiceZonesName(
    service: String,
    name: String,
    zone: String
  ): Either[RangerClientError, Option[RangerPolicy]] =
    http.get[Seq[RangerPolicy]](
      s"$V2_SERVICE_API/$service/policy?policyName=$name&zoneName=$zone",
      Map.empty,
      credential
    ) match {
      case Right(b)                => Right(b.headOption)
      case Left(ClientErr(404, _)) => Right(None)
      case Left(e)                 => Left(FindPolicyByNameErr(name, e))
    }

}
