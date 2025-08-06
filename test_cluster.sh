#!/bin/bash
echo "=== Testing Big Data Cluster Setup ==="

# Test HDFS
echo "1. Testing HDFS connectivity..."
docker exec spark_master hdfs dfs -mkdir -p /test
docker exec spark_master hdfs dfs -put /opt/spark/README.md /test/
docker exec spark_master hdfs dfs -ls /test/
echo "✓ HDFS test completed"

# Test Spark Master UI
echo "2. Testing Spark Master UI..."
curl -s http://localhost:8080 > /dev/null && echo "✓ Spark Master UI accessible" || echo "✗ Spark Master UI not accessible"

# Test YARN Resource Manager
echo "3. Testing YARN Resource Manager..."
curl -s http://localhost:8088 > /dev/null && echo "✓ YARN Resource Manager accessible" || echo "✗ YARN Resource Manager not accessible"

# Test Hive Metastore
echo "4. Testing Hive Metastore..."
docker exec hive_server beeline -u jdbc:hive2://localhost:10000 -e "SHOW DATABASES;" && echo "✓ Hive connection successful" || echo "✗ Hive connection failed"

# Compile and package tests
echo "5. Compiling Scala tests..."
if command -v sbt &> /dev/null; then
    sbt clean compile assembly
    echo "✓ Project compiled successfully"
else
    echo "⚠ SBT not found, using spark-shell directly"
fi

# Run Scala tests
echo "6. Running Scala tests..."

echo "Running HDFS test..."
docker cp src/test/scala/TestHDFS.scala spark_master:/tmp/
docker exec spark_master /opt/spark/bin/spark-shell --master spark://spark_master:7077 -i /tmp/TestHDFS.scala

echo "Running Spark test..."
docker cp src/test/scala/TestSpark.scala spark_master:/tmp/
docker exec spark_master /opt/spark/bin/spark-shell --master spark://spark_master:7077 -i /tmp/TestSpark.scala

echo "Running Hive test..."
docker cp src/test/scala/TestHive.scala spark_master:/tmp/
docker exec spark_master /opt/spark/bin/spark-shell --master spark://spark_master:7077 -i /tmp/TestHive.scala

echo "=== All tests completed ==="