package it.agilelab.provisioning.commons.client.ranger.model

import org.scalatest.funsuite.AnyFunSuite

class AccessTest extends AnyFunSuite {

  test("all") {
    assert(Access.all == Access("ALL", isAllowed = true))
  }

  test("read") {
    assert(Access.read == Access("READ", isAllowed = true))
  }

  test("select") {
    assert(Access.select == Access("SELECT", isAllowed = true))
  }
}
