package cluster_test

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import com.typesafe.scalalogging.LazyLogging

class SparkTest extends ClusterTest with LazyLogging {
  
  override def testName: String = "Spark Functionality"
  
  override def test(spark: SparkSession, recordCount: Int, verbose: Boolean): Unit = {
    logger.info("Testing Spark functionality...")
    
    val sparkSession = spark
    import sparkSession.implicits._
    
    val data = (1 to recordCount).map(i => (i, i * 2, s"user_$i"))
    val df = data.toDF("id", "value", "name")
    
    val result = df.agg(sum("value").alias("total")).collect()(0).getLong(0)
    val expected = recordCount.toLong * (recordCount + 1)
    
    if (verbose) {
      logger.info(s"Created DataFrame with $recordCount records")
      logger.info(s"Sum result: $result, Expected: $expected")
    }
    
    if (result == expected) {
      logger.info("✅ Spark functionality test PASSED")
    } else {
      throw new RuntimeException(s"Spark test failed: expected $expected, got $result")
    }
  }
}