package it.agilelab.provisioning.commons.client.ranger.model

import cats.Show

final case class RangerSecurityZone(
  id: Int,
  name: String,
  services: Map[String, RangerSecurityZoneResources],
  isEnabled: Boolean,
  adminUsers: Seq[String],
  adminUserGroups: Seq[String],
  auditUsers: Seq[String],
  auditUserGroups: Seq[String]
)
