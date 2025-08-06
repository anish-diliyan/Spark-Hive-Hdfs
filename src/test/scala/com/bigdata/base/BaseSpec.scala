package com.bigdata.base

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.SparkSession
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

abstract class BaseSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll with LazyLogging {
  
  protected val config: Config = ConfigFactory.load()
  protected var spark: SparkSession = _
  
  protected def createSparkSession(appName: String, enableHive: Boolean = false): SparkSession = {
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
  
  override def afterAll(): Unit = {
    if (spark != null) {
      logger.info("Stopping Spark session")
      spark.stop()
    }
  }
}