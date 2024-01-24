package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyResource

import java.util
import scala.jdk.CollectionConverters.{ CollectionHasAsScala, MapHasAsJava, MapHasAsScala, SeqHasAsJava }
import scala.language.implicitConversions

final case class RangerResource(
  values: Seq[String],
  isExcludes: Boolean,
  isRecursive: Boolean
)

object RangerResource {
  implicit def resourceFromRangerModel(resources: util.Map[String, RangerPolicyResource]): Map[String, RangerResource] =
    resources.asScala.map { case (key, resource) =>
      key -> {
        new RangerResource(
          values = resource.getValues.asScala.toSeq,
          isExcludes = resource.getIsExcludes,
          isRecursive = resource.getIsRecursive
        )
      }
    }.toMap

  implicit def resourceToRangerModel(resources: Map[String, RangerResource]): util.Map[String, RangerPolicyResource] =
    resources.map { case (key, resource) =>
      key -> {
        val policyResource = new RangerPolicyResource()
        policyResource.setValues(resource.values.asJava)
        policyResource.setIsExcludes(resource.isExcludes)
        policyResource.setIsRecursive(resource.isRecursive)
        policyResource
      }
    }.asJava

}
