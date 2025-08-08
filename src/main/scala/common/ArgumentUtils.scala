package common

object ArgumentUtils {
  
  def parseArgs(args: Array[String]): Map[String, String] = {
    args.grouped(2).collect {
      case Array(key, value) if key.startsWith("--") => key.substring(2) -> value
    }.toMap
  }
  
  def getArgOrDefault(args: Map[String, String], key: String, default: String): String = {
    args.getOrElse(key, default)
  }
  
  def getArgOrExit(args: Map[String, String], key: String, errorMsg: String): String = {
    args.getOrElse(key, {
      println(s"Error: $errorMsg")
      sys.exit(1)
    })
  }
  
  def printUsageAndExit(usage: String): Unit = {
    println(usage)
    sys.exit(0)
  }
}