package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.request

import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base.PythonEnvironment

final case class CreateResourceReq(
  name: String,
  `type`: String,
  retentionPolicy: String,
  pythonEnvironment: Option[PythonEnvironment]
)
