package it.agilelab.provisioning.commons.client.cdp.dw

import com.cloudera.cdp.dw.model.{ ClusterSummary, ListClustersRequest, VwSummary }
import it.agilelab.provisioning.commons.client.cdp.dw.wrapper.DwClientWrapper
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultCdpDwClientTest extends AnyFunSuite with MockFactory with CdpDwClientTestSupport {

  val dwClientWrapper: DwClientWrapper = stub[DwClientWrapper]
  val cdpDwClient                      = new DefaultCdpDwClient(dwClientWrapper)

  val clusterSummary1 = new ClusterSummary()
  clusterSummary1.setId("1")
  clusterSummary1.setEnvironmentCrn("crn-env1")

  val clusterSummary2 = new ClusterSummary()
  clusterSummary2.setId("2")
  clusterSummary2.setEnvironmentCrn("crn-env2")

  val vwSummary1 = new VwSummary()
  vwSummary1.setId("1.1")
  vwSummary1.setName("1.1.name")

  val vwSummary2 = new VwSummary()
  vwSummary2.setId("1.2")
  vwSummary2.setName("1.2.name")

  test("findAllClusters return Right(Seq(ClusterSummary))") {
    (dwClientWrapper.listClusters _)
      .when(new ListClustersRequest())
      .returns(Seq(clusterSummary1, clusterSummary2))

    val actual   = cdpDwClient.findAllClusters()
    val expected = Right(Seq(clusterSummary1, clusterSummary2))
    assert(actual == expected)
  }

  test("findAllClusters return Left(CdpDwClientError) on exception") {
    (dwClientWrapper.listClusters _)
      .when(new ListClustersRequest())
      .throws(new IllegalArgumentException("x"))
    assertFindAllClustersErr(cdpDwClient.findAllClusters(), "x")
  }

  test("findClusterByEnvironmentCrn return Right(Some)") {
    (dwClientWrapper.listClusters _)
      .when(new ListClustersRequest())
      .returns(Seq(clusterSummary1, clusterSummary2))

    val actual   = cdpDwClient.findClusterByEnvironmentCrn("crn-env1")
    val expected = Right(Some(clusterSummary1))
    assert(actual == expected)
  }

  test("findClusterByEnvironmentCrn return Right(None)") {
    (dwClientWrapper.listClusters _)
      .when(new ListClustersRequest())
      .returns(Seq(clusterSummary1, clusterSummary2))

    val actual   = cdpDwClient.findClusterByEnvironmentCrn("crn-env3")
    val expected = Right(None)
    assert(actual == expected)
  }

  test("findClusterByEnvironmentCrn return Left(Error)") {
    (dwClientWrapper.listClusters _)
      .when(new ListClustersRequest())
      .throws(new IllegalArgumentException("x"))
    assertFindAllClustersErr(cdpDwClient.findClusterByEnvironmentCrn("crn-env1"), "x")
  }

  test("findAllVw return Right(Seq)") {
    (dwClientWrapper.listVws _)
      .when("1")
      .returns(Seq(vwSummary1, vwSummary2))

    val actual   = cdpDwClient.findAllVw("1")
    val expected = Right(Seq(vwSummary1, vwSummary2))
    assert(actual == expected)
  }

  test("findAllVw return Left(Err)") {
    (dwClientWrapper.listVws _)
      .when("1")
      .throws(new IllegalArgumentException("x"))
    assertFindAllVwsErr(cdpDwClient.findAllVw("1"), "1", "x")
  }

  test("findVwByName return Right(Some)") {
    (dwClientWrapper.listVws _)
      .when("1")
      .returns(Seq(vwSummary1, vwSummary2))

    val actual   = cdpDwClient.findVwByName("1", "1.2.name")
    val expected = Right(Some(vwSummary2))

    assert(actual == expected)
  }

  test("findVwByName return Right(None)") {
    (dwClientWrapper.listVws _)
      .when("1")
      .returns(Seq(vwSummary1, vwSummary2))

    val actual   = cdpDwClient.findVwByName("1", "1.3.name")
    val expected = Right(None)

    assert(actual == expected)
  }

  test("findVwByName return Left(Err)") {
    (dwClientWrapper.listVws _)
      .when("1")
      .throws(new IllegalArgumentException("x"))
    assertFindAllVwsErr(cdpDwClient.findVwByName("1", "1.3.name"), "1", "x")
  }
}
