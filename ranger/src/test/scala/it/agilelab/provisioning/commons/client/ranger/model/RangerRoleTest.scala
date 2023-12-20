package it.agilelab.provisioning.commons.client.ranger.model

import org.scalatest.funsuite.AnyFunSuite

class RangerRoleTest extends AnyFunSuite {
  test("empty") {
    val actual   = RangerRole.empty(name = "Name", description = "Description")
    val expected =
      RangerRole(
        id = -1,
        isEnabled = true,
        name = "Name",
        description = "Description",
        groups = Seq.empty,
        users = Seq.empty,
        roles = Seq.empty
      )
    assert(actual == expected)
  }
}
