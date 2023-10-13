package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base.Resource.{
  FilesResource,
  PythonEnvironmentResource
}
import org.scalatest.funsuite.AnyFunSuite

class ResourceTest extends AnyFunSuite {

  test("filesResource") {
    val actual   = Resource.filesResource("test-res")
    val expected = FilesResource(
      name = "test-res",
      `type` = "files",
      retentionPolicy = "keep_indefinitely"
    )
    assert(actual == expected)
  }

  test("environmentResource") {
    val actual   = Resource.environmentResource("test-res", "pyversion", Some("pypi-mirror"))
    val expected = PythonEnvironmentResource(
      name = "test-res",
      `type` = "python-env",
      retentionPolicy = "keep_indefinitely",
      pythonEnvironment = PythonEnvironment("pyversion", Some("pypi-mirror"))
    )
    assert(actual == expected)
  }

}
