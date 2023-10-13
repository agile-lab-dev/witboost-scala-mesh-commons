package it.agilelab.provisioning.commons.client.cdp.de.cluster

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.request._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.response._

class DefaultCdeClusterClientWithAudit(
  cdeClient: CdeClusterClient,
  audit: Audit
) extends CdeClusterClient {

  override def getResource(
    req: CdeRequest[GetResourceReq]
  ): Either[CdeClusterClientError, GetResourceRes] = {
    val result = cdeClient.getResource(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def createResource(
    req: CdeRequest[CreateResourceReq]
  ): Either[CdeClusterClientError, Unit] = {
    val result = cdeClient.createResource(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def safeCreateResource(
    req: CdeRequest[CreateResourceReq]
  ): Either[CdeClusterClientError, Unit] = {
    val result = cdeClient.safeCreateResource(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def uploadFile(
    req: CdeRequest[UploadFileReq]
  ): Either[CdeClusterClientError, Unit] = {
    val result = cdeClient.uploadFile(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def listJobs(
    req: CdeRequest[ListJobsReq]
  ): Either[CdeClusterClientError, ListJobsRes] = {
    val result = cdeClient.listJobs(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def getJob(
    req: CdeRequest[GetJobReq]
  ): Either[CdeClusterClientError, GetJobRes] = {
    val result = cdeClient.getJob(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def createJob(req: CdeRequest[CreateJobReq]): Either[CdeClusterClientError, Unit] = {
    val result = cdeClient.createJob(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def updateJob(req: CdeRequest[UpdateJobReq]): Either[CdeClusterClientError, Unit] = {
    val result = cdeClient.updateJob(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def upsertJob(req: CdeRequest[UpsertJobReq]): Either[CdeClusterClientError, Unit] = {
    val result = cdeClient.upsertJob(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def listJobRuns(
    req: CdeRequest[ListJobRunsReq]
  ): Either[CdeClusterClientError, ListJobRunsRes] = {
    val result = cdeClient.listJobRuns(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def deleteJob(req: CdeRequest[DeleteJobReq]): Either[CdeClusterClientError, Unit] = {
    val result = cdeClient.deleteJob(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def safeDeleteJob(req: CdeRequest[DeleteJobReq]): Either[CdeClusterClientError, Unit] = {
    val result = cdeClient.safeDeleteJob(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def deleteResource(req: CdeRequest[DeleteResourceReq]): Either[CdeClusterClientError, Unit] = {
    val result = cdeClient.deleteResource(req)
    auditWithinResult(req.spec, result)
    result
  }

  override def safeDeleteResource(req: CdeRequest[DeleteResourceReq]): Either[CdeClusterClientError, Unit] = {
    val result = cdeClient.safeDeleteResource(req)
    auditWithinResult(req.spec, result)
    result
  }

  private def auditWithinResult[A, B](
    request: A,
    result: Either[CdeClusterClientError, B]
  ): Unit =
    result match {
      case Right(_) => audit.info(show"${Show.fromToString[A].show(request)} completed successfully")
      case Left(l)  => audit.error(show"${Show.fromToString[A].show(request)} failed. Details: $l")
    }

}
