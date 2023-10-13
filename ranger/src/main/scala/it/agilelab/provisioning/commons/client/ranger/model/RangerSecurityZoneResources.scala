package it.agilelab.provisioning.commons.client.ranger.model

final case class RangerSecurityZoneResources(
  resources: Seq[Map[String, Seq[String]]]
)
