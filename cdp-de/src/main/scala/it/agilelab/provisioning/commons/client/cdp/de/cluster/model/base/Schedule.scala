package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

/** Schedule model
  *
  * from cde swagger API doc.
  *
  * @param enabled: boolean value that describe schedule state
  * @param user: Optional user
  * @param paused: Optional boolean value that describe if a schedule are in pause
  * @param catchup: Optional  -
  * @param dependsOnPast: Optional -
  * @param pausedUponCreation: Optional -
  * @param start: Optional start date
  * @param end: Optional end date
  * @param cronExpression: Optional cron expression
  * @param nextExecution: Optional next schedule execution
  */
final case class Schedule(
  enabled: Boolean,
  user: Option[String],
  paused: Option[Boolean],
  catchup: Option[Boolean],
  dependsOnPast: Option[Boolean],
  pausedUponCreation: Option[Boolean],
  start: Option[String],
  end: Option[String],
  cronExpression: Option[String],
  nextExecution: Option[String]
)

/** Schedule companion object
  *
  * contains some utilities method to create specific Schedule type
  */
object Schedule {

  /** Create a disabled Schedule
    * @return
    */
  def disabled(): Schedule =
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
      nextExecution = None
    )

  /** Create an enable Schedule isntance
    * @param user: user
    * @param cronExpression: cron expression
    * @param start: start date
    * @param end: end date
    * @return
    */
  def enable(
    user: String,
    cronExpression: String,
    start: String,
    end: String
  ): Schedule =
    Schedule(
      enabled = true,
      user = Some(user),
      paused = None,
      catchup = None,
      dependsOnPast = None,
      pausedUponCreation = None,
      start = Some(start),
      end = Some(end),
      cronExpression = Some(cronExpression),
      nextExecution = None
    )
}
