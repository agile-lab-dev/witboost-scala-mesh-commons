package it.agilelab.provisioning.commons.client.ranger

import com.sun.jersey.api.client.ClientResponse
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.ranger.RangerClientError._
import it.agilelab.provisioning.commons.client.ranger.model.{
  ClientResponseMock,
  RangerPolicy,
  RangerRole,
  RangerSecurityZone,
  RangerSecurityZoneResources,
  RangerService
}
import it.agilelab.provisioning.commons.http.HttpErrors.ClientErr
import org.apache.ranger.RangerServiceException
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.apache.ranger

class RangerClientAdapterWithAuditTest extends AnyFunSuite with BeforeAndAfterAll with MockFactory {

  val defaultRangerClient: RangerClientAdapter = stub[RangerClientAdapter]
  val audit: Audit                             = mock[Audit]
  val rangerClient                             = new RangerClientAdapterWithAudit(defaultRangerClient, audit)

  test("findPolicyById logs success info") {
    val policy = RangerPolicy(
      id = 1,
      service = "a",
      name = "b",
      description = "c",
      isAuditEnabled = false,
      isEnabled = false,
      resources = Map.empty,
      policyItems = Seq.empty,
      serviceType = "d",
      policyLabels = Seq.empty,
      isDenyAllElse = false,
      zoneName = "e",
      policyPriority = 0
    )
    (defaultRangerClient.findPolicyById _).when(*).returns(Right(Some(policy)))
    inSequence(
      (audit.info _).expects("Executing FindPolicyById(1)").once(),
      (audit.info _).expects(
        "FindPolicyById(1) completed successfully"
      )
    )
    val actual = rangerClient.findPolicyById(1)
    assert(actual == Right(Some(policy)))
  }

  test("findPolicyById logs error info") {
    val exception = new RangerServiceException(
      ranger.RangerClient.GET_POLICY_BY_ID,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )

    (defaultRangerClient.findPolicyById _).when(*).returns(Left(FindPolicyByIdErr(1, exception)))
    inSequence(
      (audit.info _).expects("Executing FindPolicyById(1)").once(),
      (audit.error _).expects(
        where((s: String) => s.startsWith("FindPolicyById(1) failed. Details: FindPolicyByIdErr(1,"))
      )
    )
    val actual = rangerClient.findPolicyById(1)
    assert(actual == Left(FindPolicyByIdErr(1, exception)))
  }

  test("findPolicyByName logs success info") {
    val policy = RangerPolicy(
      id = 1,
      service = "a",
      name = "b",
      description = "c",
      isAuditEnabled = false,
      isEnabled = false,
      resources = Map.empty,
      policyItems = Seq.empty,
      serviceType = "d",
      policyLabels = Seq.empty,
      isDenyAllElse = false,
      zoneName = "e",
      policyPriority = 0
    )

    (defaultRangerClient.findPolicyByName _).when(*, *, *).returns(Right(Some(policy)))
    inSequence(
      (audit.info _)
        .expects("Executing FindPolicyByName(service=srv,name=pn,zoneName=None)")
        .once(),
      (audit.info _).expects(
        "FindPolicyByName(service=srv,name=pn,zoneName=None) completed successfully"
      )
    )
    val actual = rangerClient.findPolicyByName("srv", "pn", None)
    assert(actual == Right(Some(policy)))
  }

