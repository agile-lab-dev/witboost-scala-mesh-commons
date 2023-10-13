package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.response

import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base.Job

final case class ListJobsRes(
  jobs: Seq[Job],
  meta: PaginationMeta
)
