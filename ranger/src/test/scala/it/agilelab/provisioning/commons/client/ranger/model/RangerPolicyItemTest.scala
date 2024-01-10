package it.agilelab.provisioning.commons.client.ranger.model

import org.scalatest.funsuite.AnyFunSuite

class RangerPolicyItemTest extends AnyFunSuite {

  test("userLevel") {
    val actual   = RangerPolicyItem.userLevel(Seq("x", "y"), Seq("w", "z"), Seq("r1", "r2"))
    val expected = RangerPolicyItem(
      users = Seq("w", "z"),
      roles = Seq("r1", "r2"),
      groups = Seq("x", "y"),
      conditions = Seq.empty,
      delegateAdmin = false,
      accesses = Seq(
        Access("select", isAllowed = true),
        Access("read", isAllowed = true)
      )
    )
    assert(actual == expected)
  }

  test("ownerLevel") {
    val actual   = RangerPolicyItem.ownerLevel(Seq("x", "y"), Seq("w", "z"), Seq("r1", "r2"))
    val expected = RangerPolicyItem(
      users = Seq("w", "z"),
      roles = Seq("r1", "r2"),
      groups = Seq("x", "y"),
      conditions = Seq.empty,
      delegateAdmin = false,
      accesses = Seq(
        Access("all", isAllowed = true)
      )
    )
    assert(actual == expected)
  }
}
