package partition_clean

import partition_clean.ArgumentParser
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ArgumentParserSpec extends AnyFlatSpec with Matchers {

  "ArgumentParser" should "parse default arguments correctly" in {
    val args = Array.empty[String]
    val config = ArgumentParser.parseArgs(args)
    
    config.tableName shouldBe "sales_data"
    config.cutoffYear shouldBe 2023
    config.dryRun shouldBe true
    config.partitionColumn shouldBe "year"
  }

  it should "parse custom arguments correctly" in {
    val args = Array(
      "--table", "test_table",
      "--cutoff-year", "2022",
      "--dry-run", "false",
      "--partition-column", "date_year"
    )
    val config = ArgumentParser.parseArgs(args)
    
    config.tableName shouldBe "test_table"
    config.cutoffYear shouldBe 2022
    config.dryRun shouldBe false
    config.partitionColumn shouldBe "date_year"
  }

  it should "handle partial arguments with defaults" in {
    val args = Array("--table", "custom_table", "--dry-run", "false")
    val config = ArgumentParser.parseArgs(args)
    
    config.tableName shouldBe "custom_table"
    config.cutoffYear shouldBe 2023 // default
    config.dryRun shouldBe false
    config.partitionColumn shouldBe "year" // default
  }
}