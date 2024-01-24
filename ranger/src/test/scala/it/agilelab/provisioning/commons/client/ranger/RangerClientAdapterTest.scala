package it.agilelab.provisioning.commons.client.ranger

import cats.implicits.showInterpolator
import com.sun.jersey.api.client.ClientResponse
import it.agilelab.provisioning.commons.client.ranger.RangerClientError._
import it.agilelab.provisioning.commons.client.ranger.model._
import org.apache.ranger
import org.apache.ranger.RangerServiceException
import org.apache.ranger.plugin.model
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

import java.util
import scala.jdk.CollectionConverters.{ MapHasAsJava, SeqHasAsJava }

// boilerplate as mock fails if hostName is not provided
class MockRangerClient extends ranger.RangerClient("url", null, null, null, null)

class RangerClientAdapterTest extends AnyFunSuite with BeforeAndAfterAll with MockFactory {

  val client: MockRangerClient = mock[MockRangerClient]
  val rangerClient             = new RangerClientAdapter(client)

  test("findPolicyById return Right(Some(RangerPolicy))") {
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

    (client.getPolicy(_: Long)).expects(policy.id).once().returns(policy)

    val actual = rangerClient.findPolicyById(1)
    assert(actual == Right(Some(policy)))
  }

  test("findPolicyById return Right(None)") {
    (client
      .getPolicy(_: Long))
      .expects(*)
      .once()
      .throws(
        new RangerServiceException(
          ranger.RangerClient.GET_POLICY_BY_ID,
          ClientResponseMock(ClientResponse.Status.NOT_FOUND, "error")
        )
      )

    val actual = rangerClient.findPolicyById(1)
    assert(actual == Right(None))
  }

  test("findPolicyById return Left(FindPolicyByIdErr") {
    val exception = new RangerServiceException(
      ranger.RangerClient.GET_POLICY_BY_ID,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )
    (client.getPolicy(_: Long)).expects(*).once().throws(exception)

    val actual   = rangerClient.findPolicyById(1)
    val expected = Left(FindPolicyByIdErr(1, exception))
    assert(actual == expected)
  }

  test("findPolicyByName without zone return Right(Some(RangerPolicy))") {
    val policy = RangerPolicy(
      id = 1,
      service = "srv",
      name = "pn",
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

    (client
      .findPolicies(_: util.Map[String, String]))
      .expects(Map("serviceName" -> "srv", "policyName" -> "pn").asJava)
      .returns(util.Arrays.asList(policy))

    val actual = rangerClient.findPolicyByName("srv", "pn", None)
    assert(actual == Right(Some(policy)))
  }

  test("findPolicyByName without zone return Right(None)") {
    (client
      .findPolicies(_: util.Map[String, String]))
      .expects(Map("serviceName" -> "srv", "policyName" -> "pn").asJava)
      .returns(new util.ArrayList[model.RangerPolicy]())

    val actual = rangerClient.findPolicyByName("srv", "pn", None)
    assert(actual == Right(None))
  }

  test("findPolicyByName without zone return Left(FindPolicyByIdErr(ClientError(401,)))") {
    val exception = new RangerServiceException(
      ranger.RangerClient.GET_POLICY_BY_NAME,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client
      .findPolicies(_: util.Map[String, String]))
      .expects(Map("serviceName" -> "srv", "policyName" -> "pn").asJava)
      .throws(exception)

    val actual   = rangerClient.findPolicyByName("srv", "pn", None)
    val expected =
      Left(FindPoliciesErr(Map("serviceName" -> "srv", "policyName" -> "pn"), exception))
    assert(actual == expected)
  }

  test("findPolicyByName with zone return Right(Some(RangerPolicy))") {
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

    (client
      .findPolicies(_: util.Map[String, String]))
      .expects(Map("serviceName" -> "srv", "policyName" -> "pn", "zoneName" -> "zn").asJava)
      .returns(util.Arrays.asList(policy))

    val actual = rangerClient.findPolicyByName("srv", "pn", Some("zn"))
    assert(actual == Right(Some(policy)))
  }

  test("findPolicyByName with zone return Right(None)") {
    (client
      .findPolicies(_: util.Map[String, String]))
      .expects(Map("serviceName" -> "srv", "policyName" -> "pn", "zoneName" -> "zn").asJava)
      .returns(new util.ArrayList[model.RangerPolicy]())

    val actual = rangerClient.findPolicyByName("srv", "pn", Some("zn"))
    assert(actual == Right(None))
  }

  test("findPolicyByName with zone return Left(FindPolicyByIdErr(ClientError(401,)))") {
    val exception = new RangerServiceException(
      ranger.RangerClient.GET_POLICY_BY_NAME,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client
      .findPolicies(_: util.Map[String, String]))
      .expects(Map("serviceName" -> "srv", "policyName" -> "pn", "zoneName" -> "zn").asJava)
      .throws(exception)

    val actual   = rangerClient.findPolicyByName("srv", "pn", Some("zn"))
    val expected =
      Left(FindPoliciesErr(Map("serviceName" -> "srv", "policyName" -> "pn", "zoneName" -> "zn"), exception))
    assert(actual == expected)
  }

  test("createPolicy return Right(RangerPolicy)") {
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

    (client.createPolicy(_: model.RangerPolicy)).expects(*).once().returns(policy.copy(id = 2))

    val actual = rangerClient.createPolicy(policy)
    assert(actual == Right(policy.copy(id = 2)))
  }

  /*
  test("createPolicy return Left(CreateRangerPolicyErr(Empty body))") {
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

    (client
      .post[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Right(None))

    val actual = rangerClient.createPolicy(policy)
    assert(actual == Left(CreatePolicyEmptyResponseErr(policy)))
  }
   */

  test("createPolicy return Left(CreatePolicyErr(ClientError(401,)))") {
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
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client.createPolicy(_: model.RangerPolicy)).expects(*).throws(exception)

    val actual   = rangerClient.createPolicy(policy)
    val expected = Left(CreatePolicyErr(policy, exception))
    assert(actual == expected)
  }

