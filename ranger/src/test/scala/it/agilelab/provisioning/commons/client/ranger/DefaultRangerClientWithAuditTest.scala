package it.agilelab.provisioning.commons.client.ranger

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.ranger.RangerClientError._
import it.agilelab.provisioning.commons.client.ranger.model.{
  RangerPolicy,
  RangerRole,
  RangerSecurityZone,
  RangerSecurityZoneResources,
  RangerService
}
import it.agilelab.provisioning.commons.http.HttpErrors.ClientErr
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

class DefaultRangerClientWithAuditTest extends AnyFunSuite with BeforeAndAfterAll with MockFactory {

  val defaultRangerClient: DefaultRangerClient = stub[DefaultRangerClient]
  val audit: Audit                             = mock[Audit]
  val rangerClient                             = new DefaultRangerClientWithAudit(defaultRangerClient, audit)

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
    (defaultRangerClient.findPolicyById _).when(*).returns(Left(FindPolicyByIdErr(1, ClientErr(404, "x"))))
    inSequence(
      (audit.info _).expects("Executing FindPolicyById(1)").once(),
      (audit.error _).expects(
        "FindPolicyById(1) failed. Details: FindPolicyByIdErr(1,ClientErr(404,x))"
      )
    )
    val actual = rangerClient.findPolicyById(1)
    assert(actual == Left(FindPolicyByIdErr(1, ClientErr(404, "x"))))
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
    (defaultRangerClient.findPolicyByName _).when(*, *, *).returns(Left(FindPolicyByNameErr("pn", ClientErr(404, "x"))))
    inSequence(
      (audit.info _)
        .expects("Executing FindPolicyByName(service=srv,name=pn,zoneName=None)")
        .once(),
      (audit.error _).expects(
        "FindPolicyByName(service=srv,name=pn,zoneName=None) failed. Details: FindPolicyByNameErr(pn,ClientErr(404,x))"
      )
    )
    val actual = rangerClient.findPolicyByName("srv", "pn", None)
    assert(actual == Left(FindPolicyByNameErr("pn", ClientErr(404, "x"))))
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

    (defaultRangerClient.createPolicy _).when(*).returns(Left(CreatePolicyErr(policy, ClientErr(404, "x"))))
    inSequence(
      (audit.info _)
        .expects("Executing CreatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0))")
        .once(),
      (audit.error _).expects(
        "CreatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0)) failed. Details: CreatePolicyErr(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0),ClientErr(404,x))"
      )
    )
    val actual = rangerClient.createPolicy(policy)
    assert(actual == Left(CreatePolicyErr(policy, ClientErr(404, "x"))))
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

    (defaultRangerClient.updatePolicy _).when(*).returns(Left(CreatePolicyErr(policy, ClientErr(404, "x"))))
    inSequence(
      (audit.info _)
        .expects("Executing UpdatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0))")
        .once(),
      (audit.error _).expects(
        "UpdatePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0)) failed. Details: CreatePolicyErr(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0),ClientErr(404,x))"
      )
    )
    val actual = rangerClient.updatePolicy(policy)
    assert(actual == Left(CreatePolicyErr(policy, ClientErr(404, "x"))))
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
    (defaultRangerClient.findSecurityZoneByName _)
      .when(*)
      .returns(Left(FindSecurityZoneByNameErr("zone", ClientErr(404, "x"))))
    inSequence(
      (audit.info _).expects("Executing FindSecurityZoneByName(zoneName=zzz)").once(),
      (audit.error _).expects(
        "FindSecurityZoneByName(zoneName=zzz) failed. Details: FindSecurityZoneByNameErr(zone,ClientErr(404,x))"
      )
    )
    val actual = rangerClient.findSecurityZoneByName("zzz")
    assert(actual == Left(FindSecurityZoneByNameErr("zone", ClientErr(404, "x"))))
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

    (defaultRangerClient.updateSecurityZone _).when(*).returns(Left(UpdateSecurityZoneErr(zone, ClientErr(404, ""))))
    inSequence(
      (audit.info _)
        .expects(
          "Executing UpdateSecurityZone(RangerSecurityZone(1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2)))"
        )
        .once(),
      (audit.error _).expects(
        "UpdateSecurityZone(RangerSecurityZone(1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2))) failed. Details: UpdateSecurityZoneErr(RangerSecurityZone(1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2)),ClientErr(404,))"
      )
    )
    val actual = rangerClient.updateSecurityZone(zone)
    assert(actual == Left(UpdateSecurityZoneErr(zone, ClientErr(404, ""))))
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
    (defaultRangerClient.findAllServices _).when().returns(Left(FindAllServicesErr(ClientErr(404, "x"))))
    inSequence(
      (audit.info _).expects("Executing FindAllServices").once(),
      (audit.error _).expects(
        "FindAllServices failed. Details: FindAllServicesErr(ClientErr(404,x))"
      )
    )
    val actual = rangerClient.findAllServices
    assert(actual == Left(FindAllServicesErr(ClientErr(404, "x"))))
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

