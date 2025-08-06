package com.bigdata.performance

import com.bigdata.base.BaseSpec
import org.apache.spark.sql.functions._
import scala.util.Random

class PerformanceSpec extends BaseSpec {
  
  override def beforeAll(): Unit = {
    logger.info("Starting performance tests")
    spark = createSparkSession("Performance Test")
  }
  
  "Spark performance" should "handle large dataset efficiently" in {
    val sparkSession = spark
    import sparkSession.implicits._
    
    val startTime = System.currentTimeMillis()
    
    // Generate large dataset
    val largeData = (1 to 100000).map(i => (i, Random.nextDouble() * 1000, s"user_$i"))
    val df = largeData.toDF("id", "value", "name")
    
    // Perform aggregations
    val result = df
      .filter(col("value") > 500)
      .groupBy(col("id") % 10 as "bucket")
      .agg(
        count("*") as "count",
        avg("value") as "avg_value",
        max("value") as "max_value"
      )
      .collect()
    
    val endTime = System.currentTimeMillis()
    val duration = endTime - startTime
    
    logger.info(s"Large dataset processing took ${duration}ms")
    
    result.length shouldBe 10
    duration should be < 30000L // Should complete within 30 seconds
  }
  
  it should "efficiently write and read parquet files" in {
    val sparkSession = spark
    import sparkSession.implicits._
    
    val testData = (1 to 50000).map(i => (i, s"name_$i", Random.nextDouble() * 100))
    val df = testData.toDF("id", "name", "score")
    
    val writePath = s"${config.getString("hdfs.namenode")}/perf_test/data.parquet"
    
    val writeStart = System.currentTimeMillis()
    df.write.mode("overwrite").parquet(writePath)
    val writeEnd = System.currentTimeMillis()
    
    val readStart = System.currentTimeMillis()
    val readDf = spark.read.parquet(writePath)
    val count = readDf.count()
    val readEnd = System.currentTimeMillis()
    
    val writeTime = writeEnd - writeStart
    val readTime = readEnd - readStart
    
    logger.info(s"Write time: ${writeTime}ms, Read time: ${readTime}ms")
    
    count shouldBe 50000L
    writeTime should be < 15000L
    readTime should be < 10000L
  }
}