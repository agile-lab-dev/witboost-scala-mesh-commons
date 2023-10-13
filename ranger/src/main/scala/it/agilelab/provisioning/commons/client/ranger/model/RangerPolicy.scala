package it.agilelab.provisioning.commons.client.ranger.model

import cats.Show
import it.agilelab.provisioning.commons.client.ranger.model.PolicyPriority.PolicyPriority

final case class RangerPolicy(
  id: Int,
  service: String,
  name: String,
  description: String,
  isAuditEnabled: Boolean,
  isEnabled: Boolean,
  resources: Map[String, RangerResource],
  policyItems: Seq[RangerPolicyItem],
  serviceType: String,
  policyLabels: Seq[String],
  isDenyAllElse: Boolean,
  zoneName: String,
  policyPriority: PolicyPriority
)

object RangerPolicy {

  def empty(
    service: String,
    name: String,
    description: String,
    serviceType: String,
    labels: Seq[String],
    zoneName: Option[String]
  ): RangerPolicy =
    RangerPolicy(
      id = -1,
      service = service,
      name = name,
      description = description,
      isAuditEnabled = true,
      isEnabled = true,
      resources = Map.empty,
      policyItems = Seq.empty,
      serviceType = serviceType,
      policyLabels = labels,
      isDenyAllElse = true,
      zoneName = zoneName.getOrElse(""),
      policyPriority = PolicyPriority.NORMAL
    )

}
