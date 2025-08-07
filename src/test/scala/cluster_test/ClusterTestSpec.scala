package cluster_test

import cluster_test._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ClusterTestSpec extends AnyFlatSpec with Matchers {

  "SparkTest" should "have correct test name" in {
    val sparkTest = new SparkTest()
    sparkTest.testName shouldBe "Spark Functionality"
  }

  "HDFSTest" should "have correct test name" in {
    val hdfsTest = new HDFSTest()
    hdfsTest.testName shouldBe "HDFS Operations"
  }

  "HiveTest" should "create test name correctly" in {
    val hiveTest = new HiveTest()
    hiveTest.testName shouldBe "Hive Operations"
  }

  "PerformanceTest" should "create test name correctly" in {
    val performanceTest = new PerformanceTest()
    performanceTest.testName shouldBe "Cluster Performance"
  }

  "ClusterTest implementations" should "have unique test names" in {
    val tests = Seq(
      new SparkTest(),
      new HDFSTest(), 
      new HiveTest(),
      new PerformanceTest()
    )
    
    val testNames = tests.map(_.testName)
    testNames.distinct.length shouldBe testNames.length
  }
}