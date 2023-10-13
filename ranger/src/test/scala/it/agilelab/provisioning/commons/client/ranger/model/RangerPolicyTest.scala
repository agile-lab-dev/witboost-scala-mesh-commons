package it.agilelab.provisioning.commons.client.ranger.model

import org.scalatest.funsuite.AnyFunSuite

class RangerPolicyTest extends AnyFunSuite {

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

}
