package it.agilelab.provisioning.commons.client.cdp.de.cluster

import com.cloudera.cdp.de.model.{ ServiceDescription, VcDescription }
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.de.cluster.CdeClusterClientError._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base.Job
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.request._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.response._
import it.agilelab.provisioning.commons.http.HttpErrors.ClientErr
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

class DefaultCdeClusterClientWithAuditTest extends AnyFunSuite with BeforeAndAfterAll with MockFactory {

  val serviceDesc: ServiceDescription           = mock[ServiceDescription]
  val vcDesc: VcDescription                     = mock[VcDescription]
  val defaultCdeClient: DefaultCdeClusterClient = stub[DefaultCdeClusterClient]
  val audit: Audit                              = mock[Audit]
  val cdeClient                                 = new DefaultCdeClusterClientWithAudit(defaultCdeClient, audit)

  test("getResource logs success info") {
    (defaultCdeClient.getResource _).when(*).returns(Right(GetResourceRes(None)))
    (audit.info _)
      .expects("GetResourceReq(x) completed successfully")
      .once()
    val req    = CdeRequest(serviceDesc, vcDesc, GetResourceReq("x"))
    val actual = cdeClient.getResource(req)
    assert(actual == Right(GetResourceRes(None)))
  }

  test("getResource logs error info") {
    (defaultCdeClient.getResource _).when(*).returns(Left(GetResourceErr(GetResourceReq("x"), ClientErr(404, "x"))))
    (audit.error _)
      .expects("GetResourceReq(x) failed. Details: GetResourceErr(GetResourceReq(x),ClientErr(404,x))")
      .once()
    val req    = CdeRequest(serviceDesc, vcDesc, GetResourceReq("x"))
    val actual = cdeClient.getResource(req)
    assert(actual == Left(GetResourceErr(GetResourceReq("x"), ClientErr(404, "x"))))
  }

