package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model
import org.scalatest.funsuite.AnyFunSuite

import scala.jdk.CollectionConverters.SeqHasAsJava

class RangerPolicyTest extends AnyFunSuite {

  def assertAreEqual(aPolicy: model.RangerPolicy, otherPolicy: model.RangerPolicy): Unit = {
    assert(aPolicy.getId.equals(otherPolicy.getId))
    assert(aPolicy.getName.equals(otherPolicy.getName))
    assert(aPolicy.getIsEnabled.equals(otherPolicy.getIsEnabled))
    assert(aPolicy.getDescription.equals(otherPolicy.getDescription))
    assert(aPolicy.getOptions.equals(otherPolicy.getOptions))
    assert(aPolicy.getService.equals(otherPolicy.getService))
    assert(aPolicy.getServiceType.equals(otherPolicy.getServiceType))
    assert(aPolicy.getPolicyLabels.equals(otherPolicy.getPolicyLabels))
    assert(aPolicy.getZoneName.equals(otherPolicy.getZoneName))
    assert(aPolicy.getService.equals(otherPolicy.getService))
    assert(aPolicy.getPolicyPriority.equals(otherPolicy.getPolicyPriority))
    assert(aPolicy.getIsAuditEnabled.equals(otherPolicy.getIsAuditEnabled))
    assert(aPolicy.getIsDenyAllElse.equals(otherPolicy.getIsDenyAllElse))
    assert(aPolicy.getPolicyItems.equals(otherPolicy.getPolicyItems))
    assert(aPolicy.getResources.equals(otherPolicy.getResources))
  }

  test("empty") {
    val actual   = RangerPolicy.empty(
      service = "srv",
      name = "nm",
      description = "desc",
      serviceType = "srvT",
      labels = Seq("pl"),
      Some("zzz")
    )
    val expected = RangerPolicy(
      id = -1,
      service = "srv",
      name = "nm",
      description = "desc",
      isAuditEnabled = true,
      isEnabled = true,
      resources = Map.empty,
      policyItems = Seq.empty,
      serviceType = "srvT",
      policyLabels = Seq("pl"),
      isDenyAllElse = true,
      "zzz",
      policyPriority = 0
    )
    assert(actual == expected)
  }

  test("implicit from ranger-intg model") {

    val policy = new model.RangerPolicy()
    policy.setId(10)
    policy.setService("srv")
    policy.setName("nm")
    policy.setDescription("desc")
    policy.setIsAuditEnabled(true)
    policy.setIsEnabled(true)
    policy.setResources(
      Map(
        "table" -> RangerResource(Seq("*"), isExcludes = false, isRecursive = false)
      )
    )
    policy.setPolicyItems(
      Seq(
        RangerPolicyItem(
          Seq("u1", "u2"),
          Seq("r1", "r2"),
          Seq("g1", "g2"),
          Seq.empty,
          false,
          Seq(Access.select, Access.read)
        )
      )
    )
    policy.setServiceType("srvT")
    policy.setPolicyLabels(List("pl").asJava)
    policy.setIsDenyAllElse(true)
    policy.setZoneName("zzz")
    policy.setPolicyPriority(PolicyPriority.NORMAL)

    val expected = RangerPolicy(
      id = 10,
      service = "srv",
      name = "nm",
      description = "desc",
      isAuditEnabled = true,
      isEnabled = true,
      resources = Map(
        "table" -> RangerResource(Seq("*"), isExcludes = false, isRecursive = false)
      ),
      policyItems = Seq(
        RangerPolicyItem(
          Seq("u1", "u2"),
          Seq("r1", "r2"),
          Seq("g1", "g2"),
          Seq.empty,
          false,
          Seq(Access.select, Access.read)
        )
      ),
      serviceType = "srvT",
      policyLabels = Seq("pl"),
      isDenyAllElse = true,
      "zzz",
      policyPriority = PolicyPriority.NORMAL
    )

    val actual = RangerPolicy.policyFromRangerModel(policy)

    assert(actual == expected)

  }

  test("implicit to ranger-intg model") {

    val expected = new model.RangerPolicy()
    expected.setId(10)
    expected.setService("srv")
    expected.setName("nm")
    expected.setDescription("desc")
    expected.setIsAuditEnabled(true)
    expected.setIsEnabled(true)
    expected.setResources(
      Map(
        "table" -> RangerResource(Seq("*"), isExcludes = false, isRecursive = false)
      )
    )
    expected.setPolicyItems(
      Seq(
        RangerPolicyItem(
          Seq("u1", "u2"),
          Seq("r1", "r2"),
          Seq("g1", "g2"),
          Seq.empty,
          delegateAdmin = false,
          Seq(Access.select, Access.read)
        )
      )
    )
    expected.setServiceType("srvT")
    expected.setPolicyLabels(List("pl").asJava)
    expected.setIsDenyAllElse(true)
    expected.setZoneName("zzz")
    expected.setPolicyPriority(PolicyPriority.NORMAL)

    val policy = RangerPolicy(
      id = 10,
      service = "srv",
      name = "nm",
      description = "desc",
      isAuditEnabled = true,
      isEnabled = true,
      resources = Map(
        "table" -> RangerResource(Seq("*"), isExcludes = false, isRecursive = false)
      ),
      policyItems = Seq(
        RangerPolicyItem(
          Seq("u1", "u2"),
          Seq("r1", "r2"),
          Seq("g1", "g2"),
          Seq.empty,
          delegateAdmin = false,
          Seq(Access.select, Access.read)
        )
      ),
      serviceType = "srvT",
      policyLabels = Seq("pl"),
      isDenyAllElse = true,
      "zzz",
      policyPriority = PolicyPriority.NORMAL
    )

    val actual = RangerPolicy.policyToRangerModel(policy)

    assertAreEqual(actual, expected)

  }

}
