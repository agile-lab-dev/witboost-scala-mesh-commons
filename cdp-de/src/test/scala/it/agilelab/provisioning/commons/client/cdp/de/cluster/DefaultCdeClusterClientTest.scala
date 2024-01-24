package it.agilelab.provisioning.commons.client.cdp.de.cluster

import com.cloudera.cdp.de.model.{ ServiceDescription, VcDescription }
import io.circe.{ Decoder, Encoder }
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import cats.implicits._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.CdeClusterClientError._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider.{ TokenProvider, TokenProviderError }
import it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider.TokenProviderError._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base.{ Job, JobDetails, JobRun, ResourceDetails }
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.request._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.response._
import it.agilelab.provisioning.commons.http.Auth.{ BasicCredential, BearerToken }
import it.agilelab.provisioning.commons.http.{ Auth, Http }
import it.agilelab.provisioning.commons.http.HttpErrors._
import org.scalatest.EitherValues._

class DefaultCdeClusterClientTest extends AnyFunSuite with BeforeAndAfterAll with MockFactory {

  val serviceDesc = new ServiceDescription()
  val vcDesc      = new VcDescription()
  serviceDesc.setClusterFqdn("mcf")
  vcDesc.setVcApiUrl("https://api-uri")

  val http: Http                   = stub[Http]
  val tokenProvider: TokenProvider = stub[TokenProvider]
  val cdeClient                    = new DefaultCdeClusterClient(http, tokenProvider, BasicCredential("usr", "pwd"))

