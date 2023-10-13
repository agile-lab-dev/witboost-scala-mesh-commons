package it.agilelab.provisioning.commons.client.cdp.de.cluster

import cats.Show
import cats.implicits._
import it.agilelab.provisioning.commons.client.cdp.de.cluster.auth.provider.TokenProviderError
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.request._
import it.agilelab.provisioning.commons.http.HttpErrors
import it.agilelab.provisioning.commons.showable.ShowableOps

sealed trait CdeClusterClientError extends Exception with Product with Serializable

object CdeClusterClientError {
  final case class CdeClusterClientInitErr(error: Throwable)                    extends CdeClusterClientError
  final case class GetResourceErr(req: GetResourceReq, error: HttpErrors)       extends CdeClusterClientError
  final case class GetJobErr(req: GetJobReq, error: HttpErrors)                 extends CdeClusterClientError
  final case class CreateResourceErr(req: CreateResourceReq, error: HttpErrors) extends CdeClusterClientError
  final case class ResourceAlreadyExistsErr(req: CreateResourceReq)             extends CdeClusterClientError
  final case class UploadFileErr(req: UploadFileReq, error: HttpErrors)         extends CdeClusterClientError
  final case class CreateJobErr(req: CreateJobReq, error: HttpErrors)           extends CdeClusterClientError
  final case class UpdateJobErr(req: UpdateJobReq, error: HttpErrors)           extends CdeClusterClientError
  final case class ListJobsErr(req: ListJobsReq, error: HttpErrors)             extends CdeClusterClientError
  final case class ListJobRunsErr(req: ListJobRunsReq, error: HttpErrors)       extends CdeClusterClientError
  final case class AuthErr(error: TokenProviderError)                           extends CdeClusterClientError
  final case class DeleteJobErr(req: DeleteJobReq, error: HttpErrors)           extends CdeClusterClientError
  final case class DeleteResourceErr(req: DeleteResourceReq, error: HttpErrors) extends CdeClusterClientError

  implicit val showCdeClientError: Show[CdeClusterClientError] = Show.show {
    case e: CdeClusterClientInitErr  => show"CdeClusterClientInitErr(${ShowableOps.showThrowableError.show(e.error)})"
    case e: GetResourceErr           => show"GetResourceErr(${e.req.toString},${e.error})"
    case e: GetJobErr                => show"GetJobErr(${e.req.toString},${e.error})"
    case e: CreateResourceErr        => show"CreateResourceErr(${e.req.toString},${e.error})"
    case e: ResourceAlreadyExistsErr => show"ResourceAlreadyExistsErr(${e.req.toString})"
    case e: UploadFileErr            => show"UploadFileErr(${e.req.toString},${e.error})"
    case e: CreateJobErr             => show"CreateJobErr(${e.req.toString},${e.error})"
    case e: UpdateJobErr             => show"UpdateJobErr(${e.req.toString},${e.error})"
    case e: ListJobsErr              => show"ListJobsErr(${e.req.toString},${e.error})"
    case e: ListJobRunsErr           => show"ListJobRunsErr(${e.req.toString},${e.error})"
    case e: AuthErr                  => show"AuthErr(${e.error})"
    case e: DeleteJobErr             => show"DeleteJobErr(${e.req.toString},${e.error})"
    case e: DeleteResourceErr        => show"DeleteResourceErr(${e.req.toString},${e.error})"
  }
}
