package it.agilelab.provisioning.commons.audit

import com.typesafe.scalalogging.Logger
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.funsuite.AnyFunSuite

class DefaultAuditTest extends AnyFunSuite with MockFactory with OneInstancePerTest {

  //com.typesafe.Logger is a final class, to test logging
  //we have to mock slf4j logger interface
  val logger: org.slf4j.Logger = mock[org.slf4j.Logger]
  val audit: DefaultAudit      = new DefaultAudit(Logger(logger))

  Seq(
    "message 1",
    "message 2"
  ) foreach { message: String =>
    test(s"info call logger with $message") {
      (logger.isInfoEnabled: () => Boolean).expects().returns(true)
      (logger.info(_: String)).expects(message).once()
      audit.info(message)
    }

    test(s"error call logger with $message") {
      (logger.isErrorEnabled: () => Boolean).expects().returns(true)
      (logger.error(_: String)).expects(message).once()
      audit.error(message)
    }

    test(s"warning call logger with $message") {
      (logger.isWarnEnabled: () => Boolean).expects().returns(true)
      (logger.warn(_: String)).expects(message).once()
      audit.warning(message)
    }
  }

}
