package it.agilelab.provisioning.commons.client.ranger.model

final case class RoleMember(name: String, isAdmin: Boolean)

final case class RangerRole(
  id: Int,
  isEnabled: Boolean,
  name: String,
  description: String,
  groups: Seq[RoleMember],
  users: Seq[RoleMember],
  roles: Seq[RoleMember]
)

object RangerRole {
  def empty(name: String, description: String): RangerRole =
    new RangerRole(
      id = 0,
      isEnabled = true,
      name = name,
      description = description,
      groups = Seq.empty,
      users = Seq.empty,
      roles = Seq.empty
    )
}
