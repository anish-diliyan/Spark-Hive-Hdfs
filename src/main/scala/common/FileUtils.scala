package common

import org.apache.hadoop.fs.{FileSystem, Path, FileStatus}
import org.apache.spark.sql.SparkSession
import scala.collection.mutable.ArrayBuffer

object FileUtils {
  
  def formatBytes(bytes: Long): String = {
    val units = Array("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.length - 1) {
      size /= 1024
      unitIndex += 1
    }
    
    f"$size%.2f ${units(unitIndex)}"
  }
  
  def listFiles(fs: FileSystem, path: Path): Array[FileStatus] = {
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
  
  def getFileSystem(spark: SparkSession): FileSystem = {
    FileSystem.get(spark.sparkContext.hadoopConfiguration)
  }
}