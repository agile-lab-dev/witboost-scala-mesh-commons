package it.agilelab.provisioning.commons.http

import org.scalatest.funsuite.AnyFunSuite

class HttpTest extends AnyFunSuite {

  test("default") {
    assert(Http.default().isInstanceOf[DefaultHttp])
  }

  test("defaultWithAudit") {
    assert(Http.defaultWithAudit().isInstanceOf[DefaultHttpWithAudit])
  }

}
