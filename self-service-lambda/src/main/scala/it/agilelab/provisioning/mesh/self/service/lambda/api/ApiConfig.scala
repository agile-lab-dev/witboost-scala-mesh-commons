package it.agilelab.provisioning.mesh.self.service.lambda.api

final case class ApiConfig(
  validatePath: String,
  provisionPath: String,
  provisionStatus: String,
  unprovisionPath: String
)

object ApiConfig {

  def default(): ApiConfig =
    ApiConfig(
      validatePath = s"/validate",
      provisionPath = s"/provision",
      provisionStatus = s"/provision/([^\\/]*)/status",
      unprovisionPath = s"/unprovision"
    )

  def withRootPath(rootPath: String): ApiConfig = {
    val prefix = sanitize(rootPath)
    ApiConfig(
      validatePath = s"$prefix/validate",
      provisionPath = s"$prefix/provision",
      provisionStatus = s"$prefix/provision/([^\\/]*)/status",
      unprovisionPath = s"$prefix/unprovision"
    )
  }

  private def sanitize(str: String): String =
    if (str.endsWith("/")) str.substring(0, str.length - 1) else str

}
