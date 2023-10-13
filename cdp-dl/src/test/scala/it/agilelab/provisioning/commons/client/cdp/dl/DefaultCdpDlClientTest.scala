package it.agilelab.provisioning.commons.client.cdp.dl

import com.cloudera.cdp.datalake.model.{ Datalake, DatalakeDetails, DescribeDatalakeRequest, ListDatalakesRequest }
import it.agilelab.provisioning.commons.client.cdp.dl.wrapper.DataLakeClientWrapper
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultCdpDlClientTest extends AnyFunSuite with MockFactory with CdpDlClientTestSupport {
  val dlClientWrapper: DataLakeClientWrapper = stub[DataLakeClientWrapper]
  val cdpDlClient                            = new DefaultCdpDlClient(dlClientWrapper)

  test("findAllDl return Right") {
    val req = new ListDatalakesRequest()
    val dl1 = new Datalake()
    dl1.setDatalakeName("dl1")
    val dl2 = new Datalake()
    dl2.setDatalakeName("dl2")

    (dlClientWrapper.listDatalakes _).when(req).returns(Seq(dl1, dl2))

    val actual = cdpDlClient.findAllDl()
    assert(actual == Right(Seq(dl1, dl2)))
  }

  test("findAllDl return Left") {
    val req = new ListDatalakesRequest()
    val dl1 = new Datalake()
    dl1.setDatalakeName("dl1")
    val dl2 = new Datalake()
    dl2.setDatalakeName("dl2")

    (dlClientWrapper.listDatalakes _).when(req).throws(new IllegalArgumentException("x"))
    assertFindAllDlErr(cdpDlClient.findAllDl(), "x")
  }

  test("describeDl return Right") {
    val req = new DescribeDatalakeRequest()
    req.setDatalakeName("dl1")

    val res = new DatalakeDetails()
    req.setDatalakeName("dl1")

    (dlClientWrapper.describeDatalake _).when(req).returns(res)
    val actual = cdpDlClient.describeDl("dl1")

    assert(actual == Right(res))
  }

  test("describeDl return Left") {
    val req = new DescribeDatalakeRequest()
    req.setDatalakeName("dl1")

    (dlClientWrapper.describeDatalake _).when(req).throws(new IllegalArgumentException("x"))
    assertDescribeDlErr(cdpDlClient.describeDl("dl1"), "dl1", "x")
  }
}
