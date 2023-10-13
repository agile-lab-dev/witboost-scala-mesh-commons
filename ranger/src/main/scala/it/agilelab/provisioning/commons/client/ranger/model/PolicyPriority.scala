package it.agilelab.provisioning.commons.client.ranger.model

object PolicyPriority {
  type PolicyPriority = Int
  val NORMAL: PolicyPriority   = 0
  val OVERRIDE: PolicyPriority = 1
}
