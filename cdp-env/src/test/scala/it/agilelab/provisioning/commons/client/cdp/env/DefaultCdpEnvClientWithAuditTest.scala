package it.agilelab.provisioning.commons.client.cdp.env

import com.cloudera.cdp.environments.model.{ Environment, EnvironmentSummary }
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.env.CdpEnvClientError._
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultCdpEnvClientWithAuditTest extends AnyFunSuite with MockFactory with CdpEnvClientTestSupport {

  val audit: Audit                             = mock[Audit]
  val defaultCdpEnvClient: DefaultCdpEnvClient = stub[DefaultCdpEnvClient]
  val cdpEnvClient                             = new DefaultCdpEnvClientWithAudit(defaultCdpEnvClient, audit)

  test("listEnvironments return Right") {
    (defaultCdpEnvClient.listEnvironments _).when().returns(Right(Seq(new EnvironmentSummary())))
    inSequence(
      (audit.info _).expects("Executing ListEnvironments").once(),
      (audit.info _).expects(where { info: String =>
        info.startsWith("ListEnvironments completed successfully")
      })
    )
    val actual = cdpEnvClient.listEnvironments()
    assert(actual == Right(Seq(new EnvironmentSummary())))
  }

  test("listEnvironments return Left") {
    (defaultCdpEnvClient.listEnvironments _)
      .when()
      .returns(Left(ListEnvironmentsErr(new IllegalArgumentException("x"))))
    inSequence(
      (audit.info _).expects("Executing ListEnvironments").once(),
      (audit.error _).expects(where { s: String =>
        s.startsWith("ListEnvironments failed. Details: ListEnvironmentsErr(java.lang.IllegalArgumentException: x")
      })
    )
    assertListEnvironmentsErr(cdpEnvClient.listEnvironments(), "x")
  }

  test("describeEnvironment return Right") {
    (defaultCdpEnvClient.describeEnvironment _).when(*).returns(Right(new Environment()))
    inSequence(
      (audit.info _).expects("Executing DescribeEnvironment(env1)").once(),
      (audit.info _).expects(where { info: String =>
        info.startsWith("DescribeEnvironment(env1) completed successfully")
      })
    )
    val actual = cdpEnvClient.describeEnvironment("env1")
    assert(actual == Right(new Environment()))
  }

  test("describeEnvironment return Left") {
    (defaultCdpEnvClient.describeEnvironment _)
      .when(*)
      .returns(Left(DescribeEnvironmentErr("env1", new IllegalArgumentException("x"))))
    inSequence(
      (audit.info _).expects("Executing DescribeEnvironment(env1)").once(),
      (audit.error _).expects(where { s: String =>
        s.startsWith(
          "DescribeEnvironment(env1) failed. Details: DescribeEnvironmentErr(env1,java.lang.IllegalArgumentException: x"
        )
      })
    )
    assertDescribeEnvironmentErr(cdpEnvClient.describeEnvironment("env1"), "env1", "x")
  }

  test("syncAllUsers return Right") {
    (defaultCdpEnvClient.syncAllUsers _).when(*, *).returns(Right())
    inSequence(
      (audit.info _).expects("Executing SyncAllUsers(env1,25)").once(),
      (audit.info _).expects("SyncAllUsers(env1,25) completed successfully")
    )
    val actual = cdpEnvClient.syncAllUsers("env1")
    assert(actual == Right())
  }

  test("syncAllUsers return Left") {
    (defaultCdpEnvClient.syncAllUsers _)
      .when(*, *)
      .returns(Left(SyncAllUsersErr("env1", new IllegalArgumentException("x"))))
    inSequence(
      (audit.info _).expects("Executing SyncAllUsers(env1,25)").once(),
      (audit.error _).expects(where { s: String =>
        s.startsWith(
          "SyncAllUsers(env1,25) failed. Details: SyncAllUsersErr(env1,java.lang.IllegalArgumentException: x"
        )
      })
    )
    assertSyncAllUsersErr(cdpEnvClient.syncAllUsers("env1"), "env1", "x")
  }
}