  // test that all the method will return unauthorized if token exchange fail
  Seq(
    (Left(ExchangeErr(ClientErr(404, "x"))), Left(AuthErr(ExchangeErr(ClientErr(404, "x"))))),
    (Left(UnauthorizedErr("x")), Left(AuthErr(UnauthorizedErr("x"))))
  ) foreach { case (err: Either[TokenProviderError, Any], expected: Either[CdeClusterClientError, Any]) =>
    test(show"getResource return Left(${expected.left.value})") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, GetResourceReq("x"))
      assert(cdeClient.getResource(req) == expected)
    }

    test(show"createResource return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
      assert(cdeClient.createResource(req) == expected)
    }

    test(show"safeCreateResource return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
      assert(cdeClient.safeCreateResource(req) == expected)
    }

    test(show"uploadFile return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, UploadFileReq("x", "y", "z", "x".getBytes))
      assert(cdeClient.uploadFile(req) == expected)
    }

    test(show"listJobs return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, ListJobsReq(None, None, None))
      assert(cdeClient.listJobs(req) == expected)
    }

    test(show"getJob return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, GetJobReq("x"))
      assert(cdeClient.getJob(req) == expected)
    }

    test(show"createJob return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, CreateJobReq(Job("x", "y", Seq.empty, "", None, None, None)))
      assert(cdeClient.createJob(req) == expected)
    }

    test(show"updateJob return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None)))
      assert(cdeClient.updateJob(req) == expected)
    }

    test(show"upsertJob return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(
        serviceDesc,
        vcDesc,
        UpsertJobReq(Job("x", "y", Seq.empty, "", None, None, None))
      )
      assert(cdeClient.upsertJob(req) == expected)
    }

    test(show"listJobRuns return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, ListJobRunsReq(None, None, None))
      assert(cdeClient.listJobRuns(req) == expected)
    }

    test(show"deleteJob return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, DeleteJobReq("jobName"))
      assert(cdeClient.deleteJob(req) == expected)
    }

    test(show"safeDeleteJob return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, DeleteJobReq("jobName"))
      assert(cdeClient.safeDeleteJob(req) == expected)
    }

    test(show"deleteResource return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, DeleteResourceReq("resourceName"))
      assert(cdeClient.deleteResource(req) == expected)
    }

    test(show"safeDeleteResource return ${expected.left.value}") {
      (tokenProvider.get _).when(*, *).returns(err)
      val req = CdeRequest(serviceDesc, vcDesc, DeleteResourceReq("resourceName"))
      assert(cdeClient.safeDeleteResource(req) == expected)
    }
  }

  test("getResource return Right(GetResourceRes(Some))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(ResourceDetails("x", "y", None, "", "", "", "", "")))

    val expected = Right(GetResourceRes(Some(ResourceDetails("x", "y", None, "", "", "", "", ""))))
    val actual   = cdeClient.getResource(CdeRequest(serviceDesc, vcDesc, GetResourceReq("x")))
    assert(actual == expected)
  }

  test("getResource return Right(GetResourceRes(None))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(404, "not found")))

    val expected = Right(GetResourceRes(None))
    val actual   = cdeClient.getResource(CdeRequest(serviceDesc, vcDesc, GetResourceReq("x")))
    assert(actual == expected)
  }

  test("getResource return Left(GetResourceErr(ClientError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(301, "error")))

    val expected = Left(GetResourceErr(GetResourceReq("x"), ClientErr(301, "error")))
    val actual   = cdeClient.getResource(CdeRequest(serviceDesc, vcDesc, GetResourceReq("x")))
    assert(actual == expected)
  }

  test("getResource return Left(GetResourceErr(ServerError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ServerErr(505, "error")))

    val expected = Left(GetResourceErr(GetResourceReq("x"), ServerErr(505, "error")))
    val actual   = cdeClient.getResource(CdeRequest(serviceDesc, vcDesc, GetResourceReq("x")))
    assert(actual == expected)
  }

  test("getResource return Left(GenericErr(ServerError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(GenericErr(101, "error")))

    val expected = Left(GetResourceErr(GetResourceReq("x"), GenericErr(101, "error")))
    val actual   = cdeClient.getResource(CdeRequest(serviceDesc, vcDesc, GetResourceReq("x")))
    assert(actual == expected)
  }

  test(s"createResource return Right() with Right(None)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .post[CreateResourceReq, Unit](_: String, _: Map[String, String], _: CreateResourceReq, _: Auth)(
        _: Encoder[CreateResourceReq],
        _: Decoder[Unit]
      ))
      .when("https://api-uri/resources", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Right(None))

    val req    = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
    val actual = cdeClient.createResource(req)
    assert(actual == Right())
  }

  test(s"createResource return Right() with Right(Some())") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .post[CreateResourceReq, Unit](_: String, _: Map[String, String], _: CreateResourceReq, _: Auth)(
        _: Encoder[CreateResourceReq],
        _: Decoder[Unit]
      ))
      .when("https://api-uri/resources", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Right(Some()))

    val req    = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
    val actual = cdeClient.createResource(req)
    assert(actual == Right())
  }

  test(s"createResource return Left(CreateResourceErr(ClientError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .post[CreateResourceReq, Unit](_: String, _: Map[String, String], _: CreateResourceReq, _: Auth)(
        _: Encoder[CreateResourceReq],
        _: Decoder[Unit]
      ))
      .when("https://api-uri/resources", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(ClientErr(404, "error")))

    val req    = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
    val actual = cdeClient.createResource(req)
    assert(actual == Left(CreateResourceErr(CreateResourceReq("x", "y", "z", None), ClientErr(404, "error"))))
  }

  test(s"createResource return Left(CreateResourceErr(ServerError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .post[CreateResourceReq, Unit](_: String, _: Map[String, String], _: CreateResourceReq, _: Auth)(
        _: Encoder[CreateResourceReq],
        _: Decoder[Unit]
      ))
      .when("https://api-uri/resources", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(ServerErr(505, "error")))

    val req    = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
    val actual = cdeClient.createResource(req)
    assert(actual == Left(CreateResourceErr(CreateResourceReq("x", "y", "z", None), ServerErr(505, "error"))))
  }

  test(s"createResource return Left(CreateResourceErr(GenericError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .post[CreateResourceReq, Unit](_: String, _: Map[String, String], _: CreateResourceReq, _: Auth)(
        _: Encoder[CreateResourceReq],
        _: Decoder[Unit]
      ))
      .when("https://api-uri/resources", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(GenericErr(101, "error")))

    val req    = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
    val actual = cdeClient.createResource(req)
    assert(actual == Left(CreateResourceErr(CreateResourceReq("x", "y", "z", None), GenericErr(101, "error"))))
  }

  test(s"safeCreateResource return Right() with Right(None)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(404, "")))

    (http
      .post[CreateResourceReq, Unit](_: String, _: Map[String, String], _: CreateResourceReq, _: Auth)(
        _: Encoder[CreateResourceReq],
        _: Decoder[Unit]
      ))
      .when("https://api-uri/resources", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Right(None))

    val req    = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
    val actual = cdeClient.safeCreateResource(req)
    assert(actual == Right())
  }

  test(s"safeCreateResource return Right() with Right(Some())") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(404, "")))

    (http
      .post[CreateResourceReq, Unit](_: String, _: Map[String, String], _: CreateResourceReq, _: Auth)(
        _: Encoder[CreateResourceReq],
        _: Decoder[Unit]
      ))
      .when("https://api-uri/resources", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Right(Some()))

    val req    = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
    val actual = cdeClient.safeCreateResource(req)
    assert(actual == Right())
  }

  test(s"safeCreateResource return Right() with existing res") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(ResourceDetails("existing-res", "y", None, "", "", "", "", "")))

    val req    = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
    val actual = cdeClient.safeCreateResource(req)
    assert(actual == Right())
  }

  test(s"safeCreateResource return Left(GetResourceError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ServerErr(500, "")))

    val req    = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
    val actual = cdeClient.safeCreateResource(req)
    assert(actual == Left(GetResourceErr(GetResourceReq("x"), ServerErr(500, ""))))
  }

  test(s"safeCreateResource return Left(CreateResourceErr)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(404, "")))

    (http
      .post[CreateResourceReq, Unit](_: String, _: Map[String, String], _: CreateResourceReq, _: Auth)(
        _: Encoder[CreateResourceReq],
        _: Decoder[Unit]
      ))
      .when("https://api-uri/resources", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(ServerErr(500, "")))

    val req    = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
    val actual = cdeClient.safeCreateResource(req)
    assert(actual == Left(CreateResourceErr(CreateResourceReq("x", "y", "z", None), ServerErr(500, ""))))
  }

  test("uploadFile return Right()") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http.putFileMultiPart _)
      .when(
        "https://api-uri/resources/x/my/file.txt",
        *,
        "file",
        "my/file.txt",
        "cnt",
        *,
        BearerToken("tk", "", "", "", "", 1L)
      )
      .returns(Right())

    val req    = UploadFileReq("x", "my/file.txt", "cnt", "x".getBytes)
    val actual = cdeClient.uploadFile(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("uploadFile return Left(ClientErr(403,)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http.putFileMultiPart _)
      .when(
        "https://api-uri/resources/x/my/file.txt",
        *,
        "file",
        "my/file.txt",
        "cnt",
        *,
        BearerToken("tk", "", "", "", "", 1L)
      )
      .returns(Left(ClientErr(403, "")))

    val req    = UploadFileReq("x", "my/file.txt", "cnt", "x".getBytes)
    val actual = cdeClient.uploadFile(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(UploadFileErr(req, ClientErr(403, ""))))
  }

  test("uploadFile return Left(ServerErr(403,)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http.putFileMultiPart _)
      .when(
        "https://api-uri/resources/x/my/file.txt",
        *,
        "file",
        "my/file.txt",
        "cnt",
        *,
        BearerToken("tk", "", "", "", "", 1L)
      )
      .returns(Left(ServerErr(505, "")))

    val req    = UploadFileReq("x", "my/file.txt", "cnt", "x".getBytes)
    val actual = cdeClient.uploadFile(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(UploadFileErr(req, ServerErr(505, ""))))
  }

  test("uploadFile return Left(GenericErr(301,)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http.putFileMultiPart _)
      .when(
        "https://api-uri/resources/x/my/file.txt",
        *,
        "file",
        "my/file.txt",
        "cnt",
        *,
        BearerToken("tk", "", "", "", "", 1L)
      )
      .returns(Left(GenericErr(301, "")))

    val req    = UploadFileReq("x", "my/file.txt", "cnt", "x".getBytes)
    val actual = cdeClient.uploadFile(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(UploadFileErr(req, GenericErr(301, ""))))
  }

  test("listJobs return Right(ListJobsRes)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ListJobsRes](_: String, _: Map[String, String], _: Auth)(_: Decoder[ListJobsRes]))
      .when("https://api-uri/jobs?limit=100&offset=0", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(
        Right(
          ListJobsRes(Seq(Job("x", "t", Seq.empty, "", None, None, None)), PaginationMeta(hasNext = false, 0, 1, 1))
        )
      )

    val expected = Right(
      ListJobsRes(Seq(Job("x", "t", Seq.empty, "", None, None, None)), PaginationMeta(hasNext = false, 0, 1, 1))
    )

    val actual = cdeClient.listJobs(CdeRequest(serviceDesc, vcDesc, ListJobsReq(None, None, None)))
    assert(actual == expected)
  }

  test("listJobs return Left(ListJobsErr(ClientError())") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ListJobsRes](_: String, _: Map[String, String], _: Auth)(_: Decoder[ListJobsRes]))
      .when("https://api-uri/jobs?limit=100&offset=0", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(405, "")))

    val actual = cdeClient.listJobs(CdeRequest(serviceDesc, vcDesc, ListJobsReq(None, None, None)))
    assert(actual == Left(ListJobsErr(ListJobsReq(None, None, None), ClientErr(405, ""))))
  }

  test("listJobs return Left(ListJobsErr(ServerError())") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ListJobsRes](_: String, _: Map[String, String], _: Auth)(_: Decoder[ListJobsRes]))
      .when("https://api-uri/jobs?limit=100&offset=0", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ServerErr(500, "")))

    val actual = cdeClient.listJobs(CdeRequest(serviceDesc, vcDesc, ListJobsReq(None, None, None)))
    assert(actual == Left(ListJobsErr(ListJobsReq(None, None, None), ServerErr(500, ""))))
  }

  test("listJobs return Left(ListJobsErr(GenericError())") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ListJobsRes](_: String, _: Map[String, String], _: Auth)(_: Decoder[ListJobsRes]))
      .when("https://api-uri/jobs?limit=100&offset=0", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(GenericErr(301, "")))

    val actual = cdeClient.listJobs(CdeRequest(serviceDesc, vcDesc, ListJobsReq(None, None, None)))
    assert(actual == Left(ListJobsErr(ListJobsReq(None, None, None), GenericErr(301, ""))))
  }

  test("getJob return Right(GetJobRes(Some))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(JobDetails("x", "y", "", "", "", Seq.empty, "", None, None)))

    val expected = Right(
      GetJobRes(Some(JobDetails("x", "y", "", "", "", Seq.empty, "", None, None)))
    )
    val actual   = cdeClient.getJob(CdeRequest(serviceDesc, vcDesc, GetJobReq("x")))
    assert(actual == expected)
  }

  test("getJob return Right(GetJobRes(None))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(404, "")))

    val expected = Right(GetJobRes(None))
    val actual   = cdeClient.getJob(CdeRequest(serviceDesc, vcDesc, GetJobReq("x")))
    assert(actual == expected)
  }

  test("getJob return Left(GetJobErr(ClientError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(405, "")))

    val actual = cdeClient.getJob(CdeRequest(serviceDesc, vcDesc, GetJobReq("x")))
    assert(actual == Left(GetJobErr(GetJobReq("x"), ClientErr(405, ""))))
  }

  test("getJob return Left(GetJobErr(ServerError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ServerErr(500, "")))

    val actual = cdeClient.getJob(CdeRequest(serviceDesc, vcDesc, GetJobReq("x")))
    assert(actual == Left(GetJobErr(GetJobReq("x"), ServerErr(500, ""))))
  }

  test("getJob return Left(GetJobErr(GenericError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(GenericErr(301, "")))

    val actual = cdeClient.getJob(CdeRequest(serviceDesc, vcDesc, GetJobReq("x")))
    assert(actual == Left(GetJobErr(GetJobReq("x"), GenericErr(301, ""))))
  }

  test("createJob return Right()") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .post[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Right(Some(Job("x", "z", Seq.empty, "", None, None, None))))

    val req    = CreateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.createJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("createJob return Left(CreateJobErr(ClientError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .post[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(ClientErr(404, "")))

    val req    = CreateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.createJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(CreateJobErr(req, ClientErr(404, ""))))
  }

  test("createJob return Left(CreateJobErr(ServerError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .post[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(ServerErr(500, "")))

    val req    = CreateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.createJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(CreateJobErr(req, ServerErr(500, ""))))
  }

  test("createJob return Left(CreateJobErr(GenericError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .post[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(GenericErr(301, "")))

    val req    = CreateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.createJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(CreateJobErr(req, GenericErr(301, ""))))
  }

  test("updateJob return Right()") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .patch[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs/x", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Right(Some(Job("x", "z", Seq.empty, "", None, None, None))))

    val req    = UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.updateJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("updateJob return Left(CreateJobErr(ClientError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .patch[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs/x", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(ClientErr(404, "")))

    val req    = UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.updateJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(UpdateJobErr(req, ClientErr(404, ""))))
  }

  test("updateJob return Left(CreateJobErr(ServerError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .patch[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs/x", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(ServerErr(500, "")))

    val req    = UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.updateJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(UpdateJobErr(req, ServerErr(500, ""))))
  }

  test("updateJob return Left(CreateJobErr(GenericError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .patch[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs/x", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(GenericErr(301, "")))

    val req    = UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.updateJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(UpdateJobErr(req, GenericErr(301, ""))))
  }

  test("upsertJob return Right() with not existing job") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(404, "")))

    (http
      .post[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Right(Some(Job("x", "y", Seq.empty, "", None, None, None))))

    val req    = UpsertJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.upsertJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("upsertJob return Right() with existing job") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(JobDetails("x", "y", "", "", "", Seq.empty, "", None, None)))

    (http
      .patch[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs/x", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Right(Some(Job("x", "y", Seq.empty, "", None, None, None))))

    val req    = UpsertJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.upsertJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("upsertJob return Left(GetJobErr(GenericError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(GenericErr(301, "")))

    val req    = UpsertJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.upsertJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(GetJobErr(GetJobReq("x"), GenericErr(301, ""))))
  }

  test("upsertJob return Left(CreateJobErr(GenericError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(404, "")))

    (http
      .post[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(GenericErr(301, "")))

    val req    = UpsertJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.upsertJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(
      actual == Left(CreateJobErr(CreateJobReq(Job("x", "y", Seq.empty, "", None, None, None)), GenericErr(301, "")))
    )
  }

  test("upsertJob return Left(UpdateJobErr(GenericError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(JobDetails("x", "y", "", "", "", Seq.empty, "", None, None)))

    (http
      .patch[Job, Job](_: String, _: Map[String, String], _: Job, _: Auth)(_: Encoder[Job], _: Decoder[Job]))
      .when("https://api-uri/jobs/x", *, *, BearerToken("tk", "", "", "", "", 1L), *, *)
      .returns(Left(GenericErr(301, "")))

    val req    = UpsertJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.upsertJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(
      actual == Left(UpdateJobErr(UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None)), GenericErr(301, "")))
    )
  }

  test("listJobRuns return Right(ListJobRunsRes)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ListJobRunsRes](_: String, _: Map[String, String], _: Auth)(_: Decoder[ListJobRunsRes]))
      .when(
        "https://api-uri/job-runs?limit=100&offset=0",
        *,
        BearerToken("tk", "", "", "", "", 1L),
        *
      )
      .returns(
        Right(
          ListJobRunsRes(
            Seq(JobRun(1, "s", "", "", None, None, "s", "", "", None)),
            PaginationMeta(hasNext = false, 0, 1, 1)
          )
        )
      )

    val req      = ListJobRunsReq(None, None, None)
    val actual   = cdeClient.listJobRuns(CdeRequest(serviceDesc, vcDesc, req))
    val expected = Right(
      ListJobRunsRes(
        Seq(JobRun(1, "s", "", "", None, None, "s", "", "", None)),
        PaginationMeta(hasNext = false, 0, 1, 1)
      )
    )
    assert(actual == expected)
  }

  test("listJobRuns return Left(ListJobRunsErr(ClientError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ListJobRunsRes](_: String, _: Map[String, String], _: Auth)(_: Decoder[ListJobRunsRes]))
      .when(
        "https://api-uri/job-runs?limit=100&offset=0",
        *,
        BearerToken("tk", "", "", "", "", 1L),
        *
      )
      .returns(Left(ClientErr(404, "")))

    val req      = ListJobRunsReq(None, None, None)
    val actual   = cdeClient.listJobRuns(CdeRequest(serviceDesc, vcDesc, req))
    val expected = Left(ListJobRunsErr(req, ClientErr(404, "")))
    assert(actual == expected)
  }

  test("listJobRuns return Left(ListJobRunsErr(ServerError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ListJobRunsRes](_: String, _: Map[String, String], _: Auth)(_: Decoder[ListJobRunsRes]))
      .when(
        "https://api-uri/job-runs?limit=100&offset=0",
        *,
        BearerToken("tk", "", "", "", "", 1L),
        *
      )
      .returns(Left(ServerErr(500, "")))

    val req      = ListJobRunsReq(None, None, None)
    val actual   = cdeClient.listJobRuns(CdeRequest(serviceDesc, vcDesc, req))
    val expected = Left(ListJobRunsErr(req, ServerErr(500, "")))
    assert(actual == expected)
  }

  test("listJobRuns return Left(ListJobRunsErr(GenericError))") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ListJobRunsRes](_: String, _: Map[String, String], _: Auth)(_: Decoder[ListJobRunsRes]))
      .when(
        "https://api-uri/job-runs?limit=100&offset=0",
        *,
        BearerToken("tk", "", "", "", "", 1L),
        *
      )
      .returns(Left(GenericErr(301, "")))

    val req      = ListJobRunsReq(None, None, None)
    val actual   = cdeClient.listJobRuns(CdeRequest(serviceDesc, vcDesc, req))
    val expected = Left(ListJobRunsErr(req, GenericErr(301, "")))
    assert(actual == expected)
  }

  test("deleteJob return Right()") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/jobs/jobName", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(None))

    val req    = DeleteJobReq("jobName")
    val actual = cdeClient.deleteJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("deleteJob return Left(DeleteJobErr(ClientError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/jobs/jobName", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(404, "")))

    val req    = DeleteJobReq("jobName")
    val actual = cdeClient.deleteJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(DeleteJobErr(req, ClientErr(404, ""))))
  }

  test("deleteJob return Left(DeleteJobErr(ServerError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/jobs/jobName", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ServerErr(500, "")))

    val req    = DeleteJobReq("jobName")
    val actual = cdeClient.deleteJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(DeleteJobErr(req, ServerErr(500, ""))))
  }

  test("deleteJob return Left(DeleteJobErr(GenericError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/jobs/jobName", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(GenericErr(301, "")))

    val req    = DeleteJobReq("jobName")
    val actual = cdeClient.deleteJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(DeleteJobErr(req, GenericErr(301, ""))))
  }

  test(s"safeDeleteJob return Right() with Right(None)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(404, "")))

    val req    = CdeRequest(serviceDesc, vcDesc, DeleteJobReq("x"))
    val actual = cdeClient.safeDeleteJob(req)
    assert(actual == Right())
  }

  test(s"safeDeleteJob return Right() with existing job") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(JobDetails("x", "y", "", "", "", Seq.empty, "", None, None)))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(None))

    val req    = CdeRequest(serviceDesc, vcDesc, DeleteJobReq("x"))
    val actual = cdeClient.safeDeleteJob(req)
    assert(actual == Right())
  }

  test(s"safeDeleteJob return Left(GetJobError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ServerErr(500, "")))

    val req    = CdeRequest(serviceDesc, vcDesc, DeleteJobReq("x"))
    val actual = cdeClient.safeDeleteJob(req)
    assert(actual == Left(GetJobErr(GetJobReq("x"), ServerErr(500, ""))))
  }

  test(s"safeDeleteJob return Left(DeleteJobErr)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[JobDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[JobDetails]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(JobDetails("x", "y", "", "", "", Seq.empty, "", None, None)))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/jobs/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ServerErr(500, "")))

    val req    = CdeRequest(serviceDesc, vcDesc, DeleteJobReq("x"))
    val actual = cdeClient.safeDeleteJob(req)
    assert(actual == Left(DeleteJobErr(DeleteJobReq("x"), ServerErr(500, ""))))
  }

  test("deleteResource return Right()") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/resources/resourceName", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(None))

    val req    = DeleteResourceReq("resourceName")
    val actual = cdeClient.deleteResource(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("deleteResource return Left(DeleteResourceErr(ClientError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/resources/resourceName", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(404, "")))

    val req    = DeleteResourceReq("resourceName")
    val actual = cdeClient.deleteResource(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(DeleteResourceErr(req, ClientErr(404, ""))))
  }

  test("deleteResource return Left(DeleteResourceErr(ServerError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/resources/resourceName", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ServerErr(500, "")))

    val req    = DeleteResourceReq("resourceName")
    val actual = cdeClient.deleteResource(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(DeleteResourceErr(req, ServerErr(500, ""))))
  }

  test("deleteResource return Left(DeleteResourceErr(GenericError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/resources/resourceName", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(GenericErr(301, "")))

    val req    = DeleteResourceReq("resourceName")
    val actual = cdeClient.deleteResource(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(DeleteResourceErr(req, GenericErr(301, ""))))
  }

  test(s"safeDeleteResource return Right() with Right(None)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ClientErr(404, "")))

    val req    = CdeRequest(serviceDesc, vcDesc, DeleteResourceReq("x"))
    val actual = cdeClient.safeDeleteResource(req)
    assert(actual == Right())
  }

  test(s"safeDeleteResource return Right() with existing resource") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(ResourceDetails("x", "y", None, "", "", "", "", "")))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(None))

    val req    = CdeRequest(serviceDesc, vcDesc, DeleteResourceReq("x"))
    val actual = cdeClient.safeDeleteResource(req)
    assert(actual == Right())
  }

  test(s"safeDeleteResource return Left(GetResourceError)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ServerErr(500, "")))

    val req    = CdeRequest(serviceDesc, vcDesc, DeleteResourceReq("x"))
    val actual = cdeClient.safeDeleteResource(req)
    assert(actual == Left(GetResourceErr(GetResourceReq("x"), ServerErr(500, ""))))
  }

  test(s"safeDeleteResource return Left(DeleteResourceErr)") {
    (tokenProvider.get _)
      .when("https://mcf/gateway/authtkn/knoxtoken/api/v1/token", *)
      .returns(Right(BearerToken("tk", "", "", "", "", 1L)))

    (http
      .get[ResourceDetails](_: String, _: Map[String, String], _: Auth)(_: Decoder[ResourceDetails]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Right(ResourceDetails("x", "y", None, "", "", "", "", "")))

    (http
      .delete[Unit](_: String, _: Map[String, String], _: Auth)(_: Decoder[Unit]))
      .when("https://api-uri/resources/x", *, BearerToken("tk", "", "", "", "", 1L), *)
      .returns(Left(ServerErr(500, "")))

    val req    = CdeRequest(serviceDesc, vcDesc, DeleteResourceReq("x"))
    val actual = cdeClient.safeDeleteResource(req)
    assert(actual == Left(DeleteResourceErr(DeleteResourceReq("x"), ServerErr(500, ""))))
  }
}
