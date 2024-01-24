package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model
import org.scalatest.funsuite.AnyFunSuite

import scala.jdk.CollectionConverters.{ MapHasAsJava, SeqHasAsJava }

class RangerRoleTest extends AnyFunSuite {

  def assertAreEqual(aRole: model.RangerRole, otherRole: model.RangerRole): Unit = {
    assert(aRole.getId.equals(otherRole.getId))
    assert(aRole.getName.equals(otherRole.getName))
    assert(aRole.getIsEnabled.equals(otherRole.getIsEnabled))
    assert(aRole.getDescription.equals(otherRole.getDescription))
    assert(aRole.getUsers.equals(otherRole.getUsers))
    assert(aRole.getGroups.equals(otherRole.getGroups))
    assert(aRole.getRoles.equals(otherRole.getRoles))
    assert(aRole.getOptions.equals(otherRole.getOptions))
  }

  test("empty") {
    val actual   = RangerRole.empty(name = "Name", description = "Description")
    val expected =
      RangerRole(
        id = 0,
        isEnabled = true,
        name = "Name",
        description = "Description",
        groups = Seq.empty,
        users = Seq.empty,
        roles = Seq.empty
      )
    assert(actual == expected)
  }

  test("role member implicit to ranger-intg model") {
    val expected =
      List(new model.RangerRole.RoleMember("name", false), new model.RangerRole.RoleMember("admin", true)).asJava
    val actual   =
      RoleMember.roleMemberToRangerModel(List(RoleMember("name", isAdmin = false), RoleMember("admin", isAdmin = true)))

    assert(actual == expected)
  }

  test("role member implicit from ranger-intg model") {
    val expected =
      List(RoleMember("name", isAdmin = false), RoleMember("admin", isAdmin = true))
    val actual   =
      RoleMember.roleMemberFromRangerModel(
        List(new model.RangerRole.RoleMember("name", false), new model.RangerRole.RoleMember("admin", true)).asJava
      )

    assert(actual == expected)
  }

  test("role implicit to ranger-intg model") {
    val expected = new model.RangerRole(
      "name",
      "description",
      Map.empty[String, AnyRef].asJava,
      List(RoleMember("u1", isAdmin = false), RoleMember("u2", isAdmin = true)),
      List(RoleMember("g1", isAdmin = false), RoleMember("g2", isAdmin = true)),
      List(RoleMember("r1", isAdmin = false), RoleMember("r2", isAdmin = true))
    )
    expected.setId(10)
    expected.setIsEnabled(true)

    val actual = RangerRole.roleToRangerModel(
      RangerRole(
        10,
        isEnabled = true,
        "name",
        "description",
        List(RoleMember("g1", isAdmin = false), RoleMember("g2", isAdmin = true)),
        List(RoleMember("u1", isAdmin = false), RoleMember("u2", isAdmin = true)),
        List(RoleMember("r1", isAdmin = false), RoleMember("r2", isAdmin = true))
      )
    )

    assertAreEqual(actual, expected)
  }

  test("role implicit from ranger-intg model") {
    val role = new model.RangerRole(
      "name",
      "description",
      Map.empty[String, AnyRef].asJava,
      List(RoleMember("u1", isAdmin = false), RoleMember("u2", isAdmin = true)),
      List(RoleMember("g1", isAdmin = false), RoleMember("g2", isAdmin = true)),
      List(RoleMember("r1", isAdmin = false), RoleMember("r2", isAdmin = true))
    )
    role.setId(10)
    role.setIsEnabled(true)

    val expected = RangerRole(
      10,
      isEnabled = true,
      "name",
      "description",
      List(RoleMember("g1", isAdmin = false), RoleMember("g2", isAdmin = true)),
      List(RoleMember("u1", isAdmin = false), RoleMember("u2", isAdmin = true)),
      List(RoleMember("r1", isAdmin = false), RoleMember("r2", isAdmin = true))
    )

    val actual = RangerRole.roleFromRangerModel(role)

    assert(actual == expected)
  }

}
