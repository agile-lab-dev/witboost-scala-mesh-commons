package it.agilelab.provisioning.commons.client.cdp.de

import it.agilelab.provisioning.commons.client.cdp.de.CdpDeClientError._
import org.scalatest.EitherValues._

trait CdpDeClientTestSupport {

  def assertFindAllServiceErr[A, B <: Throwable](actual: Either[CdpDeClientError, A], error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[FindAllServiceErr])
    assert(actual.left.value.asInstanceOf[FindAllServiceErr].error.asInstanceOf[B].getMessage == error)
  }

  def assertDescribeVcErr[A, B <: Throwable](
    actual: Either[CdpDeClientError, A],
    service: String,
    vc: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[DescribeVcErr])
    assert(actual.left.value.asInstanceOf[DescribeVcErr].service == service)
    assert(actual.left.value.asInstanceOf[DescribeVcErr].vc == vc)
    assert(actual.left.value.asInstanceOf[DescribeVcErr].error.asInstanceOf[B].getMessage == error)
  }

  def assertFindAllVcsErr[A, B <: Throwable](
    actual: Either[CdpDeClientError, A],
    service: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[FindAllVcsErr])
    assert(actual.left.value.asInstanceOf[FindAllVcsErr].service == service)
    assert(actual.left.value.asInstanceOf[FindAllVcsErr].error.asInstanceOf[B].getMessage == error)
  }

  def assertDescribeServiceErr[A, B <: Throwable](
    actual: Either[CdpDeClientError, A],
    service: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[DescribeServiceErr])
    assert(actual.left.value.asInstanceOf[DescribeServiceErr].service == service)
    assert(actual.left.value.asInstanceOf[DescribeServiceErr].error.asInstanceOf[B].getMessage == error)
  }

}
