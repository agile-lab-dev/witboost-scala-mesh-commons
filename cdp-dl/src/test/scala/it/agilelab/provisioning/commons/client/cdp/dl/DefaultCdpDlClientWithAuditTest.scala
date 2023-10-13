package it.agilelab.provisioning.commons.client.cdp.dl

import com.cloudera.cdp.datalake.model.{ Datalake, DatalakeDetails }
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.client.cdp.dl.CdpDlClientError.{ DescribeDlErr, FindAllDlErr }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultCdpDlClientWithAuditTest extends AnyFunSuite with MockFactory with CdpDlClientTestSupport {

  val defaultCdpDlClient: DefaultCdpDlClient = stub[DefaultCdpDlClient]
  val audit: Audit                           = mock[Audit]
  val cdpDlClient                            = new DefaultCdpDlClientWithAudit(defaultCdpDlClient, audit)

  val datalakeDetails = new DatalakeDetails()

  test("findAllDl logs success info") {
    (defaultCdpDlClient.findAllDl _).when().returns(Right(Seq.empty))

    (audit.info _)
      .expects("FindAllDl completed successfully")

    val actual   = cdpDlClient.findAllDl()
    val expected = Right(Seq.empty[Datalake])

    assert(actual == expected)
  }

  test("findAllDl logs error info") {
    (defaultCdpDlClient.findAllDl _).when().returns(Left(FindAllDlErr(new IllegalArgumentException("x"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith("FindAllDl failed. Details: FindAllDlErr(java.lang.IllegalArgumentException: x")
      })
    assertFindAllDlErr(cdpDlClient.findAllDl(), "x")
  }

  test("describeDl logs success info") {
    (defaultCdpDlClient.describeDl _).when(*).returns(Right(datalakeDetails))
    (audit.info _)
      .expects(where { info: String =>
        info.startsWith("DescribeDl(dl) completed successfully")
      })
    val actual = cdpDlClient.describeDl("dl")
    assert(actual == Right(datalakeDetails))
  }

  test("describeDl logs error info") {
    (defaultCdpDlClient.describeDl _).when(*).returns(Left(DescribeDlErr("dl", new IllegalArgumentException("x"))))
    (audit.error _)
      .expects(where { s: String =>
        s.startsWith("DescribeDl(dl) failed. Details: DescribeDlErr(dl,java.lang.IllegalArgumentException: x")
      })
      .once()
    assertDescribeDlErr(cdpDlClient.describeDl("dl"), "dl", "x")
  }
}
