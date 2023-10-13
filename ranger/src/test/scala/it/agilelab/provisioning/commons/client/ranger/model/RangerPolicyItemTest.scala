package it.agilelab.provisioning.commons.client.ranger.model

import org.scalatest.funsuite.AnyFunSuite

class RangerPolicyItemTest extends AnyFunSuite {

  test("userLevel") {
    val actual   = RangerPolicyItem.userLevel(Seq("x", "y"), Seq("w", "z"))
    val expected = RangerPolicyItem(
      users = Seq("w", "z"),
      roles = Seq.empty,
      groups = Seq("x", "y"),
      conditions = Seq.empty,
      delegateAdmin = false,
      accesses = Seq(
        Access("SELECT", isAllowed = true),
        Access("READ", isAllowed = true)
      )
    )
    assert(actual == expected)
  }

  test("ownerLevel") {
    val actual   = RangerPolicyItem.ownerLevel(Seq("x", "y"), Seq("w", "z"))
    val expected = RangerPolicyItem(
      users = Seq("w", "z"),
      roles = Seq.empty,
      groups = Seq("x", "y"),
      conditions = Seq.empty,
      delegateAdmin = false,
      accesses = Seq(
        Access("ALL", isAllowed = true)
      )
    )
    assert(actual == expected)
  }
}