  test("findPolicyByName logs error info") {
    val exception = new RangerServiceException(
      ranger.RangerClient.GET_POLICY_BY_NAME,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )

    (defaultRangerClient.findPolicyByName _)
      .when(*, *, *)
      .returns(Left(FindPoliciesErr(Map("policyName" -> "pn", "serviceName" -> "srv"), exception)))
    inSequence(
      (audit.info _)
        .expects("Executing FindPolicyByName(service=srv,name=pn,zoneName=None)")
        .once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "FindPolicyByName(service=srv,name=pn,zoneName=None) failed. Details: FindPoliciesErr("
          )
        )
      )
    )
    val actual = rangerClient.findPolicyByName("srv", "pn", None)
    assert(actual == Left(FindPoliciesErr(Map("policyName" -> "pn", "serviceName" -> "srv"), exception)))
  }

  test("createPolicy logs success info") {
    val policy = RangerPolicy(
      id = 1,
      service = "a",
      name = "b",
      description = "c",
      isAuditEnabled = false,
      isEnabled = false,
      resources = Map.empty,
      policyItems = Seq.empty,
      serviceType = "d",
      policyLabels = Seq.empty,
      isDenyAllElse = false,
      zoneName = "e",
      policyPriority = 0
    )

    (defaultRangerClient.createPolicy _).when(*).returns(Right(policy))
    inSequence(
      (audit.info _)
        .expects("Executing CreatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0))")
        .once(),
      (audit.info _).expects(
        "CreatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0)) completed successfully"
      )
    )
    val actual = rangerClient.createPolicy(policy)
    assert(actual == Right(policy))
  }

  test("createPolicy logs error info") {
    val policy = RangerPolicy(
      id = 1,
      service = "a",
      name = "b",
      description = "c",
      isAuditEnabled = false,
      isEnabled = false,
      resources = Map.empty,
      policyItems = Seq.empty,
      serviceType = "d",
      policyLabels = Seq.empty,
      isDenyAllElse = false,
      zoneName = "e",
      policyPriority = 0
    )

    val exception = new RangerServiceException(
      ranger.RangerClient.CREATE_POLICY,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "x")
    )

    (defaultRangerClient.createPolicy _).when(*).returns(Left(CreatePolicyErr(policy, exception)))
    inSequence(
      (audit.info _)
        .expects("Executing CreatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0))")
        .once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "CreatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0)) failed. Details: CreatePolicyErr(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0),"
          )
        )
      )
    )
    val actual = rangerClient.createPolicy(policy)
    assert(actual == Left(CreatePolicyErr(policy, exception)))
  }

  test("updatePolicy logs success info") {
    val policy = RangerPolicy(
      id = 1,
      service = "a",
      name = "b",
      description = "c",
      isAuditEnabled = false,
      isEnabled = false,
      resources = Map.empty,
      policyItems = Seq.empty,
      serviceType = "d",
      policyLabels = Seq.empty,
      isDenyAllElse = false,
      zoneName = "e",
      policyPriority = 0
    )

    (defaultRangerClient.updatePolicy _).when(*).returns(Right(policy))
    inSequence(
      (audit.info _)
        .expects("Executing UpdatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0))")
        .once(),
      (audit.info _).expects(
        "UpdatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0)) completed successfully"
      )
    )
    val actual = rangerClient.updatePolicy(policy)
    assert(actual == Right(policy))
  }

  test("updatePolicy logs error info") {
    val policy = RangerPolicy(
      id = 1,
      service = "a",
      name = "b",
      description = "c",
      isAuditEnabled = false,
      isEnabled = false,
      resources = Map.empty,
      policyItems = Seq.empty,
      serviceType = "d",
      policyLabels = Seq.empty,
      isDenyAllElse = false,
      zoneName = "e",
      policyPriority = 0
    )

    val exception = new RangerServiceException(
      ranger.RangerClient.UPDATE_POLICY_BY_ID,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )

    (defaultRangerClient.updatePolicy _).when(*).returns(Left(CreatePolicyErr(policy, exception)))
    inSequence(
      (audit.info _)
        .expects("Executing UpdatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0))")
        .once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "UpdatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0)) failed. Details: CreatePolicyErr(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0),"
          )
        )
      )
    )
    val actual = rangerClient.updatePolicy(policy)
    assert(actual == Left(CreatePolicyErr(policy, exception)))
  }

  test("findSecurityZoneByName logs success info") {
    val zone: RangerSecurityZone = RangerSecurityZone(
      1,
      "zzz",
      Map(
        "service_name" -> RangerSecurityZoneResources(
          Seq(
            Map(
              "database" -> Seq("domain_*"),
              "column"   -> Seq("*"),
              "table"    -> Seq("*")
            )
          )
        )
      ),
      isEnabled = true,
      List("adminUser1", "adminUser2"),
      List("adminUserGroup1", "adminUserGroup2"),
      List("auditUser1", "auditUser2"),
      List("auditUserGroup1", "auditUserGroup2")
    )
    (defaultRangerClient.findSecurityZoneByName _).when(*).returns(Right(Some(zone)))
    inSequence(
      (audit.info _).expects("Executing FindSecurityZoneByName(zoneName=zzz)").once(),
      (audit.info _).expects(
        "FindSecurityZoneByName(zoneName=zzz) completed successfully"
      )
    )
    val actual                   = rangerClient.findSecurityZoneByName(zone.name)
    assert(actual == Right(Some(zone)))
  }

  test("findSecurityZoneByName logs error info") {

    val exception = new RangerServiceException(
      ranger.RangerClient.GET_ZONE_BY_NAME,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )

    (defaultRangerClient.findSecurityZoneByName _)
      .when(*)
      .returns(Left(FindSecurityZoneByNameErr("zone", exception)))
    inSequence(
      (audit.info _).expects("Executing FindSecurityZoneByName(zoneName=zzz)").once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith("FindSecurityZoneByName(zoneName=zzz) failed. Details: FindSecurityZoneByNameErr(zone,")
        )
      )
    )
    val actual = rangerClient.findSecurityZoneByName("zzz")
    assert(actual == Left(FindSecurityZoneByNameErr("zone", exception)))
  }

  test("updateSecurityZone logs success info") {
    val zone: RangerSecurityZone = RangerSecurityZone(
      1,
      "zzz",
      Map(
        "service_name" -> RangerSecurityZoneResources(
          Seq(
            Map(
              "database" -> Seq("domain_*"),
              "column"   -> Seq("*"),
              "table"    -> Seq("*")
            )
          )
        )
      ),
      isEnabled = true,
      List("adminUser1", "adminUser2"),
      List("adminUserGroup1", "adminUserGroup2"),
      List("auditUser1", "auditUser2"),
      List("auditUserGroup1", "auditUserGroup2")
    )

    (defaultRangerClient.updateSecurityZone _).when(*).returns(Right(zone))
    inSequence(
      (audit.info _)
        .expects(
          "Executing UpdateSecurityZone(RangerSecurityZone(1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2)))"
        )
        .once(),
      (audit.info _).expects(
        "UpdateSecurityZone(RangerSecurityZone(1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2))) completed successfully"
      )
    )
    val actual = rangerClient.updateSecurityZone(zone)
    assert(actual == Right(zone))
  }

  test("updateSecurityZone logs error info") {

    val exception = new RangerServiceException(
      ranger.RangerClient.UPDATE_ZONE_BY_ID,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )

    val zone: RangerSecurityZone = RangerSecurityZone(
      1,
      "zzz",
      Map(
        "service_name" -> RangerSecurityZoneResources(
          Seq(
            Map(
              "database" -> Seq("domain_*"),
              "column"   -> Seq("*"),
              "table"    -> Seq("*")
            )
          )
        )
      ),
      isEnabled = true,
      List("adminUser1", "adminUser2"),
      List("adminUserGroup1", "adminUserGroup2"),
      List("auditUser1", "auditUser2"),
      List("auditUserGroup1", "auditUserGroup2")
    )

    (defaultRangerClient.updateSecurityZone _).when(*).returns(Left(UpdateSecurityZoneErr(zone, exception)))
    inSequence(
      (audit.info _)
        .expects(
          "Executing UpdateSecurityZone(RangerSecurityZone(1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2)))"
        )
        .once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "UpdateSecurityZone(RangerSecurityZone(1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2))) failed. Details: UpdateSecurityZoneErr(RangerSecurityZone(1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2)),"
          )
        )
      )
    )
    val actual = rangerClient.updateSecurityZone(zone)
    assert(actual == Left(UpdateSecurityZoneErr(zone, exception)))
  }

  test("findAllServices logs success info") {
    val services = Seq(
      RangerService(
        1,
        isEnabled = true,
        "hive",
        "cm_hive",
        "Hadoop SQL",
        Map(
          "cluster.name"                  -> "cdpEnv",
          "tag.download.auth.users"       -> "hive,hdfs,impala",
          "password"                      -> "*****",
          "policy.download.auth.users"    -> "hive,hdfs,impala",
          "policy.grantrevoke.auth.users" -> "hive,impala",
          "enable.hive.metastore.lookup"  -> "true",
          "default.policy.users"          -> "impala,beacon,hue,admin,dpprofiler",
          "ranger.plugin.audit.filters"   -> "filter",
          "jdbc.driverClassName"          -> "org.apache.hive.jdbc.HiveDriver",
          "hive.site.file.path"           -> "hive-site.xml",
          "jdbc.url"                      -> "none",
          "username"                      -> "hive"
        )
      ),
      RangerService(
        2,
        isEnabled = true,
        "nifi",
        "cm_nifi",
        "Nifi",
        Map(
          "tag.download.auth.users"      -> "nifi",
          "cluster.name"                 -> "cdpEnv1",
          "policy.download.auth.users"   -> "nifi",
          "nifi.url"                     -> "https://mynifi.com",
          "service.admin.users"          -> "nifi",
          "nifi.ssl.use.default.context" -> "true",
          "ranger.plugin.audit.filters"  -> "[]",
          "nifi.authentication"          -> "SSL"
        )
      )
    )
    (defaultRangerClient.findAllServices _).when().returns(Right(services))
    inSequence(
      (audit.info _).expects("Executing FindAllServices").once(),
      (audit.info _).expects(
        "FindAllServices completed successfully"
      )
    )
    val actual   = rangerClient.findAllServices
    assert(actual == Right(services))
  }

  test("findAllServices logs error info") {

    val exception = new RangerServiceException(
      ranger.RangerClient.FIND_SERVICES,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )

    (defaultRangerClient.findAllServices _).when().returns(Left(FindAllServicesErr(exception)))
    inSequence(
      (audit.info _).expects("Executing FindAllServices").once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "FindAllServices failed. Details: FindAllServicesErr("
          )
        )
      )
    )
    val actual = rangerClient.findAllServices
    assert(actual == Left(FindAllServicesErr(exception)))
  }

  test("createSecurityZone logs success info") {
    val zone: RangerSecurityZone = RangerSecurityZone(
      -1,
      "zzz",
      Map(
        "service_name" -> RangerSecurityZoneResources(
          Seq(
            Map(
              "database" -> Seq("domain_*"),
              "column"   -> Seq("*"),
              "table"    -> Seq("*")
            )
          )
        )
      ),
      isEnabled = true,
      List("adminUser1", "adminUser2"),
      List("adminUserGroup1", "adminUserGroup2"),
      List("auditUser1", "auditUser2"),
      List("auditUserGroup1", "auditUserGroup2")
    )

    (defaultRangerClient.createSecurityZone _).when(*).returns(Right(zone.copy(id = 1)))
    inSequence(
      (audit.info _)
        .expects(
          "Executing CreateSecurityZone(zone=RangerSecurityZone(-1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2)))"
        )
        .once(),
      (audit.info _).expects(
        "CreateSecurityZone(zone=RangerSecurityZone(-1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2))) completed successfully"
      )
    )
    val actual = rangerClient.createSecurityZone(zone)
    assert(actual == Right(zone.copy(id = 1)))
  }

  test("createSecurityZone logs error info") {
    val exception = new RangerServiceException(
      ranger.RangerClient.CREATE_ZONE,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )

    val zone: RangerSecurityZone = RangerSecurityZone(
      -1,
      "zzz",
      Map(
        "service_name" -> RangerSecurityZoneResources(
          Seq(
            Map(
              "database" -> Seq("domain_*"),
              "column"   -> Seq("*"),
              "table"    -> Seq("*")
            )
          )
        )
      ),
      isEnabled = true,
      List("adminUser1", "adminUser2"),
      List("adminUserGroup1", "adminUserGroup2"),
      List("auditUser1", "auditUser2"),
      List("auditUserGroup1", "auditUserGroup2")
    )

    (defaultRangerClient.createSecurityZone _).when(*).returns(Left(CreateSecurityZoneErr(zone, exception)))
    inSequence(
      (audit.info _)
        .expects(
          "Executing CreateSecurityZone(zone=RangerSecurityZone(-1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2)))"
        )
        .once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "CreateSecurityZone(zone=RangerSecurityZone(-1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2))) failed. Details: CreateSecurityZoneErr(RangerSecurityZone(-1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2)),"
          )
        )
      )
    )
    val actual = rangerClient.createSecurityZone(zone)
    assert(actual == Left(CreateSecurityZoneErr(zone, exception)))
  }

  test("deletePolicy logs success info") {
    val policy = RangerPolicy(
      id = 1,
      service = "a",
      name = "b",
      description = "c",
      isAuditEnabled = false,
      isEnabled = false,
      resources = Map.empty,
      policyItems = Seq.empty,
      serviceType = "d",
      policyLabels = Seq.empty,
      isDenyAllElse = false,
      zoneName = "e",
      policyPriority = 0
    )

    (defaultRangerClient.deletePolicy _).when(*).returns(Right())
    inSequence(
      (audit.info _)
        .expects("Executing DeletePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0))")
        .once(),
      (audit.info _).expects(
        "DeletePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0)) completed successfully"
      )
    )
    val actual = rangerClient.deletePolicy(policy)
    assert(actual == Right())
  }

  test("deletePolicy logs error info") {
    val policy = RangerPolicy(
      id = 1,
      service = "a",
      name = "b",
      description = "c",
      isAuditEnabled = false,
      isEnabled = false,
      resources = Map.empty,
      policyItems = Seq.empty,
      serviceType = "d",
      policyLabels = Seq.empty,
      isDenyAllElse = false,
      zoneName = "e",
      policyPriority = 0
    )

    val exception = new RangerServiceException(
      ranger.RangerClient.DELETE_POLICY_BY_ID,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )

    (defaultRangerClient.deletePolicy _).when(*).returns(Left(DeletePolicyErr(policy, exception)))
    inSequence(
      (audit.info _)
        .expects("Executing DeletePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0))")
        .once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "DeletePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0)) failed. Details: DeletePolicyErr(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0),"
          )
        )
      )
    )
    val actual = rangerClient.deletePolicy(policy)
    assert(actual == Left(DeletePolicyErr(policy, exception)))
  }

  //----------------------
  // Roles
  //----------------------

  test("findRoleById logs success info") {
    val role = RangerRole.empty(name = "b", description = "c").copy(id = 1)

    (defaultRangerClient.findRoleById _).when(*).returns(Right(Some(role)))
    inSequence(
      (audit.info _).expects("Executing FindRoleById(1)").once(),
      (audit.info _).expects("FindRoleById(1) completed successfully")
    )
    val actual = rangerClient.findRoleById(1)
    assert(actual == Right(Some(role)))
  }

  test("findRoleById logs error info") {
    val exception = new RangerServiceException(
      ranger.RangerClient.GET_ROLE_BY_ID,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )

    (defaultRangerClient.findRoleById _).when(*).returns(Left(FindRoleByIdErr(1, exception)))
    inSequence(
      (audit.info _).expects("Executing FindRoleById(1)").once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "FindRoleById(1) failed. Details: FindRoleByIdErr(1,"
          )
        )
      )
    )
    val actual = rangerClient.findRoleById(1)
    assert(actual == Left(FindRoleByIdErr(1, exception)))
  }

  test("findRoleByName logs success info") {
    val role = RangerRole.empty(name = "name", description = "c").copy(id = 1)

    (defaultRangerClient.findRoleByName _).when(*).returns(Right(Some(role)))
    inSequence(
      (audit.info _).expects("Executing FindRoleByName(name=name)").once(),
      (audit.info _).expects("FindRoleByName(name=name) completed successfully")
    )
    val actual = rangerClient.findRoleByName("name")
    assert(actual == Right(Some(role)))
  }

  test("findRoleByName logs error info") {
    val exception = new RangerServiceException(
      ranger.RangerClient.GET_ROLE_BY_NAME,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "x")
    )

    (defaultRangerClient.findRoleByName _).when(*).returns(Left(FindRoleByNameErr("name", exception)))
    inSequence(
      (audit.info _).expects("Executing FindRoleByName(name=name)").once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "FindRoleByName(name=name) failed. Details: FindRoleByNameErr(name,"
          )
        )
      )
    )
    val actual = rangerClient.findRoleByName("name")
    assert(actual == Left(FindRoleByNameErr("name", exception)))
  }

  test("createRole logs success info") {
    val role = RangerRole.empty(name = "b", description = "c")

    (defaultRangerClient.createRole _).when(*).returns(Right(role))
    inSequence(
      (audit.info _).expects("Executing CreateRole(RangerRole(0,true,b,c,List(),List(),List()))").once(),
      (audit.info _).expects("CreateRole(RangerRole(0,true,b,c,List(),List(),List())) completed successfully")
    )
    val actual = rangerClient.createRole(role)
    assert(actual == Right(role))
  }

  test("createRole logs error info") {
    val exception = new RangerServiceException(
      ranger.RangerClient.CREATE_ROLE,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )
    val role      = RangerRole.empty(name = "b", description = "c")

    (defaultRangerClient.createRole _).when(*).returns(Left(CreateRoleErr(role, exception)))
    inSequence(
      (audit.info _).expects("Executing CreateRole(RangerRole(0,true,b,c,List(),List(),List()))").once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "CreateRole(RangerRole(0,true,b,c,List(),List(),List())) failed. Details: CreateRoleErr(RangerRole(0,true,b,c,List(),List(),List()),"
          )
        )
      )
    )
    val actual = rangerClient.createRole(role)
    assert(actual == Left(CreateRoleErr(role, exception)))
  }

  test("updateRole logs success info") {
    val role = RangerRole.empty(name = "b", description = "c").copy(id = 1)

    (defaultRangerClient.updateRole _).when(*).returns(Right(role))
    inSequence(
      (audit.info _).expects("Executing UpdateRole(RangerRole(1,true,b,c,List(),List(),List()))").once(),
      (audit.info _).expects("UpdateRole(RangerRole(1,true,b,c,List(),List(),List())) completed successfully")
    )
    val actual = rangerClient.updateRole(role)
    assert(actual == Right(role))
  }

  test("updateRole logs error info") {
    val exception = new RangerServiceException(
      ranger.RangerClient.UPDATE_ROLE_BY_ID,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )
    val role      = RangerRole.empty(name = "b", description = "c").copy(id = 1)

    (defaultRangerClient.updateRole _).when(*).returns(Left(UpdateRoleErr(role, exception)))
    inSequence(
      (audit.info _).expects("Executing UpdateRole(RangerRole(1,true,b,c,List(),List(),List()))").once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "UpdateRole(RangerRole(1,true,b,c,List(),List(),List())) failed. Details: UpdateRoleErr(RangerRole(1,true,b,c,List(),List(),List()),"
          )
        )
      )
    )
    val actual = rangerClient.updateRole(role)
    assert(actual == Left(UpdateRoleErr(role, exception)))
  }

  test("deleteRole logs success info") {
    val role = RangerRole.empty(name = "b", description = "c").copy(id = 1)

    (defaultRangerClient.deleteRole _).when(*).returns(Right(()))
    inSequence(
      (audit.info _).expects("Executing DeleteRole(RangerRole(1,true,b,c,List(),List(),List()))").once(),
      (audit.info _).expects("DeleteRole(RangerRole(1,true,b,c,List(),List(),List())) completed successfully")
    )
    val actual = rangerClient.deleteRole(role)
    assert(actual == Right(()))
  }

  test("deleteRole logs error info") {
    val exception = new RangerServiceException(
      ranger.RangerClient.DELETE_ROLE_BY_ID,
      ClientResponseMock(ClientResponse.Status.NOT_FOUND, "x")
    )
    val role      = RangerRole.empty(name = "b", description = "c").copy(id = 1)

    (defaultRangerClient.deleteRole _).when(*).returns(Left(DeleteRoleErr(role, exception)))
    inSequence(
      (audit.info _).expects("Executing DeleteRole(RangerRole(1,true,b,c,List(),List(),List()))").once(),
      (audit.error _).expects(
        where((s: String) =>
          s.startsWith(
            "DeleteRole(RangerRole(1,true,b,c,List(),List(),List())) failed. Details: DeleteRoleErr(RangerRole(1,true,b,c,List(),List(),List()),"
          )
        )
      )
    )
    val actual = rangerClient.deleteRole(role)
    assert(actual == Left(DeleteRoleErr(role, exception)))
  }
}
