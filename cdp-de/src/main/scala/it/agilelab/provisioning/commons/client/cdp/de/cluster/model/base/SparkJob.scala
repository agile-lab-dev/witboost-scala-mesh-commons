package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.base

/** SparkJob model
  * from cde swagger API doc
  *
  * @param file: jar file path
  * @param driverCores: number of driver cores
  * @param driverMemory: amount of driver memory in Gigabytes
  * @param executorCores: number of executor cores
  * @param executorMemory: amount of executor memory
  * @param numExecutors: number of executors
  * @param logLevel: Log level
  * @param className: Optional application class name
  * @param args: Optional sequence of arguments
  * @param conf: Optional map of configuration
  * @param jars: optional lists of external jars
  * @param proxyUser: -
  * @param pythonEnvResourceName: Optional python environment resource
  * @param pyFiles: Optional python dependencies (.egg, .py, .zip)
  */
final case class SparkJob(
  file: String,
  driverCores: Int,
  driverMemory: String,
  executorCores: Int,
  executorMemory: String,
  logLevel: Option[String],
  numExecutors: Option[Int],
  className: Option[String],
  args: Option[Seq[String]],
  conf: Option[Map[String, String]],
  jars: Option[Seq[String]],
  proxyUser: Option[String],
  pythonEnvResourceName: Option[String],
  pyFiles: Option[Seq[String]]
)

object SparkJob {

  val DEFAULT_DRIVER_CORES: Int            = 1
  val DEFAULT_EXECUTOR_CORES: Int          = 1
  val DEFAULT_NUM_EXECUTOR: Int            = 1
  val DEFAULT_DRIVER_MEMORY: String        = "1g"
  val DEFAULT_EXECUTOR_MEMORY: String      = "1g"
  val DEFAULT_LOG_LEVEL: String            = "INFO"
  val DEX_BASE_CONFIG: Map[String, String] = Map("dex.safariEnabled" -> "true")

  /** Create a default SparkJob instance
    * @param file: jar file path
    * @param className: application class name
    * @param args: Optional sequence of arguments
    * @param driverCores: Optional number of driver cores, if not provided the created job will have 1 driver cores as default value
    * @param driverMemory: Optional amount of driver memory, if not provided the created job will have 1 Gigabyte of driver memory as default value
    * @param executorCores: Optional number of executor cores, if not provided the created job will have 1 executor cores as default value
    * @param executorMemory: Optional amount of executor memory, if not provided the created job will have 1 Gigabyt of executor memory as default value
    * @param numExecutors: Optional number of executors, if not provided the created job will have 1 executor as default value
    * @param logLevel: Optional value of log leve, if not provided the created job will have LogLevel.INFO as default value
    * @param conf: Optional configuration map, if not provided the configuration will have dex.safariEnabled as config
    * @param jars: Optional jar dependencies
    * @return SparkJob
    */
  def defaultSparkJob(
    file: String,
    className: String,
    args: Option[Seq[String]] = None,
    driverCores: Option[Int] = None,
    driverMemory: Option[String] = None,
    executorCores: Option[Int] = None,
    executorMemory: Option[String] = None,
    numExecutors: Option[Int] = None,
    logLevel: Option[String] = None,
    conf: Option[Map[String, String]] = None,
    jars: Option[Seq[String]] = None
  ): SparkJob =
    SparkJob(
      file = file,
      driverCores = driverCores.getOrElse(DEFAULT_DRIVER_CORES),
      driverMemory = driverMemory.getOrElse(DEFAULT_DRIVER_MEMORY),
      executorCores = executorCores.getOrElse(DEFAULT_EXECUTOR_CORES),
      executorMemory = executorMemory.getOrElse(DEFAULT_EXECUTOR_MEMORY),
      logLevel = logLevel.orElse(Some(DEFAULT_LOG_LEVEL)),
      numExecutors = numExecutors.orElse(Some(DEFAULT_NUM_EXECUTOR)),
      className = Some(className),
      args = args,
      conf = Some(DEX_BASE_CONFIG ++ conf.getOrElse(Map.empty[String, String])),
      jars = jars,
      proxyUser = None,
      pythonEnvResourceName = None,
      pyFiles = None
    )

  /** Create a default PysparkJob instance
    * @param file: jar file path
    * @param args: Optional sequence of arguments
    * @param driverCores: Optional number of driver cores, if not provided the created job will have 1 driver cores as default value
    * @param driverMemory: Optional amount of driver memory, if not provided the created job will have 1 Gigabyte of driver memory as default value
    * @param executorCores: Optional number of executor cores, if not provided the created job will have 1 executor cores as default value
    * @param executorMemory: Optional amount of executor memory, if not provided the created job will have 1 Gigabyt of executor memory as default value
    * @param numExecutors: Optional number of executors, if not provided the created job will have 1 executor as default value
    * @param logLevel: Optional value of log leve, if not provided the created job will have LogLevel.INFO as default value
    * @param conf: Optional configuration map, if not provided the configuration will have dex.safariEnabled as config
    * @param pythonEnvResourceName: Optional python environment resource
    * @param pyFiles: Optional python dependencies (.egg, .py, .zip)
    * @return SparkJob
    */
  def defaultPysparkJob(
    file: String,
    args: Option[Seq[String]] = None,
    driverCores: Option[Int] = None,
    driverMemory: Option[String] = None,
    executorCores: Option[Int] = None,
    executorMemory: Option[String] = None,
    numExecutors: Option[Int] = None,
    logLevel: Option[String] = None,
    conf: Option[Map[String, String]] = None,
    pythonEnvResourceName: Option[String] = None,
    pyFiles: Option[Seq[String]] = None
  ): SparkJob =
    SparkJob(
      file = file,
      driverCores = driverCores.getOrElse(DEFAULT_DRIVER_CORES),
      driverMemory = driverMemory.getOrElse(DEFAULT_DRIVER_MEMORY),
      executorCores = executorCores.getOrElse(DEFAULT_EXECUTOR_CORES),
      executorMemory = executorMemory.getOrElse(DEFAULT_EXECUTOR_MEMORY),
      logLevel = logLevel.orElse(Some(DEFAULT_LOG_LEVEL)),
      numExecutors = numExecutors.orElse(Some(DEFAULT_NUM_EXECUTOR)),
      className = None,
      args = args,
      conf = Some(DEX_BASE_CONFIG ++ conf.getOrElse(Map.empty[String, String])),
      jars = None,
      proxyUser = None,
      pythonEnvResourceName = pythonEnvResourceName,
      pyFiles = pyFiles
    )

}
