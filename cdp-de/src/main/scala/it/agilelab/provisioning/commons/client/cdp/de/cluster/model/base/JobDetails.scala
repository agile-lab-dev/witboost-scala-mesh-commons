package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

/** JobDetails model
  *
  * from cde swagger API doc.
  * @param name job name
  * @param `type` job type
  * @param created timestamp that specific job was created
  * @param modified timestamp that specific job was modified
  * @param lastUsed timestamp that specific job was used last time
  * @param mounts list of resource that need to be mounted
  * @param retentionPolicy retention policy for the specific job
  * @param spark spark job configuration
  * @param schedule schedule configuraiton
  */
final case class JobDetails(
  name: String,
  `type`: String,
  created: String,
  modified: String,
  lastUsed: String,
  mounts: Seq[Mount],
  retentionPolicy: String,
  spark: Option[SparkJob],
  schedule: Option[Schedule]
)
