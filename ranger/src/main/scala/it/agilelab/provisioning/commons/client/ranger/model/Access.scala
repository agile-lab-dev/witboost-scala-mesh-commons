package it.agilelab.provisioning.commons.client.ranger.model

final case class Access(
  `type`: String,
  isAllowed: Boolean
)

object Access {

  def all: Access = Access("ALL", isAllowed = true)

  def select: Access = Access("SELECT", isAllowed = true)

  def read: Access = Access("READ", isAllowed = true)

}
