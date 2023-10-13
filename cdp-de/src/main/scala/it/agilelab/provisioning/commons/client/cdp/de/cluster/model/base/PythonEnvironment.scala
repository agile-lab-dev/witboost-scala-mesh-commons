package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

/** Describe a Python Environment
  * @param pythonVersion python version
  * @param pyPiMirror optional pypi mirror
  */
final case class PythonEnvironment(pythonVersion: String, pyPiMirror: Option[String])
