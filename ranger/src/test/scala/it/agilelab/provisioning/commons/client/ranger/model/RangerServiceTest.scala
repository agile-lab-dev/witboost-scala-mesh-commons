package it.agilelab.provisioning.commons.client.ranger.model

import org.scalatest.funsuite.AnyFunSuite
import org.apache.ranger.plugin.model

import scala.jdk.CollectionConverters.{ CollectionHasAsScala, MapHasAsJava, SeqHasAsJava }
class RangerServiceTest extends AnyFunSuite {

  def assertAreEqual(aService: model.RangerService, otherService: model.RangerService): Unit = {
    assert(aService.getId.equals(otherService.getId))
    assert(aService.getIsEnabled.equals(otherService.getIsEnabled))
    assert(aService.getDisplayName.equals(otherService.getDisplayName))
    assert(aService.getType.equals(otherService.getType))
    assert(aService.getDisplayName.equals(otherService.getDisplayName))
    assert(aService.getConfigs.equals(otherService.getConfigs))
  }

  test("implicit to ranger-intg model") {
    val expected = new model.RangerService("srvType", "srvName", "", "", Map("config" -> "config1").asJava)
    expected.setId(10)
    expected.setIsEnabled(true)
    expected.setDisplayName("serviceName")

    val actual = RangerService.serviceToRangerModel(
      RangerService(
        id = 10,
        isEnabled = true,
        `type` = "srvType",
        name = "srvName",
        displayName = "serviceName",
        configs = Map("config" -> "config1")
      )
    )

    assertAreEqual(expected, actual)
  }

  test("implicit from ranger-intg model") {
    val expected =
      RangerService(
        id = 10,
        isEnabled = true,
        `type` = "srvType",
        name = "srvName",
        displayName = "serviceName",
        configs = Map("config" -> "config1")
      )

    val service = new model.RangerService("srvType", "srvName", "", "", Map("config" -> "config1").asJava)
    service.setId(10)
    service.setIsEnabled(true)
    service.setDisplayName("serviceName")

    val actual = RangerService.serviceFromRangerModel(service)

    assert(expected == actual)

  }

  test("implicit list to ranger-intg model") {
    val service = new model.RangerService("srvType", "srvName", "", "", Map("config" -> "config1").asJava)
    service.setId(10)
    service.setIsEnabled(true)
    service.setDisplayName("serviceName")

    val expected = List(service).asJava

    val actual = RangerService.servicesToRangerModel(
      List(
        RangerService(
          id = 10,
          isEnabled = true,
          `type` = "srvType",
          name = "srvName",
          displayName = "serviceName",
          configs = Map("config" -> "config1")
        )
      )
    )
    assert(expected.size() == actual.size())
    expected.asScala.zip(actual.asScala).foreach { case (exp, actu) => assertAreEqual(exp, actu) }
  }

  test("implicit list from ranger-intg model") {
    val expected = List(
      RangerService(
        id = 10,
        isEnabled = true,
        `type` = "srvType",
        name = "srvName",
        displayName = "serviceName",
        configs = Map("config" -> "config1")
      )
    )
    val service  = new model.RangerService("srvType", "srvName", "", "", Map("config" -> "config1").asJava)
    service.setId(10)
    service.setIsEnabled(true)
    service.setDisplayName("serviceName")

    val services = List(service).asJava

    val actual = RangerService.servicesFromRangerModel(services)

    assert(expected == actual)
  }

}
