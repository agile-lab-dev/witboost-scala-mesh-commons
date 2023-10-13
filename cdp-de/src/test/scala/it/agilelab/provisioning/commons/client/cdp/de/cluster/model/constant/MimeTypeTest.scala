package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.constant

import org.scalatest.funsuite.AnyFunSuite

class MimeTypeTest extends AnyFunSuite {

  test("JAVA_ARCHIVE") {
    assert(MimeType.JAVA_ARCHIVE == "application/java-archive")
  }
}
