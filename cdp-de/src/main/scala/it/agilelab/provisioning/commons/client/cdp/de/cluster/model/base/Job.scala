package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.constant.RetentionPolicy
import it.agilelab.provisioning.commons.client.cdp.de.cluster.model.constant.RetentionPolicy.KEEP_INDEFINITELY

/** Job model
  *
  * from cde swagger API doc.
  *
  * @param name: job name
  * @param `type`: job type
  * @param mounts: sequence of Mount resource
  * @param retentionPolicy: retention policy of the job
  * @param spark: Optional [[SparkJob]] instance
  * @param schedule: Optional [[Schedule]] instance
  */
final case class Job(
  name: String,
  `type`: String,
  mounts: Seq[Mount],
  retentionPolicy: String,
  spark: Option[SparkJob],
  airflow: Option[AirflowJob],
  schedule: Option[Schedule]
)

/** Job companion object
  *
  * contains some utilities method to create specific Job type
  */
object Job {

  private val SPARK_TYPE = "spark"

  /** Create a Job instance that describe a spark job
    *
    * @param name: job name
    * @param resource: resource name
    * @param filePath: jar file path
    * @param className: main class name
    * @param jars: Optional jars dependencies
    * @param args: Optional sequence of args
    * @param driverCores: Optional number of driver cores
    * @param driverMemory: Optional amount of driver memory in Gigabyte
    * @param executorCores: Optional number of executor cores
    * @param executorMemory: Optional number of executor memory in Gigabyte
    * @param numExecutors: Optional number of executors
    * @param logLevel: Optional LogLevel of the spark application
    * @param conf: Optional Map[String,String] that describe spark job config
    * @param schedule: Optional Schedule instance
    * @return Job
    */
  def spark(
    name: String,
    resource: String,
    filePath: String,
    className: String,
    jars: Option[Seq[String]] = None,
    args: Option[Seq[String]] = None,
    driverCores: Option[Int] = None,
    driverMemory: Option[String] = None,
    executorCores: Option[Int] = None,
    executorMemory: Option[String] = None,
    numExecutors: Option[Int] = None,
    logLevel: Option[String] = None,
    conf: Option[Map[String, String]] = None,
    schedule: Option[Schedule] = None
  ): Job =
    Job(
      name = name,
      `type` = SPARK_TYPE,
      mounts = Seq(Mount(resource)),
      retentionPolicy = RetentionPolicy.KEEP_INDEFINITELY,
      airflow = None,
      spark = Some(
        SparkJob.defaultSparkJob(
          filePath,
          className,
          args,
          driverCores,
          driverMemory,
          executorCores,
          executorMemory,
          numExecutors,
          logLevel,
          conf,
          jars
        )
      ),
      schedule = schedule.orElse(Some(Schedule.disabled()))
    )

  /** Create a Job instance that describe a pyspark job
    *
    * @param name: job name
    * @param resource: resource name
    * @param filePath: jar file path
    * @param args: Optional sequence of args
    * @param pythonEnvResourceName: Optional python environment resource
    * @param pyFiles: Optional python dependencies (.egg, .py, .zip)
    * @param driverCores: Optional number of driver cores
    * @param driverMemory: Optional amount of driver memory in Gigabyte
    * @param executorCores: Optional number of executor cores
    * @param executorMemory: Optional number of executor memory in Gigabyte
    * @param numExecutors: Optional number of executors
    * @param logLevel: Optional LogLevel of the spark application
    * @param conf: Optional Map[String,String] that describe spark job config
    * @param schedule: Optional Schedule instance
    * @return Job
    */
  def pyspark(
    name: String,
    resource: String,
    filePath: String,
    args: Option[Seq[String]] = None,
    pythonEnvResourceName: Option[String] = None,
    pyFiles: Option[Seq[String]] = None,
    driverCores: Option[Int] = None,
    driverMemory: Option[String] = None,
    executorCores: Option[Int] = None,
    executorMemory: Option[String] = None,
    numExecutors: Option[Int] = None,
    logLevel: Option[String] = None,
    conf: Option[Map[String, String]] = None,
    schedule: Option[Schedule] = None
  ): Job =
    Job(
      name = name,
      `type` = SPARK_TYPE,
      mounts = Seq(Mount(resource)),
      retentionPolicy = KEEP_INDEFINITELY,
      airflow = None,
      spark = Some(
        SparkJob.defaultPysparkJob(
          filePath,
          args,
          driverCores,
          driverMemory,
          executorCores,
          executorMemory,
          numExecutors,
          logLevel,
          conf,
          pythonEnvResourceName,
          pyFiles
        )
      ),
      schedule = schedule.orElse(Some(Schedule.disabled()))
    )
}
