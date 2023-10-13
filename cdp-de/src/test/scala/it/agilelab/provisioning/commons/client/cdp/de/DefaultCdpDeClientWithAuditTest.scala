package it.agilelab.provisioning.commons.client.cdp.de

import com.cloudera.cdp.de.model._
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.de.CdpDeClientError._
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultCdpDeClientWithAuditTest extends AnyFunSuite with MockFactory with CdpDeClientTestSupport {

  val defaultCdpDeClient: DefaultCdpDeClient = stub[DefaultCdpDeClient]
  val audit: Audit                           = mock[Audit]
  val cdpDeClient                            = new DefaultCdpDeClientWithAudit(defaultCdpDeClient, audit)

  val service1     = new ServiceSummary()
  val service2     = new ServiceSummary()
  val service1Desc = new ServiceDescription()
  val vc1          = new VcSummary()
  val vc2          = new VcSummary()
  val vc1Desc      = new VcDescription()

  test("findAllServices logs success info") {
    (defaultCdpDeClient.findAllServices _).when().returns(Right(Seq(service1, service2)))
    (audit.info _)
      .expects(where { info: String =>
        info.startsWith("FindAllServices completed successfully")
      })
      .once()
    val actual = cdpDeClient.findAllServices()
    assert(actual == Right(Seq(service1, service2)))
  }

  test("findAllServices logs error info") {
    (defaultCdpDeClient.findAllServices _).when().returns(Left(FindAllServiceErr(new IllegalArgumentException("x"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith("FindAllServices failed. Details: FindAllServiceErr(java.lang.IllegalArgumentException: x")
      })
      .once()
    val actual = cdpDeClient.findAllServices()
    assertFindAllServiceErr(actual, "x")
  }

  test("findServiceByName logs success info") {
    (defaultCdpDeClient.findServiceByName _).when(*).returns(Right(Option(service1)))
    (audit.info _)
      .expects(where { info: String =>
        info.startsWith("FindServiceByName(xyz) completed successfully")
      })
      .once()
    val actual = cdpDeClient.findServiceByName("xyz")
    assert(actual == Right(Option(service1)))
  }

  test("findServiceByName logs error info") {
    (defaultCdpDeClient.findServiceByName _).when(*).returns(Left(FindAllServiceErr(new IllegalArgumentException("x"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith(
          "FindServiceByName(xyz) failed. Details: FindAllServiceErr(java.lang.IllegalArgumentException: x"
        )
      })
      .once()
    val actual = cdpDeClient.findServiceByName("xyz")
    assertFindAllServiceErr(actual, "x")
  }

  test("findAllVcs logs success info") {
    (defaultCdpDeClient.findAllVcs _).when(*).returns(Right(Seq(vc1, vc2)))
    (audit.info _)
      .expects(where { info: String =>
        info.startsWith("FindAllVcs(x) completed successfully")
      })
      .once()
    val actual = cdpDeClient.findAllVcs("x")
    assert(actual == Right(Seq(vc1, vc2)))
  }

  test("findAllVcs logs error info") {
    (defaultCdpDeClient.findAllVcs _).when(*).returns(Left(FindAllVcsErr("x", new IllegalArgumentException("x"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith("FindAllVcs(x) failed. Details: FindAllVcsErr(x,java.lang.IllegalArgumentException: x")
      })
      .once()
    val actual = cdpDeClient.findAllVcs("x")
    assertFindAllVcsErr(actual, "x", "x")
  }

  test("findVcByName logs success info") {
    (defaultCdpDeClient.findVcByName _).when(*, *).returns(Right(Option(vc1)))
    (audit.info _)
      .expects(where { info: String =>
        info.startsWith("FindVcByName(x,y) completed successfully")
      })
      .once()
    val actual = cdpDeClient.findVcByName("x", "y")
    assert(actual == Right(Some(vc1)))
  }

  test("findVcByName logs error info") {
    (defaultCdpDeClient.findVcByName _).when(*, *).returns(Left(FindAllVcsErr("x", new IllegalArgumentException("x"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith("FindVcByName(x,y) failed. Details: FindAllVcsErr(x,java.lang.IllegalArgumentException: x")
      })
      .once()

    val actual = cdpDeClient.findVcByName("x", "y")
    assertFindAllVcsErr(actual, "x", "x")
  }

  test("describeService logs success info") {
    (defaultCdpDeClient.describeService _).when(*).returns(Right(service1Desc))
    (audit.info _)
      .expects(where { info: String =>
        info.startsWith("DescribeService(x) completed successfully")
      })
      .once()

    val actual = cdpDeClient.describeService("x")
    assert(actual == Right(service1Desc))
  }

  test("describeService logs error info") {
    (defaultCdpDeClient.describeService _)
      .when(*)
      .returns(Left(DescribeServiceErr("x", new IllegalArgumentException("x"))))

    (audit.error _)
      .expects(where { s: String =>
        s.startsWith("DescribeService(x) failed. Details: DescribeServiceErr(x,java.lang.IllegalArgumentException: x")
      })
      .once()
    val actual = cdpDeClient.describeService("x")
    assertDescribeServiceErr(actual, "x", "x")
  }

  test("describeServiceByName logs success info") {
    (defaultCdpDeClient.describeServiceByName _).when(*).returns(Right(service1Desc))

    (audit.info _)
      .expects(where { info: String =>
        info.startsWith("DescribeServiceByName(x) completed successfully")
      })
      .once()
    val actual = cdpDeClient.describeServiceByName("x")
    assert(actual == Right(service1Desc))
  }

  test("describeServiceByName logs error info") {
    (defaultCdpDeClient.describeServiceByName _)
      .when(*)
      .returns(Left(DescribeServiceErr("x", new IllegalArgumentException("x"))))

    (audit.error _)
      .expects(where { s: String =>
        s.startsWith(
          "DescribeServiceByName(x) failed. Details: DescribeServiceErr(x,java.lang.IllegalArgumentException: x"
        )
      })
      .once()
    val actual = cdpDeClient.describeServiceByName("x")
    assertDescribeServiceErr(actual, "x", "x")
  }

  test("describeVc logs success info") {
    (defaultCdpDeClient.describeVc _).when(*, *).returns(Right(vc1Desc))
    (audit.info _)
      .expects(where { info: String =>
        info.startsWith("DescribeVc(x,y) completed successfully")
      })
      .once()
    val actual = cdpDeClient.describeVc("x", "y")
    assert(actual == Right(vc1Desc))
  }

  test("describeVc logs error info") {
    (defaultCdpDeClient.describeVc _)
      .when(*, *)
      .returns(Left(DescribeVcErr("x", "y", new IllegalArgumentException("x"))))

    (audit.error _)
      .expects(where { s: String =>
        s.startsWith("DescribeVc(x,y) failed. Details: DescribeVcErr(x,y,java.lang.IllegalArgumentException: x")
      })
      .once()
    val actual = cdpDeClient.describeVc("x", "y")
    assertDescribeVcErr(actual, "x", "y", "x")
  }

  test("describeVcByName logs success info") {
    (defaultCdpDeClient.describeVcByName _).when(*, *).returns(Right(vc1Desc))
    (audit.info _)
      .expects(where { info: String =>
        info.startsWith("DescribeVcByName(x,y) completed successfully")
      })
      .once()
    val actual = cdpDeClient.describeVcByName("x", "y")
    assert(actual == Right(vc1Desc))
  }

  test("describeVcByName logs error info") {
    (defaultCdpDeClient.describeVcByName _)
      .when(*, *)
      .returns(Left(DescribeVcErr("x", "y", new IllegalArgumentException("x"))))

    (audit.error _)
      .expects(where { s: String =>
        s.startsWith("DescribeVcByName(x,y) failed. Details: DescribeVcErr(x,y,java.lang.IllegalArgumentException: x")
      })
      .once()
    val actual = cdpDeClient.describeVcByName("x", "y")
    assertDescribeVcErr(actual, "x", "y", "x")
  }

}
