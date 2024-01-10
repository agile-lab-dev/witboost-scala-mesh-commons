package it.agilelab.provisioning.commons.client.ranger.model

import org.scalatest.funsuite.AnyFunSuite

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
}
