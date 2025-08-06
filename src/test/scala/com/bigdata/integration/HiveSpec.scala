package com.bigdata.integration

import com.bigdata.base.BaseSpec

class HiveSpec extends BaseSpec {
  
  override def beforeAll(): Unit = {
    logger.info("Starting Hive integration tests")
    spark = createSparkSession("Hive Test", enableHive = true)
  }
  
  "Hive integration" should "create database and table successfully" in {
    noException should be thrownBy {
      spark.sql("CREATE DATABASE IF NOT EXISTS test_db")
      spark.sql("USE test_db")
      spark.sql("""
        CREATE TABLE IF NOT EXISTS employees_spec (
          id INT,
          name STRING,
          department STRING,
          salary DOUBLE
        ) USING HIVE
      """)
    }
  }
  
  it should "insert and query data correctly" in {
    spark.sql("USE test_db")
    
    spark.sql("""
      INSERT OVERWRITE TABLE employees_spec VALUES
      (1, 'John', 'Engineering', 75000.0),
      (2, 'Jane', 'Marketing', 65000.0),
      (3, 'Mike', 'Engineering', 80000.0)
    """)
    
    val result = spark.sql("SELECT COUNT(*) as count FROM employees_spec").collect()(0).getLong(0)
    result shouldBe 3L
    
    val avgSalary = spark.sql("SELECT AVG(salary) as avg_salary FROM employees_spec WHERE department = 'Engineering'")
      .collect()(0).getDouble(0)
    avgSalary shouldBe 77500.0
  }
}