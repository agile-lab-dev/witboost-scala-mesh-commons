package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.response

import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base.JobDetails

final case class GetJobRes(jobDetails: Option[JobDetails])
