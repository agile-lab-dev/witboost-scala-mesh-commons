package it.agilelab.provisioning.commons.client.cdp.dw

import it.agilelab.provisioning.commons.client.cdp.dw.CdpDwClientError.{ FindAllClustersErr, FindAllVwsErr }
import org.scalatest.EitherValues._

trait CdpDwClientTestSupport {
  def assertFindAllClustersErr[A, B <: Throwable](actual: Either[CdpDwClientError, A], error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[FindAllClustersErr])
    assert(actual.left.value.asInstanceOf[FindAllClustersErr].error.asInstanceOf[B].getMessage == error)
  }

  def assertFindAllVwsErr[A, B <: Throwable](
    actual: Either[CdpDwClientError, A],
    cluster: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[FindAllVwsErr])
    assert(actual.left.value.asInstanceOf[FindAllVwsErr].cluster == cluster)
    assert(actual.left.value.asInstanceOf[FindAllVwsErr].error.asInstanceOf[B].getMessage == error)
  }

}
