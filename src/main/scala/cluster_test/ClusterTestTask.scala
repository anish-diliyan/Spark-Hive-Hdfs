package cluster_test

import com.typesafe.scalalogging.LazyLogging
import common.{SparkSessionManager, ArgumentUtils}
import org.apache.spark.sql.SparkSession

object ClusterTestTask extends App with LazyLogging {
  
  private val tests = Map(
    "spark" -> new SparkTest(),
    "hdfs" -> new HDFSTest(),
    "hive" -> new HiveTest(),
    "performance" -> new PerformanceTest()
  )
  
  val spark = SparkSessionManager.createSparkSession("Cluster-Test-Task")
  
  try {
    val argMap = ArgumentUtils.parseArgs(args)
    
    val testType = ArgumentUtils.getArgOrDefault(argMap, "test-type", "all")
    val recordCount = ArgumentUtils.getArgOrDefault(argMap, "record-count", "10000").toInt
    val verbose = ArgumentUtils.getArgOrDefault(argMap, "verbose", "false").toBoolean
    
    logger.info("=== Cluster Test Task ===")
    logger.info(s"Test Type: $testType")
    logger.info(s"Record Count: $recordCount")
    logger.info(s"Verbose: $verbose")
    
    testType match {
      case "all" => runAllTests(spark, recordCount, verbose)
      case testName if tests.contains(testName) => 
        runSingleTest(tests(testName), spark, recordCount, verbose)
      case _ => 
        logger.error(s"Unknown test type: $testType")
        logger.info(s"Available tests: ${tests.keys.mkString(", ")}, all")
        sys.exit(1)
    }
    
    logger.info("Cluster test completed successfully")
    
  } catch {
    case e: Exception =>
      logger.error("Cluster test failed", e)
      sys.exit(1)
  } finally {
    SparkSessionManager.stopSparkSession(spark)
  }
  
  private def runSingleTest(test: ClusterTest, spark: SparkSession, recordCount: Int, verbose: Boolean): Unit = {
    logger.info(s"Running ${test.testName} test...")
    test.test(spark, recordCount, verbose)
  }
  
  private def runAllTests(spark: SparkSession, recordCount: Int, verbose: Boolean): Unit = {
    logger.info("Running all cluster tests...")
    
    tests.values.foreach { test =>
      runSingleTest(test, spark, recordCount, verbose)
    }
    
    logger.info("🎉 All cluster tests PASSED!")
  }
  

}