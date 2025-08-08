package small_file_compaction

import org.apache.spark.sql.SparkSession
import common.SparkSessionManager
import org.slf4j.LoggerFactory

object SmallFileCompactionTask extends App {
  private val logger = LoggerFactory.getLogger(getClass)
  
  val config = ArgumentParser.parseArgs(args)
  
  val spark = SparkSessionManager.createSparkSession("SmallFileCompaction")
  val compactor = new SmallFileCompactor(spark)
  
  try {
    config.mode match {
      case "analyze" => compactor.analyzeDirectory(config)
      case "compact" => compactor.compactFiles(config)
      case _ => 
        logger.error(s"Unknown mode: ${config.mode}")
        sys.exit(1)
    }
  } catch {
    case e: Exception =>
      logger.error("Small file compaction failed", e)
      sys.exit(1)
  } finally {
    spark.stop()
  }
}