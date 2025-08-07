package partition_clean

import com.typesafe.scalalogging.LazyLogging
import common.SparkSessionManager

object PartitionCleanupTask extends App with LazyLogging {
  
  val spark = SparkSessionManager.createSparkSession("Partition-Cleanup-Task")
  
  try {
    val config = ArgumentParser.parseArgs(args)
    
    logger.info("=== Partition Cleanup Task ===")
    logger.info(s"Table: ${config.tableName}")
    logger.info(s"Cutoff Year: ${config.cutoffYear}")
    logger.info(s"Partition Column: ${config.partitionColumn}")
    logger.info(s"Dry Run: ${config.dryRun}")
    
    val partitionInfoDisplay = new PartitionInfoDisplay(spark)
    val partitionManager = new PartitionManager(spark)
    
    // Show current partition state
    logger.info("Current partition state:")
    partitionInfoDisplay.showPartitionInfo(config.tableName)
    
    // Perform cleanup
    partitionManager.removeOldPartitions(
      config.tableName, 
      config.cutoffYear, 
      config.partitionColumn, 
      config.dryRun
    )
    
    // Show final partition state
    if (!config.dryRun) {
      logger.info("Final partition state:")
      partitionInfoDisplay.showPartitionInfo(config.tableName)
    }
    
    logger.info("Partition cleanup completed successfully")
    
  } catch {
    case e: Exception =>
      logger.error("Partition cleanup failed", e)
      sys.exit(1)
  } finally {
    SparkSessionManager.stopSparkSession(spark)
  }
}