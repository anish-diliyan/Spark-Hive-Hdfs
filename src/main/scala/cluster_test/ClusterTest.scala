package cluster_test

import org.apache.spark.sql.SparkSession

trait ClusterTest {
  def test(spark: SparkSession, recordCount: Int, verbose: Boolean): Unit
  def testName: String
}