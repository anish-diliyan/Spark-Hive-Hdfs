package small_file_compaction

import org.apache.spark.sql.SparkSession
import org.apache.hadoop.fs.{FileSystem, Path, FileStatus}
import org.slf4j.LoggerFactory
import scala.collection.mutable.ArrayBuffer

class SmallFileCompactor(spark: SparkSession) {
  private val logger = LoggerFactory.getLogger(getClass)
  private val hadoopConf = spark.sparkContext.hadoopConfiguration
  private val fs = FileSystem.get(hadoopConf)
  
  def analyzeDirectory(config: CompactionConfig): Unit = {
    logger.info(s"Analyzing directory: ${config.inputPath}")
    
    val files = listFiles(new Path(config.inputPath))
    val thresholdBytes = config.thresholdMB * 1024 * 1024L
    
    val smallFiles = files.filter(_.getLen < thresholdBytes)
    val totalFiles = files.length
    val totalSize = files.map(_.getLen).sum
    val smallFilesSize = smallFiles.map(_.getLen).sum
    
    println(s"=== Small File Analysis Report ===")
    println(s"Total files: $totalFiles")
    println(s"Small files (< ${config.thresholdMB}MB): ${smallFiles.length}")
    println(s"Small files percentage: ${(smallFiles.length.toDouble / totalFiles * 100).formatted("%.2f")}%")
    println(s"Total size: ${formatBytes(totalSize)}")
    println(s"Small files size: ${formatBytes(smallFilesSize)}")
    println(s"Potential compacted files: ${Math.ceil(smallFilesSize.toDouble / (config.targetSizeMB * 1024 * 1024)).toInt}")
    println(s"NameNode memory savings: ${smallFiles.length - Math.ceil(smallFilesSize.toDouble / (config.targetSizeMB * 1024 * 1024)).toInt} file objects")
  }
  
  def compactFiles(config: CompactionConfig): Unit = {
    logger.info(s"Starting file compaction for: ${config.inputPath}")
    
    val files = listFiles(new Path(config.inputPath))
    val thresholdBytes = config.thresholdMB * 1024 * 1024L
    val targetBytes = config.targetSizeMB * 1024 * 1024L
    
    val smallFiles = files.filter(_.getLen < thresholdBytes)
    
    if (smallFiles.isEmpty) {
      logger.info("No small files found to compact")
      return
    }
    
    val outputPath = config.outputPath.getOrElse(config.inputPath)
    val groups = groupFilesBySize(smallFiles, targetBytes)
    
    logger.info(s"Compacting ${smallFiles.length} small files into ${groups.length} groups")
    
    groups.zipWithIndex.foreach { case (group, index) =>
      val outputFile = s"$outputPath/compacted_${System.currentTimeMillis()}_$index"
      compactGroup(group, outputFile)
    }
    
    logger.info("File compaction completed successfully")
  }
  
  private def listFiles(path: Path): Array[FileStatus] = {
    val files = ArrayBuffer[FileStatus]()
    
    def traverse(currentPath: Path): Unit = {
      val status = fs.listStatus(currentPath)
      status.foreach { fileStatus =>
        if (fileStatus.isFile) {
          files += fileStatus
        } else if (fileStatus.isDirectory) {
          traverse(fileStatus.getPath)
        }
      }
    }
    
    traverse(path)
    files.toArray
  }
  
  private def groupFilesBySize(files: Array[FileStatus], targetSize: Long): Array[Array[FileStatus]] = {
    val groups = ArrayBuffer[Array[FileStatus]]()
    val currentGroup = ArrayBuffer[FileStatus]()
    var currentSize = 0L
    
    files.foreach { file =>
      if (currentSize + file.getLen > targetSize && currentGroup.nonEmpty) {
        groups += currentGroup.toArray
        currentGroup.clear()
        currentSize = 0L
      }
      currentGroup += file
      currentSize += file.getLen
    }
    
    if (currentGroup.nonEmpty) {
      groups += currentGroup.toArray
    }
    
    groups.toArray
  }
  
  private def compactGroup(files: Array[FileStatus], outputPath: String): Unit = {
    val inputPaths = files.map(_.getPath.toString)
    
    val df = spark.read.text(inputPaths: _*)
    df.coalesce(1).write.mode("overwrite").text(outputPath)
    
    logger.info(s"Compacted ${files.length} files into $outputPath")
  }
  
  private def formatBytes(bytes: Long): String = {
    val units = Array("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.length - 1) {
      size /= 1024
      unitIndex += 1
    }
    
    f"$size%.2f ${units(unitIndex)}"
  }
}