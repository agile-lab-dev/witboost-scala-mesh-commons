package it.agilelab.provisioning.commons.client.ranger.model

final case class RangerPolicyItem(
  users: Seq[String],
  roles: Seq[String],
  groups: Seq[String],
  conditions: Seq[String],
  delegateAdmin: Boolean,
  accesses: Seq[Access]
)

object RangerPolicyItem {

  def ownerLevel(groups: Seq[String], users: Seq[String]): RangerPolicyItem =
    RangerPolicyItem(
      roles = Seq.empty[String],
      groups = groups,
      users = users,
      conditions = Seq.empty[String],
      delegateAdmin = false,
      accesses = Seq(
        Access.all
      )
    )

  def userLevel(groups: Seq[String], users: Seq[String]): RangerPolicyItem =
    RangerPolicyItem(
      roles = Seq.empty[String],
      groups = groups,
      users = users,
      conditions = Seq.empty[String],
      delegateAdmin = false,
      accesses = Seq(
        Access.select,
        Access.read
      )
    )

}
