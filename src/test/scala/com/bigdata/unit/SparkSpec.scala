package com.bigdata.unit

import com.bigdata.base.BaseSpec
import org.apache.spark.sql.functions._

class SparkSpec extends BaseSpec {
  
  override def beforeAll(): Unit = {
    logger.info("Starting Spark cluster tests")
    spark = createSparkSession("Spark Cluster Test")
  }
  
  "Spark cluster" should "perform distributed computation correctly" in {
    val sparkSession = spark
    import sparkSession.implicits._
    
    val data = (1 to 1000).map(i => (i, i * 2))
    val df = data.toDF("id", "value")
    
    val result = df.agg(sum("value").alias("total")).collect()(0).getLong(0)
    result shouldBe 1001000L
  }
  
  it should "partition data across multiple partitions" in {
    val sparkSession = spark
    import sparkSession.implicits._
    
    val data = (1 to 100).map(i => (i, i * 2))
    val df = data.toDF("id", "value").repartition(4)
    
    df.rdd.getNumPartitions shouldBe 4
  }
}