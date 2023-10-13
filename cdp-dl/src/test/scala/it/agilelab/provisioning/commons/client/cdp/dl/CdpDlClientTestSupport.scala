package it.agilelab.provisioning.commons.client.cdp.dl

import it.agilelab.provisioning.commons.client.cdp.dl.CdpDlClientError.{ DescribeDlErr, FindAllDlErr }
import org.scalatest.EitherValues._

trait CdpDlClientTestSupport {
  def assertFindAllDlErr[A, B <: Throwable](actual: Either[CdpDlClientError, A], error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[FindAllDlErr])
    assert(actual.left.value.asInstanceOf[FindAllDlErr].error.asInstanceOf[B].getMessage == error)
  }

  def assertDescribeDlErr[A, B <: Throwable](actual: Either[CdpDlClientError, A], dl: String, error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[DescribeDlErr])
    assert(actual.left.value.asInstanceOf[DescribeDlErr].datalake == dl)
    assert(actual.left.value.asInstanceOf[DescribeDlErr].error.asInstanceOf[B].getMessage == error)
  }

}
