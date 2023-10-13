package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

import org.scalatest.funsuite.AnyFunSuite

class JobTest extends AnyFunSuite {

  test("spark without schedule") {
    val actual   = Job.spark("test-job", "resource", "path/my-file.jar", "myClassName")
    val expected = Job(
      name = "test-job",
      `type` = "spark",
      mounts = Seq(Mount("resource")),
      retentionPolicy = "keep_indefinitely",
      spark = Some(
        SparkJob(
          file = "path/my-file.jar",
          driverCores = 1,
          driverMemory = "1g",
          executorCores = 1,
          executorMemory = "1g",
          logLevel = Some("INFO"),
          numExecutors = Some(1),
          className = Some("myClassName"),
          args = None,
          conf = Some(Map("dex.safariEnabled" -> "true")),
          jars = None,
          proxyUser = None,
          pythonEnvResourceName = None,
          pyFiles = None
        )
      ),
      airflow = None,
      schedule = Some(Schedule.disabled())
    )
    assert(actual == expected)
  }

  test("spark with schedule") {
    val actual   = Job.spark(
      "test-job",
      "resource",
      "path/my-file.jar",
      "myClassName",
      schedule = Some(Schedule.enable("usr", "xx", "tt", "vv"))
    )
    val expected = Job(
      name = "test-job",
      `type` = "spark",
      mounts = Seq(Mount("resource")),
      retentionPolicy = "keep_indefinitely",
      spark = Some(
        SparkJob(
          file = "path/my-file.jar",
          driverCores = 1,
          driverMemory = "1g",
          executorCores = 1,
          executorMemory = "1g",
          numExecutors = Some(1),
          logLevel = Some("INFO"),
          className = Some("myClassName"),
          args = None,
          conf = Some(Map("dex.safariEnabled" -> "true")),
          jars = None,
          proxyUser = None,
          pythonEnvResourceName = None,
          pyFiles = None
        )
      ),
      airflow = None,
      schedule = Some(Schedule.enable("usr", "xx", "tt", "vv"))
    )
    assert(actual == expected)
  }

  test("pyspark without schedule") {
    val actual   = Job.pyspark("test-job", "resource", "path/my-file.jar")
    val expected = Job(
      name = "test-job",
      `type` = "spark",
      mounts = List(Mount("resource")),
      retentionPolicy = "keep_indefinitely",
      spark = Some(
        SparkJob(
          file = "path/my-file.jar",
          driverCores = 1,
          driverMemory = "1g",
          executorCores = 1,
          executorMemory = "1g",
          logLevel = Some("INFO"),
          numExecutors = Some(1),
          className = None,
          args = None,
          conf = Some(Map("dex.safariEnabled" -> "true")),
          jars = None,
          proxyUser = None,
          pythonEnvResourceName = None,
          pyFiles = None
        )
      ),
      airflow = None,
      schedule = Some(
        Schedule(
          enabled = false,
          user = None,
          paused = None,
          catchup = None,
          dependsOnPast = None,
          pausedUponCreation = None,
          start = None,
          end = None,
          cronExpression = None,
          None
        )
      )
    )
    assert(actual == expected)
  }

  test("pyspark with schedule") {
    val actual = Job.pyspark(
      name = "test-job",
      resource = "resource",
      filePath = "path/my-file.jar",
      schedule = Some(Schedule.enable(user = "usr", cronExpression = "xx", start = "tt", end = "vv"))
    )

    val expected = Job(
      name = "test-job",
      `type` = "spark",
      mounts = List(Mount("resource")),
      retentionPolicy = "keep_indefinitely",
      spark = Some(
        SparkJob(
          file = "path/my-file.jar",
          driverCores = 1,
          driverMemory = "1g",
          executorCores = 1,
          executorMemory = "1g",
          logLevel = Some("INFO"),
          numExecutors = Some(1),
          className = None,
          args = None,
          conf = Some(Map("dex.safariEnabled" -> "true")),
          jars = None,
          proxyUser = None,
          pythonEnvResourceName = None,
          pyFiles = None
        )
      ),
      airflow = None,
      schedule = Some(Schedule.enable("usr", "xx", "tt", "vv"))
    )
    assert(actual == expected)
  }

}
