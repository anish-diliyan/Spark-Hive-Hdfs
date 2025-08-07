package cluster_test

import org.apache.spark.sql.SparkSession
import com.typesafe.scalalogging.LazyLogging

class HiveTest extends ClusterTest with LazyLogging {
  
  override def testName: String = "Hive Operations"
  
  override def test(spark: SparkSession, recordCount: Int, verbose: Boolean): Unit = {
    logger.info("Testing Hive operations...")
    
    val sparkSession = spark
    import sparkSession.implicits._
    
    val testData = (1 to recordCount).map(i => (i, s"product_$i", i % 5, i * 10.0))
    val df = testData.toDF("id", "product", "category", "price")
    
    val tableName = "cluster_test_table"
    
    // Create Hive table
    df.write.mode("overwrite").partitionBy("category").saveAsTable(tableName)
    
    // Query the table
    val count = spark.sql(s"SELECT COUNT(*) as count FROM $tableName").collect()(0).getLong(0)
    val avgPrice = spark.sql(s"SELECT AVG(price) as avg_price FROM $tableName").collect()(0).getDouble(0)
    
    if (verbose) {
      logger.info(s"Created Hive table with $recordCount records")
      logger.info(s"Table count: $count, Average price: $avgPrice")
    }
    
    if (count == recordCount) {
      logger.info("✅ Hive operations test PASSED")
    } else {
      throw new RuntimeException(s"Hive test failed: expected $recordCount, got $count")
    }
  }
}