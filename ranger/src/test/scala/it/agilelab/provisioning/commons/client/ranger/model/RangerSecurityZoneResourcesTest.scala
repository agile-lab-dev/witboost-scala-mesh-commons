package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model.RangerSecurityZone.RangerSecurityZoneService
import org.scalatest.funsuite.AnyFunSuite
import java.util

import scala.jdk.CollectionConverters.{ MapHasAsJava, SeqHasAsJava }

class RangerSecurityZoneResourcesTest extends AnyFunSuite {

  def assertAreEqual(aService: RangerSecurityZoneService, otherService: RangerSecurityZoneService): Unit =
    assert(aService.getResources.equals(otherService.getResources))

  test("implicit to ranger-intg model") {
    val resources = Seq(
      Map(
        "database" -> Seq("domain_*"),
        "column"   -> Seq("*"),
        "table"    -> Seq("*")
      )
    )

    val res = resources
      .map(m =>
        new util.HashMap(m.map { case (key, resources) =>
          key -> resources.asJava
        }.asJava)
      )
      .asJava

    val expected = Map(
      "service_name" -> new RangerSecurityZoneService(res)
    ).asJava

    val actual = RangerSecurityZoneResources.zoneServiceToRangerModel(
      Map(
        "service_name" -> RangerSecurityZoneResources(
          Seq(
            Map(
              "database" -> Seq("domain_*"),
              "column"   -> Seq("*"),
              "table"    -> Seq("*")
            )
          )
        )
      )
    )
    expected.forEach((e, srv) => assertAreEqual(srv, actual.get(e)))
  }

  test("implicit from ranger-intg model") {
    val resources = Seq(
      Map(
        "database" -> Seq("domain_*"),
        "column"   -> Seq("*"),
        "table"    -> Seq("*")
      )
    )

    val res = resources
      .map(m =>
        new util.HashMap(m.map { case (key, resources) =>
          key -> resources.asJava
        }.asJava)
      )
      .asJava

    val expected =
      Map(
        "service_name" -> RangerSecurityZoneResources(
          Seq(
            Map(
              "database" -> Seq("domain_*"),
              "column"   -> Seq("*"),
              "table"    -> Seq("*")
            )
          )
        )
      )

    val actual = RangerSecurityZoneResources.zoneServiceFromRangerModel(
      Map(
        "service_name" -> new RangerSecurityZoneService(res)
      ).asJava
    )

    assert(expected == actual)

  }
}
