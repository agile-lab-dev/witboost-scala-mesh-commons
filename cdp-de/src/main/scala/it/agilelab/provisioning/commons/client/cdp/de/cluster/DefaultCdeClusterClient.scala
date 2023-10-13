package it.agilelab.provisioning.commons.client.cdp.de.cluster

import cats.implicits.toBifunctorOps
import com.cloudera.cdp.de.model.ServiceDescription
import io.circe.Decoder
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.CdeClusterClientError._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider.TokenProvider
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base.{ Job, JobDetails, ResourceDetails }
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.extension.CdeModelExtensions.ServiceDescriptionOps
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.request._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.response._
import it.agilelab.provisioning.commons.http.Auth.{ BasicCredential, BearerToken }
import it.agilelab.provisioning.commons.http.HttpErrors.ClientErr
import it.agilelab.provisioning.commons.http.{ Http, HttpErrors }

/** Default [[CdeClusterClient]] implementation
  * @param http: An instance of [[Http]] that will be used for execute rest api call
  * @param tokenProvider: An instance of [[TokenProvider]] that will be used to authenticate the client to the CdeCluster
  * @param credential: An instance of [[BasicCredential]] that will be used as credential for generate the token
  */
class DefaultCdeClusterClient(http: Http, tokenProvider: TokenProvider, credential: BasicCredential)
    extends CdeClusterClient {

  override def getResource(
    req: CdeRequest[GetResourceReq]
  ): Either[CdeClusterClientError, GetResourceRes] =
    for {
      token    <- authenticate(req.service)
      uri       = s"${req.vc.getVcApiUrl}/resources/${req.spec.resourceName}"
      resource <- get[ResourceDetails](uri, token).leftMap(e => GetResourceErr(req.spec, e))
    } yield GetResourceRes(resource)

  override def createResource(
    req: CdeRequest[CreateResourceReq]
  ): Either[CdeClusterClientError, Unit] =
    for {
      token    <- authenticate(req.service)
      uri       = s"${req.vc.getVcApiUrl}/resources"
      response <- postRes(req.spec, token, uri)
    } yield response

  override def safeCreateResource(
    req: CdeRequest[CreateResourceReq]
  ): Either[CdeClusterClientError, Unit] =
    for {
      token       <- authenticate(req.service)
      getApiUri    = s"${req.vc.getVcApiUrl}/resources/${req.spec.name}"
      createApiUri = s"${req.vc.getVcApiUrl}/resources"
      optResource <- get[ResourceDetails](getApiUri, token)
                       .leftMap(e => GetResourceErr(GetResourceReq(req.spec.name), e))
      response    <- if (optResource.isEmpty) postRes(req.spec, token, createApiUri) else Right()
    } yield response

  private def authenticate(
    serviceDesc: ServiceDescription
  ): Either[CdeClusterClientError, BearerToken] =
    tokenProvider
      .get(s"${serviceDesc.getServiceUrl}/gateway/authtkn/knoxtoken/api/v1/token", credential)
      .leftMap(e => AuthErr(e))

  private def get[A](endpoint: String, token: BearerToken)(implicit
    decoder: Decoder[A]
  ): Either[HttpErrors, Option[A]] =
    http.get[A](endpoint, Map.empty, token) match {
      case Right(r)                => Right(Some(r))
      case Left(ClientErr(404, _)) => Right(None)
      case Left(e)                 => Left(e)
    }

  private def postRes(req: CreateResourceReq, token: BearerToken, apiUri: String) =
    http.post[CreateResourceReq, Unit](apiUri, Map.empty, req, token) match {
      case Right(_)                => Right()
      case Left(ClientErr(409, _)) => Left(ResourceAlreadyExistsErr(req))
      case Left(e: HttpErrors)     => Left(CreateResourceErr(req, e))
    }

  override def uploadFile(
    req: CdeRequest[UploadFileReq]
  ): Either[CdeClusterClientError, Unit] =
    for {
      token    <- authenticate(req.service)
      apiUri    = s"${req.vc.getVcApiUrl}/resources/${req.spec.resource}/${req.spec.filePath}"
      response <- http
                    .putFileMultiPart(
                      apiUri,
                      Map.empty,
                      "file",
                      req.spec.filePath,
                      req.spec.mimeType,
                      req.spec.file,
                      token
                    )
                    .leftMap(e => UploadFileErr(req.spec, e))
    } yield response

  override def listJobs(
    req: CdeRequest[ListJobsReq]
  ): Either[CdeClusterClientError, ListJobsRes] =
    for {
      token      <- authenticate(req.service)
      offset      = req.spec.offset.map(o => s"offset=${o.toString}").getOrElse("offset=0")
      limit       = req.spec.limit.map(l => s"limit=${l.toString}").getOrElse("limit=100")
      filters     = req.spec.filter.map(_.map(f => s"filter=$f").mkString("&"))
      queryString = filters.map(f => s"$limit&$offset&$f").getOrElse(s"$limit&$offset")
      uri         = s"${req.vc.getVcApiUrl}/jobs?$queryString"
      jobs       <- http.get[ListJobsRes](uri, Map.empty, token).leftMap(e => ListJobsErr(req.spec, e))
    } yield jobs

  override def getJob(
    req: CdeRequest[GetJobReq]
  ): Either[CdeClusterClientError, GetJobRes] =
    for {
      token <- authenticate(req.service)
      uri    = s"${req.vc.getVcApiUrl}/jobs/${req.spec.jobName}"
      job   <- get[JobDetails](uri, token).leftMap(e => GetJobErr(req.spec, e))
    } yield GetJobRes(job)

  override def createJob(
    req: CdeRequest[CreateJobReq]
  ): Either[CdeClusterClientError, Unit] =
    for {
      token <- authenticate(req.service)
      uri    = s"${req.vc.getVcApiUrl}/jobs"
      _     <- postJob(req.spec, token, uri)
    } yield ()

  override def updateJob(
    req: CdeRequest[UpdateJobReq]
  ): Either[CdeClusterClientError, Unit] =
    for {
      token <- authenticate(req.service)
      uri    = s"${req.vc.getVcApiUrl}/jobs/${req.spec.job.name}"
      _     <- patchJob(req.spec, token, uri)
    } yield ()

  override def upsertJob(
    req: CdeRequest[UpsertJobReq]
  ): Either[CdeClusterClientError, Unit] =
    for {
      token       <- authenticate(req.service)
      postApiUri   = s"${req.vc.getVcApiUrl}/jobs"
      patchApiUri  = s"${req.vc.getVcApiUrl}/jobs/${req.spec.job.name}"
      getApiUri    = s"${req.vc.getVcApiUrl}/jobs/${req.spec.job.name}"
      getResponse <- get[JobDetails](getApiUri, token).leftMap(e => GetJobErr(GetJobReq(req.spec.job.name), e))
      _           <-
        if (getResponse.isEmpty) postJob(CreateJobReq(req.spec.job), token, postApiUri)
        else patchJob(UpdateJobReq(req.spec.job), token, patchApiUri)
    } yield ()

  private def patchJob(req: UpdateJobReq, token: BearerToken, patchApiUri: String) =
    http
      .patch[Job, Job](patchApiUri, Map.empty, req.job, token)
      .leftMap(e => UpdateJobErr(req, e))

  private def postJob(req: CreateJobReq, token: BearerToken, postApiUri: String) =
    http
      .post[Job, Job](postApiUri, Map.empty, req.job, token)
      .leftMap(e => CreateJobErr(req, e))

  override def listJobRuns(
    req: CdeRequest[ListJobRunsReq]
  ): Either[CdeClusterClientError, ListJobRunsRes] =
    for {
      token      <- authenticate(req.service)
      offset      = req.spec.offset.map(o => s"offset=${o.toString}").getOrElse("offset=0")
      limit       = req.spec.limit.map(l => s"limit=${l.toString}").getOrElse("limit=100")
      filters     = req.spec.filter.map(_.map(f => s"filter=$f").mkString("&"))
      queryString = filters.map(f => s"$limit&$offset&$f").getOrElse(s"$limit&$offset")
      endpoint    = s"${req.vc.getVcApiUrl}/job-runs?$queryString"
      res        <- http
                      .get[ListJobRunsRes](endpoint, Map.empty, token)
                      .leftMap(e => ListJobRunsErr(req.spec, e))
    } yield res

  override def deleteJob(req: CdeRequest[DeleteJobReq]): Either[CdeClusterClientError, Unit] = for {
    token <- authenticate(req.service)
    uri    = s"${req.vc.getVcApiUrl}/jobs/${req.spec.jobName}"
    _     <- delJob(req.spec, token, uri)
  } yield ()

  override def safeDeleteJob(req: CdeRequest[DeleteJobReq]): Either[CdeClusterClientError, Unit] =
    for {
      token         <- authenticate(req.service)
      uri            = s"${req.vc.getVcApiUrl}/jobs/${req.spec.jobName}"
      optJobDetails <- get[JobDetails](uri, token).leftMap(e => GetJobErr(GetJobReq(req.spec.jobName), e))
      response      <- if (optJobDetails.isEmpty) Right() else delJob(req.spec, token, uri)
    } yield response

  override def deleteResource(req: CdeRequest[DeleteResourceReq]): Either[CdeClusterClientError, Unit] = for {
    token <- authenticate(req.service)
    uri    = s"${req.vc.getVcApiUrl}/resources/${req.spec.resourceName}"
    _     <- delRes(req.spec, token, uri)
  } yield ()

  override def safeDeleteResource(req: CdeRequest[DeleteResourceReq]): Either[CdeClusterClientError, Unit] =
    for {
      token         <- authenticate(req.service)
      uri            = s"${req.vc.getVcApiUrl}/resources/${req.spec.resourceName}"
      optResDetails <-
        get[ResourceDetails](uri, token).leftMap(e => GetResourceErr(GetResourceReq(req.spec.resourceName), e))
      response      <- if (optResDetails.isEmpty) Right() else delRes(req.spec, token, uri)
    } yield response

  private def delJob(req: DeleteJobReq, token: BearerToken, deleteApiUri: String) =
    http
      .delete[Unit](deleteApiUri, Map.empty, token)
      .leftMap(e => DeleteJobErr(req, e))

  private def delRes(req: DeleteResourceReq, token: BearerToken, deleteApiUri: String) =
    http
      .delete[Unit](deleteApiUri, Map.empty, token)
      .leftMap(e => DeleteResourceErr(req, e))
}
