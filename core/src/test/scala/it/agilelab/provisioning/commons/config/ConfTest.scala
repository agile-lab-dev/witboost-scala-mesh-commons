package it.agilelab.provisioning.commons.config

import org.scalatest.funsuite.AnyFunSuite

class ConfTest extends AnyFunSuite {

  test("env") {
    val actual = Conf.env()
    assert(actual.isInstanceOf[DefaultConf])
  }

  test("envWithAudit") {
    val actual = Conf.envWithAudit()
    assert(actual.isInstanceOf[DefaultConfWithAudit])
  }
}
