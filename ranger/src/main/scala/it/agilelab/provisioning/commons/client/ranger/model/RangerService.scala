package it.agilelab.provisioning.commons.client.ranger.model

final case class RangerService(
  id: Int,
  isEnabled: Boolean,
  `type`: String,
  name: String,
  displayName: String,
  configs: Map[String, String]
)
