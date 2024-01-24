package it.agilelab.provisioning.commons.client.ranger.model

import org.scalatest.funsuite.AnyFunSuite
import org.apache.ranger.plugin.model

import java.util
import scala.jdk.CollectionConverters.{ MapHasAsJava, SeqHasAsJava }

class RangerSecurityZoneTest extends AnyFunSuite {

  def assertAreEqual(aZone: model.RangerSecurityZone, otherZone: model.RangerSecurityZone): Unit = {
    assert(aZone.getId.equals(otherZone.getId))
    assert(aZone.getName.equals(otherZone.getName))
    assert(aZone.getIsEnabled.equals(otherZone.getIsEnabled))
    assert(aZone.getDescription.equals(otherZone.getDescription))
    assert(aZone.getAdminUserGroups.equals(otherZone.getAdminUserGroups))
    assert(aZone.getAdminUsers.equals(otherZone.getAdminUsers))
    assert(aZone.getAuditUsers.equals(otherZone.getAuditUsers))
    assert(aZone.getAuditUserGroups.equals(otherZone.getAuditUserGroups))
    assert(aZone.getTagServices.equals(otherZone.getTagServices))
    aZone.getServices.keySet().equals(otherZone.getServices.keySet())
    aZone.getServices.forEach((e, srv) => srv.getResources.equals(otherZone.getServices.get(e).getResources))
  }

  test("implicit to ranger-intg model") {
    val r        = Seq(
      new util.HashMap[String, util.List[String]](
        Map(
          "database" -> util.Arrays.asList("domain_*"),
          "column"   -> util.Arrays.asList("*"),
          "table"    -> util.Arrays.asList("*")
        ).asJava
      )
    ).asJava
    val res      = new model.RangerSecurityZone.RangerSecurityZoneService(r)
    val expected = new model.RangerSecurityZone(
      "name",
      util.Map.of("service_name", res),
      new util.ArrayList[String](),
      List("u1", "u2").asJava,
      List("g1", "g2").asJava,
      List("u1", "u2").asJava,
      List("g1", "g2").asJava,
      ""
    )
    expected.setId(10)
    expected.setIsEnabled(true)

    val actual = RangerSecurityZone.zoneToRangerModel(
      RangerSecurityZone(
        id = 10,
        name = "name",
        services = Map(
          "service_name" -> RangerSecurityZoneResources(
            List(
              Map("database" -> List("domain_*"), "column" -> List("*"), "table" -> List("*"))
            )
          )
        ),
        isEnabled = true,
        adminUsers = List("u1", "u2"),
        adminUserGroups = List("g1", "g2"),
        auditUsers = List("u1", "u2"),
        auditUserGroups = List("g1", "g2")
      )
    )

    assertAreEqual(expected, actual)

  }
  test("implicit from ranger-intg model") {
    val r        = Seq(
      new util.HashMap[String, util.List[String]](
        Map(
          "database" -> util.Arrays.asList("domain_*"),
          "column"   -> util.Arrays.asList("*"),
          "table"    -> util.Arrays.asList("*")
        ).asJava
      )
    ).asJava
    val res      = new model.RangerSecurityZone.RangerSecurityZoneService(r)
    val expected =
      RangerSecurityZone(
        id = 10,
        name = "name",
        services = Map(
          "service_name" -> RangerSecurityZoneResources(
            List(
              Map("database" -> List("domain_*"), "column" -> List("*"), "table" -> List("*"))
            )
          )
        ),
        isEnabled = true,
        adminUsers = List("u1", "u2"),
        adminUserGroups = List("g1", "g2"),
        auditUsers = List("u1", "u2"),
        auditUserGroups = List("g1", "g2")
      )

    val zone = new model.RangerSecurityZone(
      "name",
      util.Map.of("service_name", res),
      new util.ArrayList[String](),
      List("u1", "u2").asJava,
      List("g1", "g2").asJava,
      List("u1", "u2").asJava,
      List("g1", "g2").asJava,
      ""
    )
    zone.setId(10)
    zone.setIsEnabled(true)

    val actual = RangerSecurityZone.zoneFromRangerModel(zone)

    assertAreEqual(expected, actual)

  }

}
