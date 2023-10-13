package it.agilelab.provisioning.commons.client.cdp.de.cluster

import cats.implicits.toBifunctorOps
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.de.cluster.CdeClusterClientError.CdeClusterClientInitErr
import it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider.TokenProvider
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.request._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.response._
import it.agilelab.provisioning.commons.http.Auth.BasicCredential
import it.agilelab.provisioning.commons.http.Http

trait CdeClusterClient {

  /** Find resource by name
    * @param req: an instance of [[CdeRequest]] of String type
    * @return Right(Some([[GetResourceRes]])) if resource exists
    *         Right(None) if resource does not exist
    *         Left(Error) if otherwise
    */
  def getResource(
    req: CdeRequest[GetResourceReq]
  ): Either[CdeClusterClientError, GetResourceRes]

  /** Create resource
    * @param req an instance of [[CdeRequest]] of [[CreateResourceReq]] type
    * @return Right() if resource are created
    *         Left(Error) otherwise
    */
  def createResource(
    req: CdeRequest[CreateResourceReq]
  ): Either[CdeClusterClientError, Unit]

  /** Safely create resource
    *
    * Create resource if and only if the resource does not exists
    *
    * @param req an instance of [[CdeRequest]] of [[CreateResourceReq]] type
    * @return Right() if resource are created or already exists
    *         Left(Error) otherwise
    */
  def safeCreateResource(
    req: CdeRequest[CreateResourceReq]
  ): Either[CdeClusterClientError, Unit]

  /** Upload file
    * @param req: an instance of [[CdeRequest]] of [[UploadFileReq]] type
    * @return Right() if file are uploaded
    *         Left(Error) otherwise
    */
  def uploadFile(
    req: CdeRequest[UploadFileReq]
  ): Either[CdeClusterClientError, Unit]

  /** List jobs
    * @param req ListJobReq
    * @return
    */
  def listJobs(
    req: CdeRequest[ListJobsReq]
  ): Either[CdeClusterClientError, ListJobsRes]

  /** Find job by name
    * @param req: an instance of [[CdeRequest]] of [[String]] type
    * @return Right(Some([[GetJobReq]]) if job exists
    *         Right(None) if job does not exists
    *         Left(Error) otherwise
    */
  def getJob(
    req: CdeRequest[GetJobReq]
  ): Either[CdeClusterClientError, GetJobRes]

  /** Create job
    * @param req: an instance of [[CdeRequest]] of [[CreateJobReq]] type
    * @return Right() if job are created
    *         Left(Error) otherwise
    */
  def createJob(
    req: CdeRequest[CreateJobReq]
  ): Either[CdeClusterClientError, Unit]

  /** Update job
    * @param req: an Instance of [[CdeRequest]] of [[UpdateJobReq]] type
    * @return Right() if job are updated
    *         Left(Error) otherwise
    */
  def updateJob(
    req: CdeRequest[UpdateJobReq]
  ): Either[CdeClusterClientError, Unit]

  /** Upsert Job
    *
    * Create the job if does not exists otherwise update it
    *
    * @param req: an instance of [[CdeRequest]] of [[[UpsertJobReq]]] type
    * @return Right() if job are updated or created
    *         Left(Error) otherwise
    */
  def upsertJob(
    req: CdeRequest[UpsertJobReq]
  ): Either[CdeClusterClientError, Unit]

  /** list all job runs
    * @param req: CdeRequest[ListJobRunsReq]
    * @return ListJobRunsRes
    */
  def listJobRuns(
    req: CdeRequest[ListJobRunsReq]
  ): Either[CdeClusterClientError, ListJobRunsRes]

  /** Delete job
    *
    * @param req  : an instance of [[CdeRequest]] of [[DeleteJobReq]] type
    * @return Right() if job has been created
    *         Left(Error) otherwise
    */
  def deleteJob(
    req: CdeRequest[DeleteJobReq]
  ): Either[CdeClusterClientError, Unit]

  /** Safely delete job
    *
    * Delete job if and only if the job already exists
    *
    * @param req an instance of [[CdeRequest]] of [[DeleteJobReq]] type
    * @return Right() if job has been deleted or already exists
    *         Left(Error) otherwise
    */
  def safeDeleteJob(req: CdeRequest[DeleteJobReq]): Either[CdeClusterClientError, Unit]

  /** Delete resource
    *
    * @param req : an instance of [[CdeRequest]] of [[DeleteResourceReq]] type
    * @return Right() if resource has been created
    *         Left(Error) otherwise
    */
  def deleteResource(req: CdeRequest[DeleteResourceReq]): Either[CdeClusterClientError, Unit]

  /** Safely delete resource
    *
    * Delete resource if and only if the resource already exists
    *
    * @param req an instance of [[CdeRequest]] of [[DeleteResourceReq]] type
    * @return Right() if resource has been deleted or already exists
    *         Left(Error) otherwise
    */
  def safeDeleteResource(req: CdeRequest[DeleteResourceReq]): Either[CdeClusterClientError, Unit]

}

object CdeClusterClient {

  /** Create a [[DefaultCdeClusterClient]]
    *
    * Automatically create a [[TokenProvider]] an [[Http]] instance
    * @return Right(CdeClusterClient)
    *         Left(CdeClusterClientError)
    */
  def default(credential: BasicCredential): Either[CdeClusterClientError, CdeClusterClient] =
    for {
      tokenProvider    <- TokenProvider.default().leftMap(e => CdeClusterClientInitErr(e.error))
      cdeClusterClient <- try Right(new DefaultCdeClusterClient(Http.default(), tokenProvider, credential))
                          catch { case t: Throwable => Left(CdeClusterClientInitErr(t)) }
    } yield cdeClusterClient

  /** Create a [[DefaultCdeClusterClientWithAudit]]
    *
    * Automatically create a [[CdeClusterClient]] with [[TokenProvider]] and [[Http]] instances
    * @return Right(CdeClusterClient)
    *         Left(CdeClusterClientError)
    */
  def defaultWithAudit(credential: BasicCredential): Either[CdeClusterClientError, CdeClusterClient] =
    for {
      tokenProvider    <- TokenProvider.defaultWithAudit().leftMap(e => CdeClusterClientInitErr(e.error))
      cdeClusterClient <- try Right(
                            new DefaultCdeClusterClientWithAudit(
                              new DefaultCdeClusterClient(Http.defaultWithAudit(), tokenProvider, credential),
                              Audit.default("CdeClusterClient")
                            )
                          )
                          catch { case t: Throwable => Left(CdeClusterClientInitErr(t)) }
    } yield cdeClusterClient

}
