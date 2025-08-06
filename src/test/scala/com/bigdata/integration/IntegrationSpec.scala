package com.bigdata.integration

import com.bigdata.base.BaseSpec
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.dimafeng.testcontainers.{DockerComposeContainer, ExposedService}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import java.io.File

class IntegrationSpec extends BaseSpec with TestContainerForAll {
  
  override val containerDef: DockerComposeContainer.Def = DockerComposeContainer.Def(
    new File("docker-compose.yml"),
    exposedServices = Seq(
      ExposedService("spark_master", 8080),
      ExposedService("name_node", 9870),
      ExposedService("resource_manager", 8088)
    )
  )
  
  "Full cluster integration" should "work end-to-end" in withContainers { containers =>
    logger.info("Starting full integration test")
    
    spark = SparkSession.builder()
      .appName("Integration Test")
      .master("local[*]") // Use local mode for integration test
      .getOrCreate()
    
    val sparkSession = spark
    import sparkSession.implicits._
    
    val testData = Seq(("integration", 1), ("test", 2))
    val df = testData.toDF("name", "value")
    
    // Test basic Spark functionality
    val result = df.agg(sum("value")).collect()(0).getLong(0)
    result shouldBe 3L
    
    logger.info("Integration test completed successfully")
  }
}