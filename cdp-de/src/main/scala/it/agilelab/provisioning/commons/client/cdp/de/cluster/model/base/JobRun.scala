package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

final case class JobRun(
  id: Int,
  job: String,
  `type`: String,
  status: String,
  spark: Option[SparkJobRun],
  airflow: Option[AirflowJobRun],
  user: String,
  started: String,
  ended: String,
  identity: Option[Identity]
)
