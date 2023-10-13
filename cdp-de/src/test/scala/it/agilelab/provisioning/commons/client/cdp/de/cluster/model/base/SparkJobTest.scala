package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

import org.scalatest.funsuite.AnyFunSuite

class SparkJobTest extends AnyFunSuite {

  test("default") {
    val actual   = SparkJob.defaultSparkJob("mypath/myfile.jar", "com.test.xx")
    val expected = SparkJob(
      file = "mypath/myfile.jar",
      driverCores = 1,
      driverMemory = "1g",
      executorCores = 1,
      executorMemory = "1g",
      numExecutors = Some(1),
      logLevel = Some("INFO"),
      className = Some("com.test.xx"),
      args = None,
      conf = Some(Map("dex.safariEnabled" -> "true")),
      jars = None,
      proxyUser = None,
      pythonEnvResourceName = None,
      pyFiles = None
    )
    assert(actual == expected)
  }

  test("default with args") {
    val actual   = SparkJob.defaultSparkJob("mypath/myfile.jar", "com.test.xx", Some(Seq("abc")))
    val expected = SparkJob(
      file = "mypath/myfile.jar",
      driverCores = 1,
      driverMemory = "1g",
      executorCores = 1,
      executorMemory = "1g",
      numExecutors = Some(1),
      logLevel = Some("INFO"),
      className = Some("com.test.xx"),
      args = Some(Seq("abc")),
      conf = Some(Map("dex.safariEnabled" -> "true")),
      jars = None,
      proxyUser = None,
      pythonEnvResourceName = None,
      pyFiles = None
    )
    assert(actual == expected)
  }

  test("default with driverCores") {
    val actual   = SparkJob.defaultSparkJob("mypath/myfile.jar", "com.test.xx")
    val expected = SparkJob(
      file = "mypath/myfile.jar",
      driverCores = 1,
      driverMemory = "1g",
      executorCores = 1,
      executorMemory = "1g",
      numExecutors = Some(1),
      logLevel = Some("INFO"),
      className = Some("com.test.xx"),
      args = None,
      conf = Some(Map("dex.safariEnabled" -> "true")),
      jars = None,
      proxyUser = None,
      pythonEnvResourceName = None,
      pyFiles = None
    )
    assert(actual == expected)
  }

  test("default with Schedule") {
    val actual   = SparkJob.defaultSparkJob("mypath/myfile.jar", "com.test.xx")
    val expected = SparkJob(
      file = "mypath/myfile.jar",
      driverCores = 1,
      driverMemory = "1g",
      executorCores = 1,
      executorMemory = "1g",
      numExecutors = Some(1),
      logLevel = Some("INFO"),
      className = Some("com.test.xx"),
      args = None,
      conf = Some(Map("dex.safariEnabled" -> "true")),
      jars = None,
      proxyUser = None,
      pythonEnvResourceName = None,
      pyFiles = None
    )
    assert(actual == expected)
  }
}
