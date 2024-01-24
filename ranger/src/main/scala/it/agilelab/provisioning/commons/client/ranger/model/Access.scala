package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItemAccess
import java.util
import scala.language.implicitConversions
import scala.jdk.CollectionConverters.{ CollectionHasAsScala, SeqHasAsJava }

final case class Access(
  `type`: String,
  isAllowed: Boolean
)

object Access {

  def all: Access = Access("all", isAllowed = true)

  def select: Access = Access("select", isAllowed = true)

  def read: Access = Access("read", isAllowed = true)

  def write: Access = Access("write", isAllowed = true)

  implicit def accessFromRangerModel(itemAccess: util.List[RangerPolicyItemAccess]): Seq[Access] =
    itemAccess.asScala.toSeq.map(itemAccess =>
      new Access(`type` = itemAccess.getType, isAllowed = itemAccess.getIsAllowed)
    )

  implicit def accessToRangerModel(access: Seq[Access]): util.List[RangerPolicyItemAccess] =
    access.map(access => new RangerPolicyItemAccess(access.`type`, access.isAllowed)).asJava

}
