package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.response

import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base.JobRun

final case class ListJobRunsRes(
  runs: Seq[JobRun],
  meta: PaginationMeta
)
