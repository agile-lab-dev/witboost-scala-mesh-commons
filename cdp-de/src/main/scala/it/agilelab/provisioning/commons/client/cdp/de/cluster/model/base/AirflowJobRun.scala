package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

final case class AirflowJobRun(
  dagID: String,
  dagFile: String,
  dagRunID: String,
  executionDate: String,
  startDate: String
)
