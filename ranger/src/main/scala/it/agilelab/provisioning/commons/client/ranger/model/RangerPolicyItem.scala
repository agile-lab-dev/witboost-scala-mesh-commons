package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model.RangerPolicy
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItemCondition

import scala.jdk.CollectionConverters.{ CollectionHasAsScala, SeqHasAsJava }
import java.util
import scala.language.implicitConversions

final case class RangerPolicyItem(
  users: Seq[String],
  roles: Seq[String],
  groups: Seq[String],
  conditions: Seq[String],
  delegateAdmin: Boolean,
  accesses: Seq[Access]
)

object RangerPolicyItem {

  implicit def policyItemFromRangerModel(policyItems: util.List[RangerPolicy.RangerPolicyItem]): Seq[RangerPolicyItem] =
    policyItems.asScala.toSeq.map { item =>
      new RangerPolicyItem(
        users = item.getUsers.asScala.toSeq,
        roles = item.getRoles.asScala.toSeq,
        groups = item.getGroups.asScala.toSeq,
        conditions = Seq.empty[String],
        delegateAdmin = item.getDelegateAdmin,
        accesses = item.getAccesses
      )
    }

  implicit def policyItemToRangerModel(items: Seq[RangerPolicyItem]): util.List[RangerPolicy.RangerPolicyItem] =
    items.map { item =>
      new RangerPolicy.RangerPolicyItem(
        item.accesses,
        item.users.asJava,
        item.groups.asJava,
        item.roles.asJava,
        item.conditions.map(new RangerPolicyItemCondition(_, List().asJava)).asJava,
        item.delegateAdmin
      )
    }.asJava

  def ownerLevel(groups: Seq[String], users: Seq[String], roles: Seq[String]): RangerPolicyItem =
    RangerPolicyItem(
      roles = roles,
      groups = groups,
      users = users,
      conditions = Seq.empty[String],
      delegateAdmin = false,
      accesses = Seq(
        Access.all
      )
    )

  def userLevel(groups: Seq[String], users: Seq[String], roles: Seq[String]): RangerPolicyItem =
    RangerPolicyItem(
      roles = roles,
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
