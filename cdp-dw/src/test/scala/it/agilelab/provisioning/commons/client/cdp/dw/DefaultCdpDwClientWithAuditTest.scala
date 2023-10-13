package it.agilelab.provisioning.commons.client.cdp.dw

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.dw.CdpDwClientError.{ FindAllClustersErr, FindAllVwsErr }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultCdpDwClientWithAuditTest extends AnyFunSuite with MockFactory with CdpDwClientTestSupport {

  val audit: Audit                           = mock[Audit]
  val defaultCdpDwClient: DefaultCdpDwClient = stub[DefaultCdpDwClient]
  val cdpDwClient                            = new DefaultCdpDwClientWithAudit(defaultCdpDwClient, audit)

  test("findAllClusters logs success info") {
    (defaultCdpDwClient.findAllClusters _).when().returns(Right(Seq.empty))
    (audit.info _).expects("FindAllClusters completed successfully")
    val actual = cdpDwClient.findAllClusters()
    assert(actual == Right(Seq.empty))
  }

  test("findAllClusters logs error info") {
    (defaultCdpDwClient.findAllClusters _).when().returns(Left(FindAllClustersErr(new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith("FindAllClusters failed. Details: FindAllClustersErr(java.lang.IllegalArgumentException: x")
    })
    assertFindAllClustersErr(cdpDwClient.findAllClusters(), "x")
  }

  test("findClusterByEnvironmentCrn logs success info") {
    (defaultCdpDwClient.findClusterByEnvironmentCrn _).when(*).returns(Right(None))
    (audit.info _).expects("FindClusterByEnvironmentCrn(x) completed successfully")
    val actual = cdpDwClient.findClusterByEnvironmentCrn("x")
    assert(actual == Right(None))
  }

  test("findClusterByEnvironmentCrn logs error info") {
    (defaultCdpDwClient.findClusterByEnvironmentCrn _)
      .when(*)
      .returns(Left(FindAllClustersErr(new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith(
        "FindClusterByEnvironmentCrn(x) failed. Details: FindAllClustersErr(java.lang.IllegalArgumentException: x"
      )
    })
    assertFindAllClustersErr(cdpDwClient.findClusterByEnvironmentCrn("x"), "x")
  }

  test("findAllVw logs success info") {
    (defaultCdpDwClient.findAllVw _).when(*).returns(Right(Seq.empty))
    (audit.info _).expects("FindAllVw(x) completed successfully")
    val actual = cdpDwClient.findAllVw("x")
    assert(actual == Right(Seq.empty))
  }

  test("findAllVw logs error info") {
    (defaultCdpDwClient.findAllVw _).when(*).returns(Left(FindAllVwsErr("x", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith("FindAllVw(x) failed. Details: FindAllVwsErr(x,java.lang.IllegalArgumentException: x")
    })
    assertFindAllVwsErr(cdpDwClient.findAllVw("x"), "x", "x")
  }

  test("findVwByName logs success info") {
    (defaultCdpDwClient.findVwByName _).when(*, *).returns(Right(None))
    (audit.info _).expects("FindVwByName(x,y) completed successfully")
    val actual = cdpDwClient.findVwByName("x", "y")
    assert(actual == Right(None))
  }

  test("findVwByName logs error info") {
    (defaultCdpDwClient.findVwByName _).when(*, *).returns(Left(FindAllVwsErr("x", new IllegalArgumentException("x"))))
    (audit.error _).expects(where { s: String =>
      s.startsWith("FindVwByName(x,y) failed. Details: FindAllVwsErr(x,java.lang.IllegalArgumentException: x")
    })
    assertFindAllVwsErr(cdpDwClient.findVwByName("x", "y"), "x", "x")
  }
}
