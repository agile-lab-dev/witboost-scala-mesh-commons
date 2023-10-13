package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.constant

import org.scalatest.funsuite.AnyFunSuite

class RetentionPolicyTest extends AnyFunSuite {

  test("KEEP_INDEFINITELY") {
    assert(RetentionPolicy.KEEP_INDEFINITELY == "keep_indefinitely")
  }
}
