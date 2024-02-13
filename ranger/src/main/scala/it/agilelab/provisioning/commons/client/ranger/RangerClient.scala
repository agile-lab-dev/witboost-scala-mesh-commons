package it.agilelab.provisioning.commons.client.ranger

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.ranger.model.{
  RangerPolicy,
  RangerRole,
  RangerSecurityZone,
  RangerService
}
import it.agilelab.provisioning.commons.http.Auth.BasicCredential
import org.apache.ranger

/** RangerClient trait
  */
trait RangerClient {

  // --------------------------------
  // Policy methods
  // --------------------------------

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

  // --------------------------------
  // Security Zone methods
  // --------------------------------

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

  // --------------------------------
  // Service methods
  // --------------------------------

  /** Retrieve all the Ranger services
    * @return Right(Seq[RangerService]) if all works fine
    *         Left(RangerClientError) otherwise
    */
  def findAllServices: Either[RangerClientError, Seq[RangerService]]

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
  def findRoleById(id: Int): Either[RangerClientError, Option[RangerRole]]

  /** Retrieve a specific role by service name and role name
    *
    * @param name    : Role name
    * @return Right(Some(RangerRole)) if role exists
    *         Right(None) if role does not exists
    *         Left(RangerClientError) in case of error
    */
  def findRoleByName(name: String): Either[RangerClientError, Option[RangerRole]]

  /** Create a new RangerRole
    *
    * @param role : RangerRole definition
    * @return Right(RangerRole) if all works fine
    *         Left(RangerClientError) otherwise
    */
  def createRole(role: RangerRole): Either[RangerClientError, RangerRole]

  /** Update an existing RangerRole
    *
    * @param role : RangerRole definition
    * @return Right(RangerRole) if all works fine
    *         Left(RangerClientError) otherwise
    */
  def updateRole(role: RangerRole): Either[RangerClientError, RangerRole]

  /** Delete an existing RangerRole
    *
    * @param role : RangerRole definition
    * @return Right() if all works fine
    *         Left(RangerClientError) otherwise
    */
  def deleteRole(role: RangerRole): Either[RangerClientError, Unit]

}

object RangerAuthType {
  type RangerAuthType = RangerAuthType.Value
  object RangerAuthType extends Enumeration {
    val Simple: RangerAuthType.Value   = Value("simple")
    val Kerberos: RangerAuthType.Value = Value("kerberos")
  }
}

object RangerClient {
  def default(host: String, credential: BasicCredential): RangerClient = {
    val client = new ranger.RangerClient(
      host,
      RangerAuthType.RangerAuthType.Simple.toString,
      credential.username,
      credential.password,
      null
    )
    new RangerClientAdapter(client)
  }

  def defaultWithKerberos(host: String, principal: String, keytabPath: String): RangerClient = {
    val client =
      new ranger.RangerClient(host, RangerAuthType.RangerAuthType.Kerberos.toString, principal, keytabPath, null)
    new RangerClientAdapter(client)
  }

  def defaultWithAudit(host: String, credential: BasicCredential): RangerClient = {
    val client = new ranger.RangerClient(
      host,
      RangerAuthType.RangerAuthType.Simple.toString,
      credential.username,
      credential.password,
      null
    )
    new RangerClientAdapterWithAudit(
      new RangerClientAdapter(client),
      Audit.default("RangerClient(auth=Basic)")
    )
  }

  def defaultWithKerberosWithAudit(host: String, principal: String, keytabPath: String): RangerClient = {
    val client =
      new ranger.RangerClient(host, RangerAuthType.RangerAuthType.Kerberos.toString, principal, keytabPath, null)
    new RangerClientAdapterWithAudit(
      new RangerClientAdapter(client),
      Audit.default("RangerClient(auth=Kerberos)")
    )
  }
}
