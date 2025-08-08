package small_file_compaction

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
    var config = CompactionConfig()
    
    args.sliding(2, 2).foreach {
      case Array("--input-path", value) => config = config.copy(inputPath = value)
      case Array("--output-path", value) => config = config.copy(outputPath = Some(value))
      case Array("--threshold-mb", value) => config = config.copy(thresholdMB = value.toInt)
      case Array("--target-size-mb", value) => config = config.copy(targetSizeMB = value.toInt)
      case Array("--dry-run", value) => config = config.copy(dryRun = value.toBoolean, mode = "analyze")
      case Array("--help", _) => printUsage(); sys.exit(0)
      case _ =>
    }
    
    if (config.inputPath.isEmpty) {
      println("Error: --input-path is required")
      printUsage()
      sys.exit(1)
    }
    
    config
  }
  
  private def printUsage(): Unit = {
    println("""
Usage: SmallFileCompactionTask [options]

Options:
  --input-path <path>      HDFS directory path containing small files to compact
  --output-path <path>     Target directory for compacted files (optional)
  --threshold-mb <size>    File size threshold in MB to consider as "small" (default: 32)
  --target-size-mb <size>  Target size for compacted files in MB (default: 128)
  --dry-run <true/false>   Preview mode to analyze without actual compaction
  --help                   Show this help message
    """)
  }
}