package it.agilelab.provisioning.commons.client.ranger.model

final case class Access(
  `type`: String,
  isAllowed: Boolean
)

object Access {

  def all: Access = Access("all", isAllowed = true)

  def select: Access = Access("select", isAllowed = true)

  def read: Access = Access("read", isAllowed = true)

  def write: Access = Access("write", isAllowed = true)

}
