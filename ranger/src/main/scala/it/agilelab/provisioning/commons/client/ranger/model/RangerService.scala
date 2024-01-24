package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model
import scala.jdk.CollectionConverters.{ CollectionHasAsScala, MapHasAsJava, MapHasAsScala, SeqHasAsJava }
import scala.language.implicitConversions
import java.util

final case class RangerService(
  id: Long,
  isEnabled: Boolean,
  `type`: String,
  name: String,
  displayName: String,
  configs: Map[String, String]
)

object RangerService {
  implicit def serviceFromRangerModel(service: model.RangerService): RangerService =
    new RangerService(
      service.getId,
      service.getIsEnabled,
      service.getType,
      service.getName,
      service.getDisplayName,
      service.getConfigs.asScala.toMap
    )

  implicit def servicesFromRangerModel(services: util.List[model.RangerService]): Seq[RangerService] =
    services.asScala.toSeq.map(serviceFromRangerModel)

  implicit def serviceToRangerModel(service: RangerService): model.RangerService = {
    val srv = new model.RangerService(service.`type`, service.name, "", "", service.configs.asJava)

    srv.setId(service.id)
    srv.setIsEnabled(service.isEnabled)
    srv.setDisplayName(service.displayName)

    srv
  }

  implicit def servicesToRangerModel(services: Seq[RangerService]): util.List[model.RangerService] =
    services.map(serviceToRangerModel).asJava

}
