package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model

import scala.jdk.CollectionConverters.{ CollectionHasAsScala, MapHasAsJava, SeqHasAsJava }
import scala.language.implicitConversions
import java.util

final case class RoleMember(name: String, isAdmin: Boolean)

object RoleMember {

  implicit def roleMemberToRangerModel(members: Seq[RoleMember]): util.List[model.RangerRole.RoleMember] =
    members.map(member => new model.RangerRole.RoleMember(member.name, member.isAdmin)).asJava

  implicit def roleMemberFromRangerModel(roleMembers: util.List[model.RangerRole.RoleMember]): Seq[RoleMember] =
    roleMembers.asScala.toSeq.map(roleMember =>
      new RoleMember(
        roleMember.getName,
        roleMember.getIsAdmin
      )
    )
}

final case class RangerRole(
  id: Long,
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

  implicit def roleToRangerModel(rangerRole: RangerRole): model.RangerRole = {
    val role = new model.RangerRole(
      rangerRole.name,
      rangerRole.description,
      Map.empty[String, AnyRef].asJava,
      rangerRole.users,
      rangerRole.groups,
      rangerRole.roles
    )
    role.setId(rangerRole.id)
    role.setIsEnabled(rangerRole.isEnabled)

    role
  }

  implicit def roleFromRangerModel(rangerRole: model.RangerRole): RangerRole =
    new RangerRole(
      id = rangerRole.getId,
      isEnabled = rangerRole.getIsEnabled,
      name = rangerRole.getName,
      description = rangerRole.getDescription,
      groups = rangerRole.getGroups,
      users = rangerRole.getUsers,
      roles = rangerRole.getRoles
    )
}
