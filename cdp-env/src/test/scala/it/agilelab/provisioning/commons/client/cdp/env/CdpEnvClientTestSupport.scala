package it.agilelab.provisioning.commons.client.cdp.env

import it.agilelab.provisioning.commons.client.cdp.env.CdpEnvClientError._
import org.scalatest.EitherValues._

trait CdpEnvClientTestSupport {
  def assertListEnvironmentsErr[A, B <: Throwable](actual: Either[CdpEnvClientError, A], error: String): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[ListEnvironmentsErr])
    assert(actual.left.value.asInstanceOf[ListEnvironmentsErr].error.asInstanceOf[B].getMessage == error)
  }

  def assertDescribeEnvironmentErr[A, B <: Throwable](
    actual: Either[CdpEnvClientError, A],
    envName: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[DescribeEnvironmentErr])
    assert(actual.left.value.asInstanceOf[DescribeEnvironmentErr].envName == envName)
    assert(actual.left.value.asInstanceOf[DescribeEnvironmentErr].error.asInstanceOf[B].getMessage == error)
  }

  def assertSyncAllUsersErr[A, B <: Throwable](
    actual: Either[CdpEnvClientError, A],
    envName: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[SyncAllUsersErr])
    assert(actual.left.value.asInstanceOf[SyncAllUsersErr].envName == envName)
    assert(actual.left.value.asInstanceOf[SyncAllUsersErr].error.asInstanceOf[B].getMessage == error)
  }

  def assertSyncStatusErr[A, B <: Throwable](
    actual: Either[CdpEnvClientError, A],
    envName: String,
    error: String
  ): Unit = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[SyncStatusErr])
    assert(actual.left.value.asInstanceOf[SyncStatusErr].envName == envName)
    assert(actual.left.value.asInstanceOf[SyncStatusErr].error == error)
  }

}
