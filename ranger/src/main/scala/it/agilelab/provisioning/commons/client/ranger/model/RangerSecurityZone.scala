package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model

import scala.jdk.CollectionConverters.{ ListHasAsScala, SeqHasAsJava }
import scala.language.implicitConversions

final case class RangerSecurityZone(
  id: Long,
  name: String,
  services: Map[String, RangerSecurityZoneResources],
  isEnabled: Boolean,
  adminUsers: Seq[String],
  adminUserGroups: Seq[String],
  auditUsers: Seq[String],
  auditUserGroups: Seq[String]
)

object RangerSecurityZone {

  implicit def zoneToRangerModel(zone: RangerSecurityZone): model.RangerSecurityZone = {
    val sz = new model.RangerSecurityZone(
      zone.name,
      zone.services,
      List.empty.asJava,
      zone.adminUsers.asJava,
      zone.adminUserGroups.asJava,
      zone.auditUsers.asJava,
      zone.auditUserGroups.asJava,
      ""
    )
    sz.setId(zone.id)
    sz.setIsEnabled(zone.isEnabled)

    sz
  }

  implicit def zoneFromRangerModel(securityZone: model.RangerSecurityZone): RangerSecurityZone =
    new RangerSecurityZone(
      id = securityZone.getId,
      name = securityZone.getName,
      services = securityZone.getServices,
      isEnabled = securityZone.getIsEnabled,
      adminUsers = securityZone.getAdminUsers.asScala.toSeq,
      adminUserGroups = securityZone.getAdminUserGroups.asScala.toSeq,
      auditUsers = securityZone.getAuditUsers.asScala.toSeq,
      auditUserGroups = securityZone.getAuditUserGroups.asScala.toSeq
    )
}
