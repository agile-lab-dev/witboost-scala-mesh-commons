package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model
import org.apache.ranger.plugin.model.RangerPolicy
import org.scalatest.funsuite.AnyFunSuite

import java.util
import scala.jdk.CollectionConverters.SeqHasAsJava

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

  test("implicit to ranger-intg model") {
    val expected = List(
      new RangerPolicy.RangerPolicyItem(
        List(new RangerPolicy.RangerPolicyItemAccess("all", true)).asJava,
        List("w", "z").asJava,
        List("x", "y").asJava,
        List("r1", "r2").asJava,
        new util.ArrayList[RangerPolicy.RangerPolicyItemCondition](),
        false
      )
    ).asJava

    val actual = RangerPolicyItem.policyItemToRangerModel(
      List(
        RangerPolicyItem(
          users = Seq("w", "z"),
          roles = Seq("r1", "r2"),
          groups = Seq("x", "y"),
          conditions = Seq.empty,
          delegateAdmin = false,
          accesses = Seq(
            Access("all", isAllowed = true)
          )
        )
      )
    )

    assert(actual == expected)
  }

  test("implicit from ranger-intg model") {
    val expected = List(
      RangerPolicyItem(
        users = Seq("w", "z"),
        roles = Seq("r1", "r2"),
        groups = Seq("x", "y"),
        conditions = Seq.empty,
        delegateAdmin = false,
        accesses = Seq(
          Access("all", isAllowed = true)
        )
      )
    )

    val actual = RangerPolicyItem.policyItemFromRangerModel(
      List(
        new RangerPolicy.RangerPolicyItem(
          List(new RangerPolicy.RangerPolicyItemAccess("all", true)).asJava,
          List("w", "z").asJava,
          List("x", "y").asJava,
          List("r1", "r2").asJava,
          new util.ArrayList[RangerPolicy.RangerPolicyItemCondition](),
          false
        )
      ).asJava
    )

    assert(actual == expected)
  }
}
