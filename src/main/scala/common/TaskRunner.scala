package common

import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

trait TaskRunner {
  protected val logger = LoggerFactory.getLogger(getClass)
  
  def runTask(appName: String, args: Array[String]): Unit = {
    val spark = SparkSessionManager.createSparkSession(appName)
    
    try {
      executeTask(spark, args)
      logger.info(s"$appName completed successfully")
    } catch {
      case e: Exception =>
        logger.error(s"$appName failed", e)
        sys.exit(1)
    } finally {
      SparkSessionManager.stopSparkSession(spark)
    }
  }
  
  protected def executeTask(spark: SparkSession, args: Array[String]): Unit
}