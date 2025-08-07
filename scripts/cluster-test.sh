#!/bin/bash

# Cluster Test Script
# Usage: ./scripts/cluster-test.sh [options]

TEST_TYPE="all"
RECORD_COUNT="10000"
VERBOSE="false"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --test-type)
      TEST_TYPE="$2"
      shift 2
      ;;
    --record-count)
      RECORD_COUNT="$2"
      shift 2
      ;;
    --verbose)
      VERBOSE="$2"
      shift 2
      ;;
    -h|--help)
      echo "Usage: $0 [options]"
      echo "Options:"
      echo "  --test-type TYPE        Test type: spark, hdfs, hive, performance, all (default: all)"
      echo "  --record-count COUNT    Number of test records (default: 10000)"
      echo "  --verbose true/false    Verbose output (default: false)"
      echo ""
      echo "Examples:"
      echo "  $0 --test-type all --record-count 10000 --verbose true"
      echo "  $0 --test-type spark --record-count 5000"
      echo "  $0 --test-type performance --record-count 50000"
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      exit 1
      ;;
  esac
done

echo "=== Cluster Test ==="
echo "Test Type: $TEST_TYPE"
echo "Record Count: $RECORD_COUNT"
echo "Verbose: $VERBOSE"

# Get script directory and project root
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$SCRIPT_DIR/.." && pwd )"

# Change to project root
cd "$PROJECT_ROOT"
echo "Working from: $(pwd)"

# Build JAR if needed
if [ ! -f "target/scala-2.12/spark-hive-hdfs-assembly-1.0.jar" ]; then
    echo "Building JAR..."
    sbt assembly
fi

# Copy JAR to container
echo "Copying JAR to Spark container..."
docker cp "$PROJECT_ROOT/target/scala-2.12/spark-hive-hdfs-assembly-1.0.jar" spark_master:/tmp/

# Run cluster test task
echo "Running cluster test task..."
docker exec spark_master /spark/bin/spark-submit \
  --class cluster_test.ClusterTestTask \
  --master spark://spark_master:7077 \
  --conf spark.sql.warehouse.dir=hdfs://name_node:9000/user/hive/warehouse \
  --conf hive.metastore.uris=thrift://hive_metastore:9083 \
  /tmp/spark-hive-hdfs-assembly-1.0.jar \
  --test-type "$TEST_TYPE" \
  --record-count "$RECORD_COUNT" \
  --verbose "$VERBOSE"

echo "=== Cluster test completed ==="