package it.agilelab.provisioning.commons.client.ranger.model

import it.agilelab.provisioning.commons.client.ranger.model.PolicyPriority.PolicyPriority
import org.apache.ranger.plugin.model

import scala.jdk.CollectionConverters.{ CollectionHasAsScala, MapHasAsJava, MapHasAsScala, SeqHasAsJava }
import scala.language.implicitConversions

final case class RangerPolicy(
  id: Long,
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

  implicit def policyFromRangerModel(policy: model.RangerPolicy): RangerPolicy =
    new RangerPolicy(
      id = policy.getId,
      service = policy.getService,
      name = policy.getName,
      description = policy.getDescription,
      isAuditEnabled = policy.getIsAuditEnabled,
      isEnabled = policy.getIsEnabled,
      resources = policy.getResources,
      policyItems = policy.getPolicyItems,
      serviceType = policy.getServiceType,
      policyLabels = policy.getPolicyLabels.asScala.toSeq,
      isDenyAllElse = policy.getIsDenyAllElse,
      zoneName = policy.getZoneName,
      policyPriority = policy.getPolicyPriority
    )

  implicit def policyToRangerModel(policy: RangerPolicy): model.RangerPolicy = {
    val rangerPolicy = new model.RangerPolicy()
    rangerPolicy.setId(policy.id)
    rangerPolicy.setService(policy.service)
    rangerPolicy.setName(policy.name)
    rangerPolicy.setDescription(policy.description)
    rangerPolicy.setIsAuditEnabled(policy.isAuditEnabled)
    rangerPolicy.setIsEnabled(policy.isEnabled)
    rangerPolicy.setResources(policy.resources)
    rangerPolicy.setPolicyItems(policy.policyItems)
    rangerPolicy.setServiceType(policy.serviceType)
    rangerPolicy.setPolicyLabels(policy.policyLabels.asJava)
    rangerPolicy.setIsDenyAllElse(policy.isDenyAllElse)
    rangerPolicy.setZoneName(policy.zoneName)
    rangerPolicy.setPolicyPriority(policy.policyPriority)

    rangerPolicy
  }

}
