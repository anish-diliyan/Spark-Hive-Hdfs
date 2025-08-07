package cluster_test

import org.apache.spark.sql.SparkSession
import com.typesafe.scalalogging.LazyLogging

class HDFSTest extends ClusterTest with LazyLogging {
  
  override def testName: String = "HDFS Operations"
  
  override def test(spark: SparkSession, recordCount: Int, verbose: Boolean): Unit = {
    logger.info("Testing HDFS operations...")
    
    val sparkSession = spark
    import sparkSession.implicits._
    
    val testData = (1 to recordCount).map(i => (i, s"record_$i", i * 1.5))
    val df = testData.toDF("id", "name", "value")
    
    val hdfsPath = "hdfs://name_node:9000/test/cluster_test.parquet"
    
    // Write to HDFS
    df.write.mode("overwrite").parquet(hdfsPath)
    
    // Read from HDFS
    val readDf = spark.read.parquet(hdfsPath)
    val count = readDf.count()
    
    if (verbose) {
      logger.info(s"Wrote $recordCount records to HDFS")
      logger.info(s"Read back $count records from HDFS")
    }
    
    if (count == recordCount) {
      logger.info("✅ HDFS operations test PASSED")
    } else {
      throw new RuntimeException(s"HDFS test failed: expected $recordCount, got $count")
    }
  }
}