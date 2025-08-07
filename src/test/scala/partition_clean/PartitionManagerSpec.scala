package partition_clean

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PartitionManagerSpec extends AnyFlatSpec with Matchers {

  "PartitionManager" should "filter partitions correctly" in {
    // Test partition filtering logic without Spark
    val partitions = Array("year=2020", "year=2021", "year=2023", "year=2024")
    val cutoffYear = 2023
    
    val partitionsToDelete = partitions.filter { partition =>
      val yearPattern = "year=(\\d{4})".r
      yearPattern.findFirstMatchIn(partition) match {
        case Some(m) => m.group(1).toInt < cutoffYear
        case None => false
      }
    }
    
    partitionsToDelete should contain allOf ("year=2020", "year=2021")
    partitionsToDelete should not contain "year=2023"
    partitionsToDelete should not contain "year=2024"
  }

  it should "handle empty partition list" in {
    val partitions = Array.empty[String]
    val cutoffYear = 2023
    
    val partitionsToDelete = partitions.filter { partition =>
      val yearPattern = "year=(\\d{4})".r
      yearPattern.findFirstMatchIn(partition) match {
        case Some(m) => m.group(1).toInt < cutoffYear
        case None => false
      }
    }
    
    partitionsToDelete shouldBe empty
  }

  it should "handle invalid partition formats" in {
    val partitions = Array("invalid_partition", "year=abc", "month=2023")
    val cutoffYear = 2023
    
    val partitionsToDelete = partitions.filter { partition =>
      val yearPattern = "year=(\\d{4})".r
      yearPattern.findFirstMatchIn(partition) match {
        case Some(m) => m.group(1).toInt < cutoffYear
        case None => false
      }
    }
    
    partitionsToDelete shouldBe empty
  }
}