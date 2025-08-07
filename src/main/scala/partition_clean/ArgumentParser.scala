package partition_clean

case class PartitionCleanupConfig(
  tableName: String,
  cutoffYear: Int,
  dryRun: Boolean,
  partitionColumn: String
)

object ArgumentParser {
  
  def parseArgs(args: Array[String]): PartitionCleanupConfig = {
    val argMap = args.grouped(2).collect {
      case Array(key, value) if key.startsWith("--") => key.substring(2) -> value
    }.toMap
    
    PartitionCleanupConfig(
      tableName = getArgOrDefault(argMap, "table", "sales_data"),
      cutoffYear = getArgOrDefault(argMap, "cutoff-year", "2023").toInt,
      dryRun = getArgOrDefault(argMap, "dry-run", "true").toBoolean,
      partitionColumn = getArgOrDefault(argMap, "partition-column", "year")
    )
  }
  
  private def getArgOrDefault(args: Map[String, String], key: String, default: String): String = {
    args.getOrElse(key, default)
  }
}