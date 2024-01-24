package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model.RangerSecurityZone.RangerSecurityZoneService

import java.util
import scala.jdk.CollectionConverters.{ CollectionHasAsScala, MapHasAsJava, MapHasAsScala, SeqHasAsJava }
import scala.language.implicitConversions

final case class RangerSecurityZoneResources(
  resources: Seq[Map[String, Seq[String]]]
)

object RangerSecurityZoneResources {
  implicit def zoneServiceFromRangerModel(
    resources: util.Map[String, RangerSecurityZoneService]
  ): Map[String, RangerSecurityZoneResources] =
    resources.asScala.map { case (key, resources) =>
      key -> {
        new RangerSecurityZoneResources(resources.getResources.asScala.toSeq.map(_.asScala.map {
          case (key, resources) =>
            key -> resources.asScala.toSeq
        }.toMap))
      }
    }.toMap

  implicit def zoneServiceToRangerModel(
    zoneResources: Map[String, RangerSecurityZoneResources]
  ): util.Map[String, RangerSecurityZoneService] =
    zoneResources.map { case (key, resource) =>
      key -> {
        val res = resource.resources
          .map(m =>
            new util.HashMap(m.map { case (key, resources) =>
              key -> resources.asJava
            }.asJava)
          )
          .asJava
        new RangerSecurityZoneService(
          res
        )
      }
    }.asJava

}
