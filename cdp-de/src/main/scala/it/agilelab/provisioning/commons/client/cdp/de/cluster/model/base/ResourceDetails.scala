package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

/** ResourceDetails
  *
  * from cde swagger API doc.
  *
  * @param name: Resource file
  * @param `type`: Resource type
  * @param signature: Resource signature
  * @param created: timestamp of Resource creation
  * @param modified: timestamp of last Resource modification
  * @param lastUsed: timestamp of last Resource usage
  * @param retentionPolicy: Resource retention policy
  * @param status: Resource status
  */
final case class ResourceDetails(
  name: String,
  `type`: String,
  signature: Option[String],
  created: String,
  modified: String,
  lastUsed: String,
  retentionPolicy: String,
  status: String
)
