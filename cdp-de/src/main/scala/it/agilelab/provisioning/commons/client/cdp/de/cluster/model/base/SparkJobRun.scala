package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

final case class SparkJobRun(
  spec: SparkJob,
  sparkAppID: String,
  sparkAppURL: String
)
