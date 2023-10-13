package it.agilelab.provisioning.commons.audit

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class AuditTest extends AnyFunSuite with MockFactory {

  test("default") {
    assert(Audit.default("my-logger").isInstanceOf[DefaultAudit])
  }

}
