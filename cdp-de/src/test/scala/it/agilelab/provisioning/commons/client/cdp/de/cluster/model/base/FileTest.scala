package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

import org.scalatest.funsuite.AnyFunSuite

class FileTest extends AnyFunSuite {

  test("jar") {
    val actual = File.jar("resName", "my/jar/path.jar", Array(1.toByte))

    val expected = File(
      "resName",
      "my/jar/path.jar",
      "application/java-archive",
      Array(1.toByte)
    )

    assert(actual.resource == expected.resource)
    assert(actual.filePath == expected.filePath)
    assert(actual.mimeType == expected.mimeType)
    assert(actual.file sameElements expected.file)
  }

  test("file") {
    val actual = File.file("resName", "my/file/myfile.txt", Array(1.toByte))

    val expected = File(
      "resName",
      "my/file/myfile.txt",
      "text/plain",
      Array(1.toByte)
    )

    assert(actual.resource == expected.resource)
    assert(actual.filePath == expected.filePath)
    assert(actual.mimeType == expected.mimeType)
    assert(actual.file sameElements expected.file)
  }
}
