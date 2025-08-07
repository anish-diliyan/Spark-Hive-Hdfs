package cluster_test

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import com.typesafe.scalalogging.LazyLogging

class PerformanceTest extends ClusterTest with LazyLogging {
  
  override def testName: String = "Cluster Performance"
  
  override def test(spark: SparkSession, recordCount: Int, verbose: Boolean): Unit = {
    logger.info("Testing cluster performance...")
    
    val sparkSession = spark
    import sparkSession.implicits._
    
    val startTime = System.currentTimeMillis()
    
    val largeData = (1 to recordCount).map { i =>
      (i, s"user_$i", i % 10, s"category_${i % 5}", i * 1.5)
    }
    val df = largeData.toDF("id", "name", "group_id", "category", "value")
    
    val result = df
      .filter($"value" > recordCount / 10)
      .groupBy($"category")
      .agg(count("*").alias("count"), avg("value").alias("avg_value"))
      .collect()
    
    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    
    if (verbose) {
      logger.info(s"Processed $recordCount records in ${executionTime}ms")
      logger.info(s"Result categories: ${result.length}")
    }
    
    val maxTime = if (recordCount > 50000) 30000L else 10000L
    
    if (executionTime < maxTime) {
      logger.info(s"✅ Performance test PASSED (${executionTime}ms < ${maxTime}ms)")
    } else {
      throw new RuntimeException(s"Performance test failed: ${executionTime}ms > ${maxTime}ms")
    }
  }
}