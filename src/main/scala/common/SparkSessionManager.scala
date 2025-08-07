package common

import org.apache.spark.sql.SparkSession
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

object SparkSessionManager extends LazyLogging {
  
  private val config: Config = ConfigFactory.load()
  
  def createSparkSession(appName: String, enableHive: Boolean = true): SparkSession = {
    logger.info(s"Creating Spark session for: $appName")
    
    val builder = SparkSession.builder()
      .appName(appName)
      .master(config.getString("spark.master"))
      .config("spark.sql.warehouse.dir", config.getString("spark.warehouse.dir"))
    
    if (enableHive) {
      builder
        .config("hive.metastore.uris", config.getString("hive.metastore.uri"))
        .enableHiveSupport()
    }
    
    builder.getOrCreate()
  }
  
  def stopSparkSession(spark: SparkSession): Unit = {
    if (spark != null) {
      logger.info("Stopping Spark session")
      spark.stop()
    }
  }
}