package small_file_compaction

import common.ArgumentUtils

case class CompactionConfig(
  inputPath: String = "",
  outputPath: Option[String] = None,
  thresholdMB: Int = 32,
  targetSizeMB: Int = 128,
  dryRun: Boolean = false,
  mode: String = "compact"
)

object ArgumentParser {
  def parseArgs(args: Array[String]): CompactionConfig = {
    val argMap = ArgumentUtils.parseArgs(args)
    
    if (argMap.contains("help")) {
      ArgumentUtils.printUsageAndExit(getUsage())
    }
    
    val inputPath = ArgumentUtils.getArgOrExit(argMap, "input-path", "--input-path is required")
    val dryRun = ArgumentUtils.getArgOrDefault(argMap, "dry-run", "false").toBoolean
    
    CompactionConfig(
      inputPath = inputPath,
      outputPath = argMap.get("output-path"),
      thresholdMB = ArgumentUtils.getArgOrDefault(argMap, "threshold-mb", "32").toInt,
      targetSizeMB = ArgumentUtils.getArgOrDefault(argMap, "target-size-mb", "128").toInt,
      dryRun = dryRun,
      mode = if (dryRun) "analyze" else "compact"
    )
  }
  
  private def getUsage(): String = {
    """
Usage: SmallFileCompactionTask [options]

Options:
  --input-path <path>      HDFS directory path containing small files to compact
  --output-path <path>     Target directory for compacted files (optional)
  --threshold-mb <size>    File size threshold in MB to consider as "small" (default: 32)
  --target-size-mb <size>  Target size for compacted files in MB (default: 128)
  --dry-run <true/false>   Preview mode to analyze without actual compaction
  --help                   Show this help message
    """
  }
}