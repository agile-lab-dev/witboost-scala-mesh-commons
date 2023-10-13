package it.agilelab.provisioning.aws.iam.gateway

import org.scalatest.funsuite.AnyFunSuite

class IamGatewayTest extends AnyFunSuite {

  test("default") {
    val actual = IamGateway.default()
    assert(actual.isRight)
    assert(actual.getOrElse(fail()).isInstanceOf[IamGateway])
  }

  test("defaultWithAudit") {
    val actual = IamGateway.defaultWithAudit()
    assert(actual.isRight)
    assert(actual.getOrElse(fail()).isInstanceOf[IamGateway])
  }

}