  test("updatePolicy return Right(RangerPolicy)") {
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

    (client
      .updatePolicy(_: Long, _: model.RangerPolicy))
      .expects(policy.id, *)
      .returns(policy.copy(name = "z"))

    val actual = rangerClient.updatePolicy(policy)
    assert(actual == Right(policy.copy(name = "z")))
  }

  /*
  test("updatePolicy return Left(UpdatePolicyErr(Empty body))") {
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

    (client
      .put[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Right(None))

    val actual = rangerClient.updatePolicy(policy)
    assert(actual == Left(UpdatePolicyEmptyResponseErr(policy)))
  }
   */
  test("updatePolicy return Left(UpdatePolicyErr(policy,ClientError(401,)))") {
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
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client.updatePolicy(_: Long, _: model.RangerPolicy)).expects(policy.id, *).throws(exception)

    val actual   = rangerClient.updatePolicy(policy)
    val expected = Left(UpdatePolicyErr(policy, exception))
    assert(actual == expected)
  }

  test("findSecurityZoneByName return Right(Some(RangerSecurityZone))") {
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

    (client.getSecurityZone(_: String)).expects("zzz").returns(zone)

    val actual   = rangerClient.findSecurityZoneByName("zzz")
    val expected = Right(Some(zone))
    assert(actual == expected)
  }

  test("findSecurityZoneByName return Right(None)") {
    (client
      .getSecurityZone(_: String))
      .expects("zzz")
      .throws(
        new RangerServiceException(
          ranger.RangerClient.GET_ZONE_BY_NAME,
          ClientResponseMock(ClientResponse.Status.NOT_FOUND, "not found")
        )
      )
    val actual = rangerClient.findSecurityZoneByName("zzz")
    assert(actual == Right(None))
  }

  test("findSecurityZoneByName return Left(FindSecurityZoneByNameErr(ClientError(401,)))") {
    val exception = new RangerServiceException(
      ranger.RangerClient.GET_ZONE_BY_NAME,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client.getSecurityZone(_: String)).expects("zzz").throws(exception)

    val actual   = rangerClient.findSecurityZoneByName("zzz")
    val expected = Left(FindSecurityZoneByNameErr("zzz", exception))
    assert(actual == expected)
  }

  test("updateSecurityZone return Right(RangerSecurityZone)") {
    val zone = RangerSecurityZone(
      11,
      "b",
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

    (client
      .updateSecurityZone(_: Long, _: model.RangerSecurityZone))
      .expects(11, *)
      .returns(zone.copy(name = "z"))

    val actual = rangerClient.updateSecurityZone(zone)
    assert(actual == Right(zone.copy(name = "z")))
  }

  /*
  test("updateSecurityZone return Left(UpdateSecurityZoneErr(Empty body))") {
    val zone = RangerSecurityZone(
      11,
      "b",
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

    (
      client
        .put[RangerSecurityZone, RangerSecurityZone](_: String, _: Map[String, String], _: RangerSecurityZone, _: Auth)(
          _: Encoder[RangerSecurityZone],
          _: Decoder[RangerSecurityZone]
        )
      )
      .when("https://my-api-url/service/public/v2/api/zones/11", *, zone, BasicCredential("x", "y"), *, *)
      .returns(Right(None))

    val actual = rangerClient.updateSecurityZone(zone)
    assert(actual == Left(UpdateSecurityZoneEmptyResponseErr(zone)))
  }

   */
  test("updateSecurityZone return Left(UpdateSecurityZoneErr(ClientError(401,)))") {
    val zone      = RangerSecurityZone(
      11,
      "b",
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
    val exception = new RangerServiceException(
      ranger.RangerClient.GET_POLICY_BY_NAME,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client.updateSecurityZone(_: Long, _: model.RangerSecurityZone)).expects(11, *).throws(exception)

    val actual   = rangerClient.updateSecurityZone(zone)
    val expected = Left(UpdateSecurityZoneErr(zone, exception))
    assert(actual == expected)
  }

  test("findAllServices return Right(Seq(RangerService))") {
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

    (client
      .findServices(_: util.Map[String, String]))
      .expects(Map.empty[String, String].asJava)
      .returns(services)

    val actual   = rangerClient.findAllServices
    val expected = Right(services)
    assert(actual == expected)
  }

  test("findAllServices return Right(Seq.empty[RangerService])") {
    val services = Seq.empty[RangerService]
    (client
      .findServices(_: util.Map[String, String]))
      .expects(Map.empty[String, String].asJava)
      .returns(services)

    val actual   = rangerClient.findAllServices
    val expected = Right(services)
    assert(actual == expected)
  }

  test("findAllServices return Left(FindAllServicesErr(ClientError(401,)))") {
    val exception = new RangerServiceException(
      ranger.RangerClient.FIND_SERVICES,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client.findServices(_: util.Map[String, String])).expects(Map.empty[String, String].asJava).throws(exception)

    val actual   = rangerClient.findAllServices
    val expected = Left(FindAllServicesErr(exception))
    assert(actual == expected)
  }

  test("createSecurityZone return Right(RangerSecurityZone)") {
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

    (client.createSecurityZone(_: model.RangerSecurityZone)).expects(*).returns(zone.copy(id = 2))

    val actual = rangerClient.createSecurityZone(zone)
    assert(actual == Right(zone.copy(id = 2)))
  }

  /*
  test("createSecurityZone return Left(CreateSecurityZoneErr(Empty body))") {
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

    (
      client
        .post[RangerSecurityZone, RangerSecurityZone](
          _: String,
          _: Map[String, String],
          _: RangerSecurityZone,
          _: Auth
        )(
          _: Encoder[RangerSecurityZone],
          _: Decoder[RangerSecurityZone]
        )
      )
      .when("https://my-api-url/service/public/v2/api/zones", *, zone, BasicCredential("x", "y"), *, *)
      .returns(Right(None))

    val actual = rangerClient.createSecurityZone(zone)
    assert(actual == Left(CreateSecurityZoneEmptyResponseErr(zone)))
  }
   */
  test("createSecurityZone return Left(CreateSecurityZoneErr(ClientError(401,)))") {
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

    val exception = new RangerServiceException(
      ranger.RangerClient.CREATE_ZONE,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client.createSecurityZone(_: model.RangerSecurityZone)).expects(*).throws(exception)

    val actual   = rangerClient.createSecurityZone(zone)
    val expected = Left(CreateSecurityZoneErr(zone, exception))
    assert(actual == expected)
  }

  test("deletePolicy return Right()") {
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

    (client.deletePolicy(_: Long)).expects(policy.id).returns(())

    val actual = rangerClient.deletePolicy(policy)
    assert(actual == Right(()))
  }

  test("deletePolicy return Left(DeletePolicyErr(ClientError(401,)))") {
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
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client.deletePolicy(_: Long)).expects(policy.id).throws(exception)

    val actual   = rangerClient.deletePolicy(policy)
    val expected = Left(DeletePolicyErr(policy, exception))
    assert(actual == expected)
  }

  test("findRoleById return Right(Some(RangerRole))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)

    (client.getRole(_: Long)).expects(role.id).returns(role)

    val actual = rangerClient.findRoleById(1)
    assert(actual == Right(Some(role)))
  }

  test("findRoleById return Right(None)") {
    (client
      .getRole(_: Long))
      .expects(1)
      .throws(
        new RangerServiceException(
          ranger.RangerClient.GET_ROLE_BY_ID,
          ClientResponseMock(ClientResponse.Status.NOT_FOUND, "not found")
        )
      )

    val actual = rangerClient.findRoleById(1)
    assert(actual == Right(None))
  }

  test("findRoleById return Left(FindRoleBydIdErr(ClientError(401,)))") {
    val exception = new RangerServiceException(
      ranger.RangerClient.GET_ROLE_BY_ID,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client.getRole(_: Long)).expects(1).throws(exception)

    val actual   = rangerClient.findRoleById(1)
    val expected = Left(FindRoleByIdErr(1, exception))
    assert(actual == expected)
  }

  test("findRoleByName return Right(RangerRole)") {
    val role = new model.RangerRole(
      "name",
      "description",
      Map.empty[String, AnyRef].asJava,
      List(RoleMember("u1", isAdmin = false), RoleMember("u2", isAdmin = true)),
      List(RoleMember("g1", isAdmin = false), RoleMember("g2", isAdmin = true)),
      List(RoleMember("r1", isAdmin = false), RoleMember("r2", isAdmin = true))
    )
    role.setId(10)
    role.setIsEnabled(true)

    (client
      .findRoles(_: util.Map[String, String]))
      .expects(Map("roleName" -> "name").asJava)
      .returns(util.Arrays.asList(role))
    val actual = rangerClient.findRoleByName("name")
    assert(actual == Right(Some(RangerRole.roleFromRangerModel(role))))
  }

  test("findRoleByName return Right(None)") {
    (client
      .findRoles(_: util.Map[String, String]))
      .expects(Map("roleName" -> "name").asJava)
      .returns(new util.ArrayList[model.RangerRole]())
    val actual = rangerClient.findRoleByName("name")
    assert(actual == Right(None))
  }

  test("findRoleByName return Left(FindRoleBydNameErr(ClientError(401,)))") {
    val exception = new RangerServiceException(
      ranger.RangerClient.GET_ROLE_BY_NAME,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client
      .findRoles(_: util.Map[String, String]))
      .expects(Map("roleName" -> "name").asJava)
      .throws(exception)

    val actual   = rangerClient.findRoleByName("name")
    val expected = Left(FindRoleByNameErr("name", exception))
    assert(actual == expected)
  }

  test("createRole return Right(RangerRole)") {
    val role = RangerRole.empty("name", "descr")

    (client.createRole(_: String, _: model.RangerRole)).expects("", *).returns(role.copy(id = 2))

    val actual = rangerClient.createRole(role)
    assert(actual == Right(role.copy(id = 2)))
  }

  /*
  test("createRole return Left(CreateRangerRoleErr(Empty body))") {
    val role = RangerRole.empty("name", "descr")
    (client
      .post[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles", *, role, BasicCredential("x", "y"), *, *)
      .returns(Right(None))

    val actual = rangerClient.createRole(role)
    assert(actual == Left(CreateRoleEmptyResponseErr(role)))
  }
   */

  test("createRole return Left(CreateRangerRoleErr(ClientErr(401,)))") {
    val role = RangerRole.empty("name", "descr")

    val exception = new RangerServiceException(
      ranger.RangerClient.CREATE_ROLE,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client.createRole(_: String, _: model.RangerRole)).expects("", *).throws(exception)

    val actual   = rangerClient.createRole(role)
    val expected = Left(CreateRoleErr(role, exception))
    assert(actual == expected)
  }

  test("updateRole return Right(RangerRole)") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)

    (client
      .updateRole(_: Long, _: model.RangerRole))
      .expects(role.id, *)
      .returns(role.copy(name = "new-name"))

    val actual = rangerClient.updateRole(role)
    assert(actual == Right(role.copy(name = "new-name")))
  }

  /*
  test("updateRole return Left(CreateRangerRoleErr(Empty body))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)
    (client
      .put[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, role, BasicCredential("x", "y"), *, *)
      .returns(Right(None))

    val actual = rangerClient.updateRole(role)
    assert(actual == Left(UpdateRoleEmptyResponseErr(role)))
  }
   */

  test("updateRole return Left(UpdateRangerRoleErr(ClientErr(401,)))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)

    val exception = new RangerServiceException(
      ranger.RangerClient.UPDATE_ROLE_BY_ID,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client.updateRole(_: Long, _: model.RangerRole)).expects(role.id, *).throws(exception)

    val actual   = rangerClient.updateRole(role)
    val expected = Left(UpdateRoleErr(role, exception))
    assert(actual == expected)
  }

  test("deleteRole return Right(())") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)

    (client.deleteRole(_: Long)).expects(role.id).returns(())

    val actual = rangerClient.deleteRole(role)
    assert(actual == Right(()))
  }

  test("deleteRole return Left(DeleteRoleErr(ClientError(401,)))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)

    val exception = new RangerServiceException(
      ranger.RangerClient.DELETE_ROLE_BY_ID,
      ClientResponseMock(ClientResponse.Status.UNAUTHORIZED, "error")
    )

    (client.deleteRole(_: Long)).expects(role.id).throws(exception)

    val actual   = rangerClient.deleteRole(role)
    val expected = Left(DeleteRoleErr(role, exception))
    assert(actual == expected)
  }

}
