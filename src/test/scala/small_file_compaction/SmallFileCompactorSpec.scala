package small_file_compaction

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.apache.spark.sql.SparkSession
import common.SparkSessionManager

class SmallFileCompactorSpec extends AnyFlatSpec with Matchers {
  
  "ArgumentParser" should "parse command line arguments correctly" in {
    val args = Array("--input-path", "/test/path", "--threshold-mb", "64", "--dry-run", "true")
    val config = ArgumentParser.parseArgs(args)
    
    config.inputPath shouldBe "/test/path"
    config.thresholdMB shouldBe 64
    config.dryRun shouldBe true
    config.mode shouldBe "analyze"
  }
  
  it should "use default values when arguments are not provided" in {
    val args = Array("--input-path", "/test/path")
    val config = ArgumentParser.parseArgs(args)
    
    config.thresholdMB shouldBe 32
    config.targetSizeMB shouldBe 128
    config.dryRun shouldBe false
    config.mode shouldBe "compact"
  }
  
  "CompactionConfig" should "have correct default values" in {
    val config = CompactionConfig()
    
    config.inputPath shouldBe ""
    config.outputPath shouldBe None
    config.thresholdMB shouldBe 32
    config.targetSizeMB shouldBe 128
    config.dryRun shouldBe false
    config.mode shouldBe "compact"
  }
}