    (defaultRangerClient.createSecurityZone _).when(*).returns(Left(CreateSecurityZoneErr(zone, ClientErr(404, "x"))))
    inSequence(
      (audit.info _)
        .expects(
          "Executing CreateSecurityZone(zone=RangerSecurityZone(-1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2)))"
        )
        .once(),
      (audit.error _).expects(
        "CreateSecurityZone(zone=RangerSecurityZone(-1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2))) failed. Details: CreateSecurityZoneErr(RangerSecurityZone(-1,zzz,Map(service_name -> RangerSecurityZoneResources(List(Map(database -> List(domain_*), column -> List(*), table -> List(*))))),true,List(adminUser1, adminUser2),List(adminUserGroup1, adminUserGroup2),List(auditUser1, auditUser2),List(auditUserGroup1, auditUserGroup2)),ClientErr(404,x))"
      )
    )
    val actual = rangerClient.createSecurityZone(zone)
    assert(actual == Left(CreateSecurityZoneErr(zone, ClientErr(404, "x"))))
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

    (defaultRangerClient.deletePolicy _).when(*).returns(Left(DeletePolicyErr(policy, ClientErr(404, "x"))))
    inSequence(
      (audit.info _)
        .expects("Executing DeletePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0))")
        .once(),
      (audit.error _).expects(
        "DeletePolicy(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0)) failed. Details: DeletePolicyErr(RangerPolicy(1,a,b,c,false,false,Map(),List(),d,List(),false,e,0),ClientErr(404,x))"
      )
    )
    val actual = rangerClient.deletePolicy(policy)
    assert(actual == Left(DeletePolicyErr(policy, ClientErr(404, "x"))))
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
    (defaultRangerClient.findRoleById _).when(*).returns(Left(FindRoleByIdErr(1, ClientErr(404, "x"))))
    inSequence(
      (audit.info _).expects("Executing FindRoleById(1)").once(),
      (audit.error _).expects(
        "FindRoleById(1) failed. Details: FindRoleByIdErr(1,ClientErr(404,x))"
      )
    )
    val actual = rangerClient.findRoleById(1)
    assert(actual == Left(FindRoleByIdErr(1, ClientErr(404, "x"))))
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
    (defaultRangerClient.findRoleByName _).when(*).returns(Left(FindRoleByNameErr("name", ClientErr(404, "x"))))
    inSequence(
      (audit.info _).expects("Executing FindRoleByName(name=name)").once(),
      (audit.error _).expects(
        "FindRoleByName(name=name) failed. Details: FindRoleByNameErr(name,ClientErr(404,x))"
      )
    )
    val actual = rangerClient.findRoleByName("name")
    assert(actual == Left(FindRoleByNameErr("name", ClientErr(404, "x"))))
  }

  test("createRole logs success info") {
    val role = RangerRole.empty(name = "b", description = "c")

    (defaultRangerClient.createRole _).when(*).returns(Right(role))
    inSequence(
      (audit.info _).expects("Executing CreateRole(RangerRole(-1,true,b,c,List(),List(),List()))").once(),
      (audit.info _).expects("CreateRole(RangerRole(-1,true,b,c,List(),List(),List())) completed successfully")
    )
    val actual = rangerClient.createRole(role)
    assert(actual == Right(role))
  }

  test("createRole logs error info") {
    val role = RangerRole.empty(name = "b", description = "c")

    (defaultRangerClient.createRole _).when(*).returns(Left(CreateRoleErr(role, ClientErr(404, "x"))))
    inSequence(
      (audit.info _).expects("Executing CreateRole(RangerRole(-1,true,b,c,List(),List(),List()))").once(),
      (audit.error _).expects(
        "CreateRole(RangerRole(-1,true,b,c,List(),List(),List())) failed. Details: CreateRoleErr(RangerRole(-1,true,b,c,List(),List(),List()),ClientErr(404,x))"
      )
    )
    val actual = rangerClient.createRole(role)
    assert(actual == Left(CreateRoleErr(role, ClientErr(404, "x"))))
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
    val role = RangerRole.empty(name = "b", description = "c").copy(id = 1)

    (defaultRangerClient.updateRole _).when(*).returns(Left(UpdateRoleErr(role, ClientErr(404, "x"))))
    inSequence(
      (audit.info _).expects("Executing UpdateRole(RangerRole(1,true,b,c,List(),List(),List()))").once(),
      (audit.error _).expects(
        "UpdateRole(RangerRole(1,true,b,c,List(),List(),List())) failed. Details: UpdateRoleErr(RangerRole(1,true,b,c,List(),List(),List()),ClientErr(404,x))"
      )
    )
    val actual = rangerClient.updateRole(role)
    assert(actual == Left(UpdateRoleErr(role, ClientErr(404, "x"))))
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
    val role = RangerRole.empty(name = "b", description = "c").copy(id = 1)

    (defaultRangerClient.deleteRole _).when(*).returns(Left(DeleteRoleErr(role, ClientErr(404, "x"))))
    inSequence(
      (audit.info _).expects("Executing DeleteRole(RangerRole(1,true,b,c,List(),List(),List()))").once(),
      (audit.error _).expects(
        "DeleteRole(RangerRole(1,true,b,c,List(),List(),List())) failed. Details: DeleteRoleErr(RangerRole(1,true,b,c,List(),List(),List()),ClientErr(404,x))"
      )
    )
    val actual = rangerClient.deleteRole(role)
    assert(actual == Left(DeleteRoleErr(role, ClientErr(404, "x"))))
  }
}
