package it.agilelab.provisioning.commons.client.cdp.iam.model

final case class MachineUserCredential(
  name: String,
  workloadUsername: String,
  workloadPassword: String
)
