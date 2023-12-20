package it.agilelab.provisioning.commons.client.ranger

import io.circe.{ Decoder, Encoder }
import it.agilelab.provisioning.commons.client.ranger.RangerClientError._
import it.agilelab.provisioning.commons.client.ranger.model._
import it.agilelab.provisioning.commons.http.Auth.BasicCredential
import it.agilelab.provisioning.commons.http.HttpErrors._
import it.agilelab.provisioning.commons.http.{ Auth, Http }
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

class DefaultRangerClientTest extends AnyFunSuite with BeforeAndAfterAll with MockFactory {

  val http: Http   = stub[Http]
  val rangerClient = new DefaultRangerClient("my-api-url", http, BasicCredential("x", "y"))

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

    (http
      .get[RangerPolicy](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerPolicy]))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, BasicCredential("x", "y"), *)
      .returns(Right(policy))

    val actual = rangerClient.findPolicyById(1)
    assert(actual == Right(Some(policy)))
  }

  test("findPolicyById return Right(None)") {
    (http
      .get[RangerPolicy](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerPolicy]))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(404, "")))

    val actual = rangerClient.findPolicyById(1)
    assert(actual == Right(None))
  }

  test("findPolicyById return Left(FindPolicyByIdErr(ClientError(401,)))") {
    (http
      .get[RangerPolicy](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerPolicy]))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.findPolicyById(1)
    val expected = Left(FindPolicyByIdErr(1, ClientErr(401, "")))
    assert(actual == expected)
  }

  test("findPolicyById return Left(FindPolicyByIdErr(ServerError(500,)))") {
    (http
      .get[RangerPolicy](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerPolicy]))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, BasicCredential("x", "y"), *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.findPolicyById(1)
    val expected = Left(FindPolicyByIdErr(1, ServerErr(500, "")))
    assert(actual == expected)
  }

  test("findPolicyById return Left(FindPolicyByIdErr(GenericError(301,)))") {
    (http
      .get[RangerPolicy](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerPolicy]))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, BasicCredential("x", "y"), *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.findPolicyById(1)
    val expected = Left(FindPolicyByIdErr(1, GenericErr(301, "")))
    assert(actual == expected)
  }

  test("findPolicyByName without zone return Right(Some(RangerPolicy))") {
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

    (http
      .get[RangerPolicy](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerPolicy]))
      .when(
        "https://my-api-url/service/public/v2/api/service/srv/policy/pn",
        *,
        BasicCredential("x", "y"),
        *
      )
      .returns(Right(policy))

    val actual = rangerClient.findPolicyByName("srv", "pn", None)
    assert(actual == Right(Some(policy)))
  }

  test("findPolicyByName without zone return Right(None)") {
    (http
      .get[RangerPolicy](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerPolicy]))
      .when(
        "https://my-api-url/service/public/v2/api/service/srv/policy/pn",
        *,
        BasicCredential("x", "y"),
        *
      )
      .returns(Left(ClientErr(404, "")))

    val actual = rangerClient.findPolicyByName("srv", "pn", None)
    assert(actual == Right(None))
  }

  test("findPolicyByName without zone return Left(FindPolicyByIdErr(ClientError(401,)))") {
    (http
      .get[RangerPolicy](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerPolicy]))
      .when(
        "https://my-api-url/service/public/v2/api/service/srv/policy/pn",
        *,
        BasicCredential("x", "y"),
        *
      )
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.findPolicyByName("srv", "pn", None)
    val expected = Left(FindPolicyByNameErr("pn", ClientErr(401, "")))
    assert(actual == expected)
  }

  test("findPolicyByName without return Left(FindPolicyByIdErr(ServerError(500,)))") {
    (http
      .get[RangerPolicy](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerPolicy]))
      .when(
        "https://my-api-url/service/public/v2/api/service/srv/policy/pn",
        *,
        BasicCredential("x", "y"),
        *
      )
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.findPolicyByName("srv", "pn", None)
    val expected = Left(FindPolicyByNameErr("pn", ServerErr(500, "")))
    assert(actual == expected)
  }

  test("findPolicyByName without return Left(FindPolicyByIdErr(GenericError(301,)))") {
    (http
      .get[RangerPolicy](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerPolicy]))
      .when(
        "https://my-api-url/service/public/v2/api/service/srv/policy/pn",
        *,
        BasicCredential("x", "y"),
        *
      )
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.findPolicyByName("srv", "pn", None)
    val expected = Left(FindPolicyByNameErr("pn", GenericErr(301, "")))
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

    (http
      .get[Seq[RangerPolicy]](_: String, _: Map[String, String], _: Auth)(_: Decoder[Seq[RangerPolicy]]))
      .when(
        "https://my-api-url/service/public/v2/api/service/srv/policy?policyName=pn&zoneName=zn",
        *,
        BasicCredential("x", "y"),
        *
      )
      .returns(Right(Seq(policy)))

    val actual = rangerClient.findPolicyByName("srv", "pn", Some("zn"))
    assert(actual == Right(Some(policy)))
  }

  test("findPolicyByName with zone return Right(None)") {
    (http
      .get[Seq[RangerPolicy]](_: String, _: Map[String, String], _: Auth)(_: Decoder[Seq[RangerPolicy]]))
      .when(
        "https://my-api-url/service/public/v2/api/service/srv/policy?policyName=pn&zoneName=zn",
        *,
        BasicCredential("x", "y"),
        *
      )
      .returns(Left(ClientErr(404, "")))

    val actual = rangerClient.findPolicyByName("srv", "pn", Some("zn"))
    assert(actual == Right(None))
  }

  test("findPolicyByName with zone return Left(FindPolicyByIdErr(ClientError(401,)))") {
    (http
      .get[Seq[RangerPolicy]](_: String, _: Map[String, String], _: Auth)(_: Decoder[Seq[RangerPolicy]]))
      .when(
        "https://my-api-url/service/public/v2/api/service/srv/policy?policyName=pn&zoneName=zn",
        *,
        BasicCredential("x", "y"),
        *
      )
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.findPolicyByName("srv", "pn", Some("zn"))
    val expected = Left(FindPolicyByNameErr("pn", ClientErr(401, "")))
    assert(actual == expected)
  }

  test("findPolicyByName with return Left(FindPolicyByIdErr(ServerError(500,)))") {
    (http
      .get[Seq[RangerPolicy]](_: String, _: Map[String, String], _: Auth)(_: Decoder[Seq[RangerPolicy]]))
      .when(
        "https://my-api-url/service/public/v2/api/service/srv/policy?policyName=pn&zoneName=zn",
        *,
        BasicCredential("x", "y"),
        *
      )
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.findPolicyByName("srv", "pn", Some("zn"))
    val expected = Left(FindPolicyByNameErr("pn", ServerErr(500, "")))
    assert(actual == expected)
  }

  test("findPolicyByName with return Left(FindPolicyByIdErr(GenericError(301,)))") {
    (http
      .get[Seq[RangerPolicy]](_: String, _: Map[String, String], _: Auth)(_: Decoder[Seq[RangerPolicy]]))
      .when(
        "https://my-api-url/service/public/v2/api/service/srv/policy?policyName=pn&zoneName=zn",
        *,
        BasicCredential("x", "y"),
        *
      )
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.findPolicyByName("srv", "pn", Some("zn"))
    val expected = Left(FindPolicyByNameErr("pn", GenericErr(301, "")))
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

    (http
      .post[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Right(Some(policy.copy(id = 2))))

    val actual = rangerClient.createPolicy(policy)
    assert(actual == Right(policy.copy(id = 2)))
  }

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

    (http
      .post[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Right(None))

    val actual = rangerClient.createPolicy(policy)
    assert(actual == Left(CreatePolicyEmptyResponseErr(policy)))
  }

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

    (http
      .post[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.createPolicy(policy)
    val expected = Left(CreatePolicyErr(policy, ClientErr(401, "")))
    assert(actual == expected)
  }

  test("createPolicy Left(CreatePolicyErr(ServerError(500,)))") {
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

    (http
      .post[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.createPolicy(policy)
    val expected = Left(CreatePolicyErr(policy, ServerErr(500, "")))
    assert(actual == expected)
  }

  test("createPolicy Left(CreatePolicyErr(GenericError(301,)))") {
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

    (http
      .post[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.createPolicy(policy)
    val expected = Left(CreatePolicyErr(policy, GenericErr(301, "")))
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

    (http
      .put[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Right(Some(policy.copy(name = "z"))))

    val actual = rangerClient.updatePolicy(policy)
    assert(actual == Right(policy.copy(name = "z")))
  }

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

    (http
      .put[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Right(None))

    val actual = rangerClient.updatePolicy(policy)
    assert(actual == Left(UpdatePolicyEmptyResponseErr(policy)))
  }

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

    (http
      .put[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.updatePolicy(policy)
    val expected = Left(UpdatePolicyErr(policy, ClientErr(401, "")))
    assert(actual == expected)
  }

  test("updatePolicy Left(UpdatePolicyErr(policy,ServerError(500,)))") {
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

    (http
      .put[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.updatePolicy(policy)
    val expected = Left(UpdatePolicyErr(policy, ServerErr(500, "")))
    assert(actual == expected)
  }

  test("updatePolicy Left(UpdatePolicyErr(GenericError(301,)))") {
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

    (http
      .put[RangerPolicy, RangerPolicy](_: String, _: Map[String, String], _: RangerPolicy, _: Auth)(
        _: Encoder[RangerPolicy],
        _: Decoder[RangerPolicy]
      ))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, policy, BasicCredential("x", "y"), *, *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.updatePolicy(policy)
    val expected = Left(UpdatePolicyErr(policy, GenericErr(301, "")))
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

    (http
      .get[RangerSecurityZone](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerSecurityZone]))
      .when("https://my-api-url/service/public/v2/api/zones/name/zzz", *, BasicCredential("x", "y"), *)
      .returns(Right(zone))

    val actual   = rangerClient.findSecurityZoneByName("zzz")
    val expected = Right(Some(zone))
    assert(actual == expected)
  }

  test("findSecurityZoneByName return Right(None)") {
    (http
      .get[RangerSecurityZone](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerSecurityZone]))
      .when("https://my-api-url/service/public/v2/api/zones/name/zzz", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(404, "")))
    val actual =
      rangerClient.findSecurityZoneByName("zzz")
    assert(actual == Right(None))
  }

  test("findSecurityZoneByName return Left(FindSecurityZoneByNameErr(ClientError(401,)))") {
    (http
      .get[RangerSecurityZone](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerSecurityZone]))
      .when("https://my-api-url/service/public/v2/api/zones/name/zzz", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.findSecurityZoneByName("zzz")
    val expected = Left(FindSecurityZoneByNameErr("zzz", ClientErr(401, "")))
    assert(actual == expected)
  }

  test("findSecurityZoneByName return Left(FindSecurityZoneByNameErr(ServerError(500,)))") {
    (http
      .get[RangerSecurityZone](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerSecurityZone]))
      .when("https://my-api-url/service/public/v2/api/zones/name/zzz", *, BasicCredential("x", "y"), *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.findSecurityZoneByName("zzz")
    val expected = Left(FindSecurityZoneByNameErr("zzz", ServerErr(500, "")))
    assert(actual == expected)
  }

  test("findSecurityZoneByName return Left(FindSecurityZoneByNameErr(GenericError(301,)))") {
    (http
      .get[RangerSecurityZone](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerSecurityZone]))
      .when("https://my-api-url/service/public/v2/api/zones/name/zzz", *, BasicCredential("x", "y"), *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.findSecurityZoneByName("zzz")
    val expected = Left(FindSecurityZoneByNameErr("zzz", GenericErr(301, "")))
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

    (
      http
        .put[RangerSecurityZone, RangerSecurityZone](_: String, _: Map[String, String], _: RangerSecurityZone, _: Auth)(
          _: Encoder[RangerSecurityZone],
          _: Decoder[RangerSecurityZone]
        )
      )
      .when("https://my-api-url/service/public/v2/api/zones/11", *, zone, BasicCredential("x", "y"), *, *)
      .returns(Right(Some(zone.copy(name = "z"))))

    val actual = rangerClient.updateSecurityZone(zone)
    assert(actual == Right(zone.copy(name = "z")))
  }

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
      http
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

  test("updateSecurityZone return Left(UpdateSecurityZoneErr(ClientError(401,)))") {
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
      http
        .put[RangerSecurityZone, RangerSecurityZone](_: String, _: Map[String, String], _: RangerSecurityZone, _: Auth)(
          _: Encoder[RangerSecurityZone],
          _: Decoder[RangerSecurityZone]
        )
      )
      .when("https://my-api-url/service/public/v2/api/zones/11", *, zone, BasicCredential("x", "y"), *, *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.updateSecurityZone(zone)
    val expected = Left(UpdateSecurityZoneErr(zone, ClientErr(401, "")))
    assert(actual == expected)
  }

  test("updateSecurityZone Left(UpdateSecurityZoneErr(ServerError(500,)))") {
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
      http
        .put[RangerSecurityZone, RangerSecurityZone](_: String, _: Map[String, String], _: RangerSecurityZone, _: Auth)(
          _: Encoder[RangerSecurityZone],
          _: Decoder[RangerSecurityZone]
        )
      )
      .when("https://my-api-url/service/public/v2/api/zones/11", *, zone, BasicCredential("x", "y"), *, *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.updateSecurityZone(zone)
    val expected = Left(UpdateSecurityZoneErr(zone, ServerErr(500, "")))
    assert(actual == expected)
  }

  test("updateSecurityZone Left(UpdateSecurityZoneErr(GenericError(301,)))") {
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
      http
        .put[RangerSecurityZone, RangerSecurityZone](_: String, _: Map[String, String], _: RangerSecurityZone, _: Auth)(
          _: Encoder[RangerSecurityZone],
          _: Decoder[RangerSecurityZone]
        )
      )
      .when("https://my-api-url/service/public/v2/api/zones/11", *, zone, BasicCredential("x", "y"), *, *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.updateSecurityZone(zone)
    val expected = Left(UpdateSecurityZoneErr(zone, GenericErr(301, "")))
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

    (http
      .get[Seq[RangerService]](_: String, _: Map[String, String], _: Auth)(_: Decoder[Seq[RangerService]]))
      .when("https://my-api-url/service/public/v2/api/service", *, BasicCredential("x", "y"), *)
      .returns(Right(services))

    val actual   = rangerClient.findAllServices
    val expected = Right(services)
    assert(actual == expected)
  }

  test("findAllServices return Right(Seq.empty[RangerService])") {
    val services = Seq.empty[RangerService]
    (http
      .get[Seq[RangerService]](_: String, _: Map[String, String], _: Auth)(_: Decoder[Seq[RangerService]]))
      .when("https://my-api-url/service/public/v2/api/service", *, BasicCredential("x", "y"), *)
      .returns(Right(services))

    val actual   = rangerClient.findAllServices
    val expected = Right(services)
    assert(actual == expected)
  }

  test("findAllServices return Left(FindAllServicesErr(ClientError(401,)))") {
    (http
      .get[Seq[RangerService]](_: String, _: Map[String, String], _: Auth)(_: Decoder[Seq[RangerService]]))
      .when("https://my-api-url/service/public/v2/api/service", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.findAllServices
    val expected = Left(FindAllServicesErr(ClientErr(401, "")))
    assert(actual == expected)
  }

  test("findAllServices return Left(FindAllServicesErr(ServerError(500,)))") {
    (http
      .get[Seq[RangerService]](_: String, _: Map[String, String], _: Auth)(_: Decoder[Seq[RangerService]]))
      .when("https://my-api-url/service/public/v2/api/service", *, BasicCredential("x", "y"), *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.findAllServices
    val expected = Left(FindAllServicesErr(ServerErr(500, "")))
    assert(actual == expected)
  }

  test("findAllServices return Left(FindAllServicesErr(GenericError(301,)))") {
    (http
      .get[Seq[RangerService]](_: String, _: Map[String, String], _: Auth)(_: Decoder[Seq[RangerService]]))
      .when("https://my-api-url/service/public/v2/api/service", *, BasicCredential("x", "y"), *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.findAllServices
    val expected = Left(FindAllServicesErr(GenericErr(301, "")))
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

    (
      http
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
      .returns(Right(Some(zone.copy(id = 2))))

    val actual = rangerClient.createSecurityZone(zone)
    assert(actual == Right(zone.copy(id = 2)))
  }

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
      http
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

    (
      http
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
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.createSecurityZone(zone)
    val expected = Left(CreateSecurityZoneErr(zone, ClientErr(401, "")))
    assert(actual == expected)
  }

  test("createSecurityZone Left(CreateSecurityZoneErr(ServerError(500,)))") {
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
      http
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
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.createSecurityZone(zone)
    val expected = Left(CreateSecurityZoneErr(zone, ServerErr(500, "")))
    assert(actual == expected)
  }

  test("createSecurityZone Left(CreateSecurityZoneErr(GenericError(301,)))") {
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
      http
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
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.createSecurityZone(zone)
    val expected = Left(CreateSecurityZoneErr(zone, GenericErr(301, "")))
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

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Unit]
      ))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, BasicCredential("x", "y"), *)
      .returns(Right(None))

    val actual = rangerClient.deletePolicy(policy)
    assert(actual == Right())
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

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Unit]
      ))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.deletePolicy(policy)
    val expected = Left(DeletePolicyErr(policy, ClientErr(401, "")))
    assert(actual == expected)
  }

  test("deletePolicy Left(DeletePolicyErr(ServerError(500,)))") {
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

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Unit]
      ))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, BasicCredential("x", "y"), *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.deletePolicy(policy)
    val expected = Left(DeletePolicyErr(policy, ServerErr(500, "")))
    assert(actual == expected)
  }

  test("deletePolicy Left(DeletePolicyErr(GenericError(301,)))") {
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

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Unit]
      ))
      .when("https://my-api-url/service/public/v2/api/policy/1", *, BasicCredential("x", "y"), *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.deletePolicy(policy)
    val expected = Left(DeletePolicyErr(policy, GenericErr(301, "")))
    assert(actual == expected)
  }

  test("findRoleById return Right(Some(RangerRole))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)

    (http
      .get[RangerRole](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerRole]))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, BasicCredential("x", "y"), *)
      .returns(Right(role))

    val actual = rangerClient.findRoleById(1)
    assert(actual == Right(Some(role)))
  }

  test("findRoleById return Right(None)") {
    (http
      .get[RangerRole](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerRole]))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(404, "")))

    val actual = rangerClient.findRoleById(1)
    assert(actual == Right(None))
  }

  test("findRoleById return Left(FindRoleBydIdErr(ClientError(401,)))") {
    (http
      .get[RangerRole](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerRole]))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.findRoleById(1)
    val expected = Left(FindRoleByIdErr(1, ClientErr(401, "")))
    assert(actual == expected)
  }

  test("findRoleById return Left(FindRoleBydIdErr(ServerErr(500,)))") {
    (http
      .get[RangerRole](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerRole]))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, BasicCredential("x", "y"), *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.findRoleById(1)
    val expected = Left(FindRoleByIdErr(1, ServerErr(500, "")))
    assert(actual == expected)
  }

  test("findRoleById return Left(FindRoleBydIdErr(GenericErr(301,)))") {
    (http
      .get[RangerRole](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerRole]))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, BasicCredential("x", "y"), *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.findRoleById(1)
    val expected = Left(FindRoleByIdErr(1, GenericErr(301, "")))
    assert(actual == expected)
  }

  test("findRoleByName return Right(Some(RangerRole))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)

    (http
      .get[RangerRole](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerRole]))
      .when("https://my-api-url/service/public/v2/api/roles/name/name", *, BasicCredential("x", "y"), *)
      .returns(Right(role))

    val actual = rangerClient.findRoleByName("name")
    assert(actual == Right(Some(role)))
  }

  test("findRoleByName return Right(None)") {
    (http
      .get[RangerRole](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerRole]))
      .when("https://my-api-url/service/public/v2/api/roles/name/name", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(404, "")))

    val actual = rangerClient.findRoleByName("name")
    assert(actual == Right(None))
  }

  test("findRoleByName return Left(FindRoleBydNameErr(ClientError(401,)))") {
    (http
      .get[RangerRole](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerRole]))
      .when("https://my-api-url/service/public/v2/api/roles/name/name", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.findRoleByName("name")
    val expected = Left(FindRoleByNameErr("name", ClientErr(401, "")))
    assert(actual == expected)
  }

  test("findRoleByName return Left(FindRoleBydNameErr(ServerErr(500,)))") {
    (http
      .get[RangerRole](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerRole]))
      .when("https://my-api-url/service/public/v2/api/roles/name/name", *, BasicCredential("x", "y"), *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.findRoleByName("name")
    val expected = Left(FindRoleByNameErr("name", ServerErr(500, "")))
    assert(actual == expected)
  }

  test("findRoleByName return Left(FindRoleBydNameErr(GenericErr(301,)))") {
    (http
      .get[RangerRole](_: String, _: Map[String, String], _: Auth)(_: Decoder[RangerRole]))
      .when("https://my-api-url/service/public/v2/api/roles/name/name", *, BasicCredential("x", "y"), *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.findRoleByName("name")
    val expected = Left(FindRoleByNameErr("name", GenericErr(301, "")))
    assert(actual == expected)
  }

  test("createRole return Right(RangerRole)") {
    val role = RangerRole.empty("name", "descr")
    (http
      .post[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles", *, role, BasicCredential("x", "y"), *, *)
      .returns(Right(Some(role.copy(id = 2))))

    val actual = rangerClient.createRole(role)
    assert(actual == Right(role.copy(id = 2)))
  }

  test("createRole return Left(CreateRangerRoleErr(Empty body))") {
    val role = RangerRole.empty("name", "descr")
    (http
      .post[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles", *, role, BasicCredential("x", "y"), *, *)
      .returns(Right(None))

    val actual = rangerClient.createRole(role)
    assert(actual == Left(CreateRoleEmptyResponseErr(role)))
  }

  test("createRole return Left(CreateRangerRoleErr(ClientErr(401,)))") {
    val role = RangerRole.empty("name", "descr")
    (http
      .post[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles", *, role, BasicCredential("x", "y"), *, *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.createRole(role)
    val expected = Left(CreateRoleErr(role, ClientErr(401, "")))
    assert(actual == expected)
  }

  test("createRole return Left(CreateRangerRoleErr(ServerErr(500,)))") {
    val role = RangerRole.empty("name", "descr")
    (http
      .post[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles", *, role, BasicCredential("x", "y"), *, *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.createRole(role)
    val expected = Left(CreateRoleErr(role, ServerErr(500, "")))
    assert(actual == expected)
  }

  test("createRole return Left(CreateRangerRoleErr(GenericErr(301,)))") {
    val role = RangerRole.empty("name", "descr")
    (http
      .post[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles", *, role, BasicCredential("x", "y"), *, *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.createRole(role)
    val expected = Left(CreateRoleErr(role, GenericErr(301, "")))
    assert(actual == expected)
  }

  test("updateRole return Right(RangerRole)") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)
    (http
      .put[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, role, BasicCredential("x", "y"), *, *)
      .returns(Right(Some(role.copy(name = "new-name"))))

    val actual = rangerClient.updateRole(role)
    assert(actual == Right(role.copy(name = "new-name")))
  }

  test("updateRole return Left(CreateRangerRoleErr(Empty body))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)
    (http
      .put[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, role, BasicCredential("x", "y"), *, *)
      .returns(Right(None))

    val actual = rangerClient.updateRole(role)
    assert(actual == Left(UpdateRoleEmptyResponseErr(role)))
  }

  test("updateRole return Left(UpdateRangerRoleErr(ClientErr(401,)))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)
    (http
      .put[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, role, BasicCredential("x", "y"), *, *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.updateRole(role)
    val expected = Left(UpdateRoleErr(role, ClientErr(401, "")))
    assert(actual == expected)
  }

  test("updateRole return Left(UpdateRangerRoleErr(ServerErr(500,)))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)
    (http
      .put[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, role, BasicCredential("x", "y"), *, *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.updateRole(role)
    val expected = Left(UpdateRoleErr(role, ServerErr(500, "")))
    assert(actual == expected)
  }

  test("updateRole return Left(UpdateRangerRoleErr(GenericErr(301,)))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)
    (http
      .put[RangerRole, RangerRole](_: String, _: Map[String, String], _: RangerRole, _: Auth)(
        _: Encoder[RangerRole],
        _: Decoder[RangerRole]
      ))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, role, BasicCredential("x", "y"), *, *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.updateRole(role)
    val expected = Left(UpdateRoleErr(role, GenericErr(301, "")))
    assert(actual == expected)
  }

  test("deleteRole return Right(())") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Unit]
      ))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, BasicCredential("x", "y"), *)
      .returns(Right(None))

    val actual = rangerClient.deleteRole(role)
    assert(actual == Right(()))
  }

  test("deleteRole return Left(DeleteRoleErr(ClientError(401,)))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Unit]
      ))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, BasicCredential("x", "y"), *)
      .returns(Left(ClientErr(401, "")))

    val actual   = rangerClient.deleteRole(role)
    val expected = Left(DeleteRoleErr(role, ClientErr(401, "")))
    assert(actual == expected)
  }

  test("deleteRole return Left(DeleteRoleErr(ServerErr(500,)))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Unit]
      ))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, BasicCredential("x", "y"), *)
      .returns(Left(ServerErr(500, "")))

    val actual   = rangerClient.deleteRole(role)
    val expected = Left(DeleteRoleErr(role, ServerErr(500, "")))
    assert(actual == expected)
  }

  test("deleteRole return Left(DeleteRoleErr(GenericErr(301,)))") {
    val role = RangerRole.empty("name", "descr").copy(id = 1)

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Unit]
      ))
      .when("https://my-api-url/service/public/v2/api/roles/1", *, BasicCredential("x", "y"), *)
      .returns(Left(GenericErr(301, "")))

    val actual   = rangerClient.deleteRole(role)
    val expected = Left(DeleteRoleErr(role, GenericErr(301, "")))
    assert(actual == expected)
  }

}
