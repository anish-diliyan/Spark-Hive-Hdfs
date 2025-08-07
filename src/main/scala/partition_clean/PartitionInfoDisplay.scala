package partition_clean

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import com.typesafe.scalalogging.LazyLogging

class PartitionInfoDisplay(spark: SparkSession) extends LazyLogging {
  
  def showPartitionInfo(tableName: String): Unit = {
    logger.info(s"Partition information for table: $tableName")
    
    try {
      val partitions = spark.sql(s"SHOW PARTITIONS $tableName")
      partitions.show(false)
      
      val df = spark.table(tableName)
      df.groupBy("year")
        .agg(count("*").alias("record_count"))
        .orderBy("year")
        .show()
        
    } catch {
      case e: Exception =>
        logger.error(s"Error showing partition info: ${e.getMessage}")
    }
  }
}