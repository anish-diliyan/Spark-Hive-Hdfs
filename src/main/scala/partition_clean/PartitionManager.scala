package partition_clean

import org.apache.spark.sql.SparkSession

import com.typesafe.scalalogging.LazyLogging

class PartitionManager(spark: SparkSession) extends LazyLogging {
  
  def removeOldPartitions(
    tableName: String, 
    cutoffYear: Int, 
    partitionColumn: String,
    dryRun: Boolean
  ): Unit = {
    
    logger.info(s"Starting partition cleanup for table: $tableName, cutoff year: $cutoffYear")
    
    try {
      val partitions = spark.sql(s"SHOW PARTITIONS $tableName").collect()
      
      val partitionsToDelete = partitions
        .map(_.getString(0))
        .filter(partition => {
          val yearPattern = s"$partitionColumn=(\\d{4})".r
          yearPattern.findFirstMatchIn(partition) match {
            case Some(m) => m.group(1).toInt < cutoffYear
            case None => false
          }
        })
      
      if (partitionsToDelete.isEmpty) {
        logger.info(s"No partitions found older than $cutoffYear")
        return
      }
      
      logger.info(s"Found ${partitionsToDelete.length} partitions to delete:")
      partitionsToDelete.foreach(p => logger.info(s"  - $p"))
      
      if (dryRun) {
        logger.info("DRY RUN: No actual deletion performed")
      } else {
        partitionsToDelete.foreach { partition =>
          val dropSql = s"ALTER TABLE $tableName DROP PARTITION ($partition)"
          logger.info(s"Executing: $dropSql")
          spark.sql(dropSql)
        }
        logger.info(s"Successfully deleted ${partitionsToDelete.length} partitions")
      }
      
    } catch {
      case e: Exception =>
        logger.error(s"Error during partition cleanup: ${e.getMessage}", e)
        throw e
    }
  }
}