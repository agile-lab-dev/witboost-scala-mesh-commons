package it.agilelab.provisioning.aws.s3.batch.gateway

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.services.s3control.S3ControlClient

class S3BatchOperationsGatewayTest extends AnyFunSuite with MockFactory {
  test("default") {
    val client = mock[S3ControlClient]
    val actual = S3BatchOperationsGateway.default("accountId", client)
    assert(actual.isInstanceOf[DefaultS3BatchOperationsGateway])
  }

  test("defaultWithAudit") {
    val client = mock[S3ControlClient]
    val actual = S3BatchOperationsGateway.defaultWithAudit("accountId", client)
    assert(actual.isInstanceOf[DefaultS3BatchOperationsGatewayWithAudit])
  }

}