  test("createResource logs success info") {
    (defaultCdeClient.createResource _).when(*).returns(Right())
    (audit.info _)
      .expects("CreateResourceReq(x,y,z,None) completed successfully")
      .once()
    val req    = CreateResourceReq("x", "y", "z", None)
    val actual = cdeClient.createResource(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("createResource logs error info") {
    (defaultCdeClient.createResource _)
      .when(*)
      .returns(Left(CreateResourceErr(CreateResourceReq("x", "y", "z", None), ClientErr(404, "x"))))
    (audit.error _)
      .expects(
        "CreateResourceReq(x,y,z,None) failed. Details: CreateResourceErr(CreateResourceReq(x,y,z,None),ClientErr(404,x))"
      )
      .once()
    val req    = CreateResourceReq("x", "y", "z", None)
    val actual = cdeClient.createResource(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(CreateResourceErr(CreateResourceReq("x", "y", "z", None), ClientErr(404, "x"))))
  }

  test("safeCreateResource logs success info") {
    (defaultCdeClient.safeCreateResource _).when(*).returns(Right())
    (audit.info _)
      .expects("CreateResourceReq(x,y,z,None) completed successfully")
      .once()
    val req    = CreateResourceReq("x", "y", "z", None)
    val actual = cdeClient.safeCreateResource(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test(s"safeCreateResource logs error info") {
    (defaultCdeClient.safeCreateResource _)
      .when(*)
      .returns(Left(CreateResourceErr(CreateResourceReq("x", "y", "z", None), ClientErr(404, "x"))))
    (audit.error _)
      .expects(
        "CreateResourceReq(x,y,z,None) failed. Details: CreateResourceErr(CreateResourceReq(x,y,z,None),ClientErr(404,x))"
      )
      .once()
    val req    = CdeRequest(serviceDesc, vcDesc, CreateResourceReq("x", "y", "z", None))
    val actual = cdeClient.safeCreateResource(req)
    assert(actual == Left(CreateResourceErr(CreateResourceReq("x", "y", "z", None), ClientErr(404, "x"))))
  }

  test(s"uploadFile logs success info") {
    (defaultCdeClient.uploadFile _).when(*).returns(Right())
    (audit.info _)
      .expects(where { info: String =>
        info.startsWith("UploadFileReq(x,y,z,") &&
        info.endsWith("completed successfully")
      })
      .once()

    val req    = CdeRequest(serviceDesc, vcDesc, UploadFileReq("x", "y", "z", "x".getBytes))
    val actual = cdeClient.uploadFile(req)
    assert(actual == Right())
  }

  test(s"uploadFile logs error info") {
    val req = UploadFileReq("x", "y", "z", "x".getBytes)

    (defaultCdeClient.uploadFile _)
      .when(*)
      .returns(Left(UploadFileErr(req, ClientErr(404, "x"))))
    (audit.error _)
      .expects(where { info: String =>
        info.startsWith("UploadFileReq(x,y,z,") &&
        info.endsWith(",ClientErr(404,x))")
      })
      .once()

    val actual = cdeClient.uploadFile(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Left(UploadFileErr(req, ClientErr(404, "x"))))
  }

  test(s"listJobs logs success info") {
    (defaultCdeClient.listJobs _)
      .when(*)
      .returns(Right(ListJobsRes(Seq.empty, PaginationMeta(hasNext = false, 0, 0, 0))))
    (audit.info _)
      .expects(
        "ListJobsReq(None,None,None) completed successfully"
      )
      .once()

    val req    = CdeRequest(serviceDesc, vcDesc, ListJobsReq(None, None, None))
    val actual = cdeClient.listJobs(req)
    assert(actual == Right(ListJobsRes(Seq.empty, PaginationMeta(hasNext = false, 0, 0, 0))))
  }

  test(s"listJobs logs error info") {
    (defaultCdeClient.listJobs _).when(*).returns(Left(ListJobsErr(ListJobsReq(None, None, None), ClientErr(404, "x"))))
    (audit.error _)
      .expects(
        "ListJobsReq(None,None,None) failed. Details: ListJobsErr(ListJobsReq(None,None,None),ClientErr(404,x))"
      )
      .once()

    val req    = CdeRequest(serviceDesc, vcDesc, ListJobsReq(None, None, None))
    val actual = cdeClient.listJobs(req)
    assert(actual == Left(ListJobsErr(ListJobsReq(None, None, None), ClientErr(404, "x"))))
  }

  test("getJob logs success info") {
    (defaultCdeClient.getJob _).when(*).returns(Right(GetJobRes(None)))
    (audit.info _)
      .expects("GetJobReq(x) completed successfully")
      .once()
    val req    = CdeRequest(serviceDesc, vcDesc, GetJobReq("x"))
    val actual = cdeClient.getJob(req)
    assert(actual == Right(GetJobRes(None)))
  }

  test("getJob logs error info") {
    (defaultCdeClient.getJob _).when(*).returns(Left(GetJobErr(GetJobReq("x"), ClientErr(404, "x"))))
    (audit.error _)
      .expects("GetJobReq(x) failed. Details: GetJobErr(GetJobReq(x),ClientErr(404,x))")
      .once()
    val req    = CdeRequest(serviceDesc, vcDesc, GetJobReq("x"))
    val actual = cdeClient.getJob(req)
    assert(actual == Left(GetJobErr(GetJobReq("x"), ClientErr(404, "x"))))
  }

  test("createJob logs success info") {
    (defaultCdeClient.createJob _).when(*).returns(Right())
    (audit.info _)
      .expects("CreateJobReq(Job(x,y,List(),,None,None,None)) completed successfully")
      .once()
    val req    = CreateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.createJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("createJob logs error info") {
    (defaultCdeClient.createJob _)
      .when(*)
      .returns(Left(CreateJobErr(CreateJobReq(Job("x", "y", Seq.empty, "", None, None, None)), ClientErr(404, "x"))))
    (audit.error _)
      .expects(
        "CreateJobReq(Job(x,y,List(),,None,None,None)) failed. Details: CreateJobErr(CreateJobReq(Job(x,y,List(),,None,None,None)),ClientErr(404,x))"
      )
      .once()
    val req      = CreateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual   = cdeClient.createJob(CdeRequest(serviceDesc, vcDesc, req))
    val expected = Left(CreateJobErr(CreateJobReq(Job("x", "y", Seq.empty, "", None, None, None)), ClientErr(404, "x")))
    assert(actual == expected)
  }

  test("updateJob logs success info") {
    (defaultCdeClient.updateJob _).when(*).returns(Right())
    (audit.info _)
      .expects("UpdateJobReq(Job(x,y,List(),,None,None,None)) completed successfully")
      .once()
    val req    = UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.updateJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("updateJob logs error info") {
    (defaultCdeClient.updateJob _)
      .when(*)
      .returns(Left(UpdateJobErr(UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None)), ClientErr(404, "x"))))
    (audit.error _)
      .expects(
        "UpdateJobReq(Job(x,y,List(),,None,None,None)) failed. Details: UpdateJobErr(UpdateJobReq(Job(x,y,List(),,None,None,None)),ClientErr(404,x))"
      )
      .once()
    val req      = UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual   = cdeClient.updateJob(CdeRequest(serviceDesc, vcDesc, req))
    val expected = Left(UpdateJobErr(UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None)), ClientErr(404, "x")))
    assert(actual == expected)
  }

  test("upsertJob logs success info") {
    (defaultCdeClient.upsertJob _).when(*).returns(Right())
    (audit.info _)
      .expects("UpsertJobReq(Job(x,y,List(),,None,None,None)) completed successfully")
      .once()
    val req    = UpsertJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual = cdeClient.upsertJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("upsertJob logs error info") {
    (defaultCdeClient.upsertJob _)
      .when(*)
      .returns(Left(UpdateJobErr(UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None)), ClientErr(404, "x"))))
    (audit.error _)
      .expects(
        "UpsertJobReq(Job(x,y,List(),,None,None,None)) failed. Details: UpdateJobErr(UpdateJobReq(Job(x,y,List(),,None,None,None)),ClientErr(404,x))"
      )
      .once()
    val req      = UpsertJobReq(Job("x", "y", Seq.empty, "", None, None, None))
    val actual   = cdeClient.upsertJob(CdeRequest(serviceDesc, vcDesc, req))
    val expected = Left(UpdateJobErr(UpdateJobReq(Job("x", "y", Seq.empty, "", None, None, None)), ClientErr(404, "x")))
    assert(actual == expected)
  }

  test(s"listJobRuns logs success info") {
    (defaultCdeClient.listJobRuns _)
      .when(*)
      .returns(Right(ListJobRunsRes(Seq.empty, PaginationMeta(hasNext = false, 0, 0, 0))))
    (audit.info _)
      .expects(
        "ListJobRunsReq(None,None,None) completed successfully"
      )
      .once()

    val req      = CdeRequest(serviceDesc, vcDesc, ListJobRunsReq(None, None, None))
    val actual   = cdeClient.listJobRuns(req)
    val expected = Right(ListJobRunsRes(Seq.empty, PaginationMeta(hasNext = false, 0, 0, 0)))
    assert(actual == expected)
  }

  test(s"listJobRuns logs error info") {
    (defaultCdeClient.listJobRuns _)
      .when(*)
      .returns(Left(ListJobRunsErr(ListJobRunsReq(None, None, None), ClientErr(404, "x"))))
    (audit.error _)
      .expects(
        "ListJobRunsReq(None,None,None) failed. Details: ListJobRunsErr(ListJobRunsReq(None,None,None),ClientErr(404,x))"
      )
      .once()

    val req      = CdeRequest(serviceDesc, vcDesc, ListJobRunsReq(None, None, None))
    val actual   = cdeClient.listJobRuns(req)
    val expected = Left(ListJobRunsErr(ListJobRunsReq(None, None, None), ClientErr(404, "x")))
    assert(actual == expected)
  }

  test("deleteJob logs success info") {
    (defaultCdeClient.deleteJob _).when(*).returns(Right())
    (audit.info _)
      .expects("DeleteJobReq(jobName) completed successfully")
      .once()
    val req    = DeleteJobReq("jobName")
    val actual = cdeClient.deleteJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("deleteJob logs error info") {
    (defaultCdeClient.deleteJob _)
      .when(*)
      .returns(Left(DeleteJobErr(DeleteJobReq("jobName"), ClientErr(404, "x"))))
    (audit.error _)
      .expects(
        "DeleteJobReq(jobName) failed. Details: DeleteJobErr(DeleteJobReq(jobName),ClientErr(404,x))"
      )
      .once()
    val req      = DeleteJobReq("jobName")
    val actual   = cdeClient.deleteJob(CdeRequest(serviceDesc, vcDesc, req))
    val expected = Left(DeleteJobErr(DeleteJobReq("jobName"), ClientErr(404, "x")))
    assert(actual == expected)
  }

  test("safeDeleteJob logs success info") {
    (defaultCdeClient.safeDeleteJob _).when(*).returns(Right())
    (audit.info _)
      .expects("DeleteJobReq(jobName) completed successfully")
      .once()
    val req    = DeleteJobReq("jobName")
    val actual = cdeClient.safeDeleteJob(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("safeDeleteJob logs error info") {
    (defaultCdeClient.safeDeleteJob _)
      .when(*)
      .returns(Left(DeleteJobErr(DeleteJobReq("jobName"), ClientErr(404, "x"))))
    (audit.error _)
      .expects(
        "DeleteJobReq(jobName) failed. Details: DeleteJobErr(DeleteJobReq(jobName),ClientErr(404,x))"
      )
      .once()
    val req      = DeleteJobReq("jobName")
    val actual   = cdeClient.safeDeleteJob(CdeRequest(serviceDesc, vcDesc, req))
    val expected = Left(DeleteJobErr(DeleteJobReq("jobName"), ClientErr(404, "x")))
    assert(actual == expected)
  }

  test("deleteResource logs success info") {
    (defaultCdeClient.deleteResource _).when(*).returns(Right())
    (audit.info _)
      .expects("DeleteResourceReq(resourceName) completed successfully")
      .once()
    val req    = DeleteResourceReq("resourceName")
    val actual = cdeClient.deleteResource(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("deleteResource logs error info") {
    (defaultCdeClient.deleteResource _)
      .when(*)
      .returns(Left(DeleteResourceErr(DeleteResourceReq("resourceName"), ClientErr(404, "x"))))
    (audit.error _)
      .expects(
        "DeleteResourceReq(resourceName) failed. Details: DeleteResourceErr(DeleteResourceReq(resourceName),ClientErr(404,x))"
      )
      .once()
    val req      = DeleteResourceReq("resourceName")
    val actual   = cdeClient.deleteResource(CdeRequest(serviceDesc, vcDesc, req))
    val expected = Left(DeleteResourceErr(DeleteResourceReq("resourceName"), ClientErr(404, "x")))
    assert(actual == expected)
  }

  test("safeDeleteResource logs success info") {
    (defaultCdeClient.safeDeleteResource _).when(*).returns(Right())
    (audit.info _)
      .expects("DeleteResourceReq(resourceName) completed successfully")
      .once()
    val req    = DeleteResourceReq("resourceName")
    val actual = cdeClient.safeDeleteResource(CdeRequest(serviceDesc, vcDesc, req))
    assert(actual == Right())
  }

  test("safeDeleteResource logs error info") {
    (defaultCdeClient.safeDeleteResource _)
      .when(*)
      .returns(Left(DeleteResourceErr(DeleteResourceReq("resourceName"), ClientErr(404, "x"))))
    (audit.error _)
      .expects(
        "DeleteResourceReq(resourceName) failed. Details: DeleteResourceErr(DeleteResourceReq(resourceName),ClientErr(404,x))"
      )
      .once()
    val req      = DeleteResourceReq("resourceName")
    val actual   = cdeClient.safeDeleteResource(CdeRequest(serviceDesc, vcDesc, req))
    val expected = Left(DeleteResourceErr(DeleteResourceReq("resourceName"), ClientErr(404, "x")))
    assert(actual == expected)
  }
}
