package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

import org.scalatest.funsuite.AnyFunSuite

class ScheduleTest extends AnyFunSuite {

  test("disable") {
    val actual   = Schedule.disabled()
    val expected = Schedule(
      enabled = false,
      user = None,
      paused = None,
      catchup = None,
      dependsOnPast = None,
      pausedUponCreation = None,
      start = None,
      end = None,
      cronExpression = None,
      nextExecution = None
    )
    assert(actual == expected)
  }

  test("enabled") {
    val actual   =
      Schedule.enable(
        "usr",
        "* * * *",
        "2021-05-18T07:27:34.987Z",
        "2021-05-18T07:27:34.987Z"
      )
    val expected = Schedule(
      enabled = true,
      user = Some("usr"),
      paused = None,
      catchup = None,
      dependsOnPast = None,
      pausedUponCreation = None,
      start = Some("2021-05-18T07:27:34.987Z"),
      end = Some("2021-05-18T07:27:34.987Z"),
      cronExpression = Some("* * * *"),
      nextExecution = None
    )
    assert(actual == expected)
  }
}
