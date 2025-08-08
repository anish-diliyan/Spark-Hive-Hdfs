package small_file_compaction

import org.apache.spark.sql.SparkSession
import common.TaskRunner

object SmallFileCompactionTask extends App with TaskRunner {
  
  runTask("SmallFileCompaction", args)
  
  protected def executeTask(spark: SparkSession, args: Array[String]): Unit = {
    val config = ArgumentParser.parseArgs(args)
    val compactor = new SmallFileCompactor(spark)
    
    config.mode match {
      case "analyze" => compactor.analyzeDirectory(config)
      case "compact" => compactor.compactFiles(config)
      case _ => 
        logger.error(s"Unknown mode: ${config.mode}")
        throw new IllegalArgumentException(s"Unknown mode: ${config.mode}")
    }
  }
}