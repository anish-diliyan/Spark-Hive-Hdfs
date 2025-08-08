package partition_clean

import common.ArgumentUtils

case class PartitionCleanupConfig(
  tableName: String,
  cutoffYear: Int,
  dryRun: Boolean,
  partitionColumn: String
)

object ArgumentParser {
  
  def parseArgs(args: Array[String]): PartitionCleanupConfig = {
    val argMap = ArgumentUtils.parseArgs(args)
    
    PartitionCleanupConfig(
      tableName = ArgumentUtils.getArgOrDefault(argMap, "table", "sales_data"),
      cutoffYear = ArgumentUtils.getArgOrDefault(argMap, "cutoff-year", "2023").toInt,
      dryRun = ArgumentUtils.getArgOrDefault(argMap, "dry-run", "true").toBoolean,
      partitionColumn = ArgumentUtils.getArgOrDefault(argMap, "partition-column", "year")
    )
  }
}