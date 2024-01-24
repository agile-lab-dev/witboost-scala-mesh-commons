package it.agilelab.provisioning.commons.client.ranger.model

import org.apache.ranger.plugin.model
import org.scalatest.funsuite.AnyFunSuite
import scala.jdk.CollectionConverters.{ CollectionHasAsScala, SeqHasAsJava }
import java.util

class AccessTest extends AnyFunSuite {

  test("all") {
    assert(Access.all == Access("all", isAllowed = true))
  }

  test("read") {
    assert(Access.read == Access("read", isAllowed = true))
  }

  test("select") {
    assert(Access.select == Access("select", isAllowed = true))
  }

  test("write") {
    assert(Access.write == Access("write", isAllowed = true))
  }

  test("implicit to ranger-intg model") {
    val expected = List(new model.RangerPolicy.RangerPolicyItemAccess("all", true)).asJava
    val actual   = Access.accessToRangerModel(List(Access("all", isAllowed = true)))

    assert(actual == expected)
  }

  test("implicit from ranger-intg model") {
    val expected = List(Access("all", isAllowed = true))
    val actual   = Access.accessFromRangerModel(List(new model.RangerPolicy.RangerPolicyItemAccess("all", true)).asJava)

    assert(actual == expected)
  }
}
