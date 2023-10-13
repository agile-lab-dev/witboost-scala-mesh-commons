package it.agilelab.provisioning.commons.client.ranger

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.ranger.model.{ RangerPolicy, RangerSecurityZone, RangerService }
import it.agilelab.provisioning.commons.http.Auth.BasicCredential
import it.agilelab.provisioning.commons.http.Http

/** RangerClient trait
  */
trait RangerClient {

  /** Retrieve a specific policy by ID
    * @param id: policyId
    * @return Right(Some(RangerPolicy)) if policy exists
    *         Right(None) if policy does not exists
    *         Left(RangerClientError) in case of error
    */
  def findPolicyById(id: Int): Either[RangerClientError, Option[RangerPolicy]]

  /** Retrieve a specific policy by service name and policy name
    * @param service: Service name
    * @param name: Policy name
    * @return Right(Some(RangerPolicy)) if policy exists
    *         Right(None) if policy does not exists
    *         Left(RangerClientError) in case of error
    */
  def findPolicyByName(
    service: String,
    name: String,
    zoneName: Option[String]
  ): Either[RangerClientError, Option[RangerPolicy]]

  /** Create a new RangerPolicy
    * @param policy: RangerPolicy definition
    * @return Right(RangerPolicy) if all works fine
    *         Left(RangerClientError) otherwise
    */
  def createPolicy(policy: RangerPolicy): Either[RangerClientError, RangerPolicy]

  /** Update an existing RangerPolicy
    * @param policy: RangerPolicy definition
    * @return Right(RangerPolicy) if all works fine
    *         Left(RangerClientError) otherwise
    */
  def updatePolicy(policy: RangerPolicy): Either[RangerClientError, RangerPolicy]

  /** Delete an existing RangerPolicy
    *
    * @param policy  : RangerPolicy definition
    * @return Right() if all works fine
    *         Left(RangerClientError) otherwise
    */
  def deletePolicy(policy: RangerPolicy): Either[RangerClientError, Unit]

  /** Retrieve a specific security zone by name
    * @param zoneName: the name of the zone
    * @return Right(RangerSecurityZone) if all works fine
    *         Left(RangerClientError) otherwise
    */
  def findSecurityZoneByName(
    zoneName: String
  ): Either[RangerClientError, Option[RangerSecurityZone]]

  /** Update an existing RangerSecurityZone
    * @param zone: RangerSecurityZone definition
    * @return Right(RangerSecurityZone) if all works fine
    *         Left(RangerClientError) otherwise
    */
  def updateSecurityZone(zone: RangerSecurityZone): Either[RangerClientError, RangerSecurityZone]

  /** Create a RangerSecurityZone
    * @param zone: RangerSecurityZone definition
    * @return Right(RangerSecurityZone) if all works fine
    *         Left(RangerClientError) otherwise
    */
  def createSecurityZone(zone: RangerSecurityZone): Either[RangerClientError, RangerSecurityZone]

  /** Retrieve all the Ranger services
    * @return Right(Seq[RangerService]) if all works fine
    *         Left(RangerClientError) otherwise
    */
  def findAllServices: Either[RangerClientError, Seq[RangerService]]

}

object RangerClient {

  def default(host: String, credential: BasicCredential): RangerClient =
    new DefaultRangerClient(host, Http.default(), credential)

  def defaultWithAudit(host: String, credential: BasicCredential): RangerClient =
    new DefaultRangerClientWithAudit(
      new DefaultRangerClient(host, Http.defaultWithAudit(), credential),
      Audit.default("RangerClient")
    )
}
