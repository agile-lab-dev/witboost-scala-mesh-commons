package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.constant.RetentionPolicy.KEEP_INDEFINITELY

sealed trait Resource

/** Resource companion object
  *
  * contains some utilities method to create specific Resource type
  */
object Resource {

  private val FILES                            = "files"
  private val PYTHON_ENV_RESOURCE_TYPE: String = "python-env"

  /** Resource model
    *
    * from cde swagger API doc.
    *
    * @param name: Resource name
    * @param `type`: Resource type
    * @param retentionPolicy: Resource retention policy
    */
  final case class FilesResource(
    name: String,
    `type`: String,
    retentionPolicy: String
  ) extends Resource

  /** Python Environment Resource model
    *
    * from cde swagger API doc.
    *
    * @param name: Resource name
    * @param `type`: Resource type
    * @param retentionPolicy: Resource retention policy
    * @param pythonEnvironment: python environment
    */
  final case class PythonEnvironmentResource(
    name: String,
    `type`: String,
    retentionPolicy: String,
    pythonEnvironment: PythonEnvironment
  ) extends Resource

  /** Create a Resource instance that define a files resource
    * @param name: resource name
    * @return a Resource instance
    */
  def filesResource(name: String): FilesResource =
    FilesResource(name, FILES, KEEP_INDEFINITELY)

  /** Create a Resource instance that define a python environment
    * @param name: resource name
    * @param pythonVersion: python version
    * @param pyPiMirror: optional pypi mirror
    * @return a Python environment resource
    */
  def environmentResource(name: String, pythonVersion: String, pyPiMirror: Option[String]): PythonEnvironmentResource =
    PythonEnvironmentResource(
      name,
      PYTHON_ENV_RESOURCE_TYPE,
      KEEP_INDEFINITELY,
      PythonEnvironment(pythonVersion, pyPiMirror)
    )
}
