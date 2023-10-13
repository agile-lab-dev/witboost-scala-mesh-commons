package it.agilelab.provisioning.commons.client.cdp.env

import com.cloudera.cdp.environments.model.{
  DescribeEnvironmentRequest,
  Environment,
  EnvironmentSummary,
  SyncAllUsersRequest,
  SyncAllUsersResponse,
  SyncStatusRequest,
  SyncStatusResponse
}
import it.agilelab.provisioning.commons.client.cdp.env.wrapper.EnvironmentsClientWrapper
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

import java.util

class DefaultCdpEnvClientTest extends AnyFunSuite with MockFactory with CdpEnvClientTestSupport {
  val environmentsClientWrapper: EnvironmentsClientWrapper = mock[EnvironmentsClientWrapper]
  val cdpEnvClient                                         = new DefaultCdpEnvClient(environmentsClientWrapper)

  test("listEnvironments return Right") {
    (environmentsClientWrapper.listEnvironments _).expects(*).once().returns(Seq(new EnvironmentSummary()))
    val actual = cdpEnvClient.listEnvironments()
    assert(actual == Right(Seq(new EnvironmentSummary())))
  }

  test("listEnvironments return Left") {
    (environmentsClientWrapper.listEnvironments _).expects(*).once().throws(new IllegalArgumentException("x"))
    assertListEnvironmentsErr(cdpEnvClient.listEnvironments(), "x")
  }

  test("describeEnvironment return Right") {
    val req = new DescribeEnvironmentRequest()
    req.setEnvironmentName("env1")

    val res = new Environment()
    res.setEnvironmentName("env1")

    (environmentsClientWrapper.describeEnvironment _).expects(req).once().returns(res)
    val actual   = cdpEnvClient.describeEnvironment("env1")
    val expected = Right(res)

    assert(actual == expected)
  }

  test("describeEnvironment return Left") {
    val req = new DescribeEnvironmentRequest()
    req.setEnvironmentName("env1")

    (environmentsClientWrapper.describeEnvironment _)
      .expects(req)
      .once()
      .throws(new IllegalArgumentException("x"))

    assertDescribeEnvironmentErr(cdpEnvClient.describeEnvironment("env1"), "env1", "x")
  }

  test("syncAllUsers return Right") {
    val req1 = new SyncAllUsersRequest()
    req1.setEnvironmentNames(util.Arrays.asList("my-env-name"))
    val res1 = new SyncAllUsersResponse()
    res1.setOperationId("operation-id")
    res1.setStatus("REQUESTED")

    val req2 = new SyncStatusRequest()
    req2.setOperationId("operation-id")
    val res2 = new SyncStatusResponse()
    res2.setStatus("RUNNING")

    val req3 = new SyncStatusRequest()
    req3.setOperationId("operation-id")
    val res3 = new SyncStatusResponse()
    res3.setStatus("RUNNING")

    val req4 = new SyncStatusRequest()
    req4.setOperationId("operation-id")
    val res4 = new SyncStatusResponse()
    res4.setStatus("COMPLETED")

    inSequence(
      (environmentsClientWrapper.syncAllUsers _)
        .expects(req1)
        .once()
        .returns(res1),
      (environmentsClientWrapper.syncStatus _)
        .expects(req2)
        .once()
        .returns(res2),
      (environmentsClientWrapper.syncStatus _)
        .expects(req3)
        .once()
        .returns(res3),
      (environmentsClientWrapper.syncStatus _)
        .expects(req4)
        .once()
        .returns(res4)
    )

    val actual   = cdpEnvClient.syncAllUsers("my-env-name")
    val expected = Right()
    assert(actual == expected)
  }

  test("syncAllUsers return Left(SyncAllUsersErr)") {
    val req1 = new SyncAllUsersRequest()
    req1.setEnvironmentNames(util.Arrays.asList("my-env-name"))

    inSequence(
      (environmentsClientWrapper.syncAllUsers _)
        .expects(req1)
        .once()
        .throws(new IllegalArgumentException("x"))
    )

    val actual = cdpEnvClient.syncAllUsers("my-env-name")
    assertSyncAllUsersErr(actual, "my-env-name", "x")
  }

  test("syncAllUsers return Left(SyncStatusErr) when status is not COMPLETED") {
    val req1 = new SyncAllUsersRequest()
    req1.setEnvironmentNames(util.Arrays.asList("my-env-name"))
    val res1 = new SyncAllUsersResponse()
    res1.setOperationId("operation-id")
    res1.setStatus("REQUESTED")

    val req2 = new SyncStatusRequest()
    req2.setOperationId("operation-id")
    val res2 = new SyncStatusResponse()
    res2.setStatus("RUNNING")

    val req3 = new SyncStatusRequest()
    req3.setOperationId("operation-id")
    val res3 = new SyncStatusResponse()
    res3.setStatus("RUNNING")

    val req4 = new SyncStatusRequest()
    req4.setOperationId("operation-id")
    val res4 = new SyncStatusResponse()
    res4.setStatus("FAILED")
    res4.setError("x")

    inSequence(
      (environmentsClientWrapper.syncAllUsers _)
        .expects(req1)
        .once()
        .returns(res1),
      (environmentsClientWrapper.syncStatus _)
        .expects(req2)
        .once()
        .returns(res2),
      (environmentsClientWrapper.syncStatus _)
        .expects(req3)
        .once()
        .returns(res3),
      (environmentsClientWrapper.syncStatus _)
        .expects(req4)
        .once()
        .returns(res4)
    )

    val actual = cdpEnvClient.syncAllUsers("my-env-name")
    assertSyncStatusErr(actual, "my-env-name", "SyncAllUsers(my-env-name) returned status FAILED; Details: x")
  }

  test("syncAllUsers return Left(SyncStatusErr) when maxChecks have been exceeded") {
    val req1 = new SyncAllUsersRequest()
    req1.setEnvironmentNames(util.Arrays.asList("my-env-name"))
    val res1 = new SyncAllUsersResponse()
    res1.setOperationId("operation-id")
    res1.setStatus("REQUESTED")

    val req2 = new SyncStatusRequest()
    req2.setOperationId("operation-id")
    val res2 = new SyncStatusResponse()
    res2.setStatus("RUNNING")

    val req3 = new SyncStatusRequest()
    req3.setOperationId("operation-id")
    val res3 = new SyncStatusResponse()
    res3.setStatus("RUNNING")

    val req4 = new SyncStatusRequest()
    req4.setOperationId("operation-id")
    val res4 = new SyncStatusResponse()
    res4.setStatus("RUNNING")

    inSequence(
      (environmentsClientWrapper.syncAllUsers _)
        .expects(req1)
        .once()
        .returns(res1),
      (environmentsClientWrapper.syncStatus _)
        .expects(req2)
        .once()
        .returns(res2),
      (environmentsClientWrapper.syncStatus _)
        .expects(req3)
        .once()
        .returns(res3),
      (environmentsClientWrapper.syncStatus _)
        .expects(req4)
        .once()
        .returns(res4)
    )

    val actual = cdpEnvClient.syncAllUsers("my-env-name", maxChecks = 3)
    assertSyncStatusErr(actual, "my-env-name", "Max number of checks 3 have been exceeded")
  }

}
