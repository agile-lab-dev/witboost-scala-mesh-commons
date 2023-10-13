package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.request

final case class ListJobRunsReq(
  filter: Option[Seq[String]],
  offset: Option[Int],
  limit: Option[Int]
)
