package common

import org.slf4j.LoggerFactory

object ArgumentUtils {
  private val logger = LoggerFactory.getLogger(getClass)
  
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
      logger.error(s"Error: $errorMsg")
      System.err.println(s"Error: $errorMsg")
      sys.exit(1)
    })
  }
  
  def printUsageAndExit(usage: String): Unit = {
    System.out.println(usage)
    sys.exit(0)
  }
}