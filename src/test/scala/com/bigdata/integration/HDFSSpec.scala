package com.bigdata.integration

import com.bigdata.base.BaseSpec

class HDFSSpec extends BaseSpec {
  
  override def beforeAll(): Unit = {
    logger.info("Starting HDFS tests")
    spark = createSparkSession("HDFS Test")
  }
  
  "HDFS" should "write and read data successfully" in {
    val sparkSession = spark
    import sparkSession.implicits._
    
    val data = Seq(("Alice", 25), ("Bob", 30), ("Charlie", 35))
    val df = data.toDF("name", "age")
    
    val hdfsPath = s"${config.getString("hdfs.namenode")}/test/users_spec.parquet"
    
    // Write to HDFS
    noException should be thrownBy {
      df.write.mode("overwrite").parquet(hdfsPath)
    }
    
    // Read from HDFS
    val dfRead = spark.read.parquet(hdfsPath)
    dfRead.count() shouldBe 3
    dfRead.columns should contain allOf ("name", "age")
  }
}