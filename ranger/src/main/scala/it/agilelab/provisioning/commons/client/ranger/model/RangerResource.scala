package it.agilelab.provisioning.commons.client.ranger.model

final case class RangerResource(
  values: Seq[String],
  isExcludes: Boolean,
  isRecursive: Boolean
)
