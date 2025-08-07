#!/bin/bash

# Partition Cleanup Script
# Usage: ./scripts/partition-cleanup.sh [options]

TABLE_NAME="sales_data"
CUTOFF_YEAR="2023"
DRY_RUN="true"
PARTITION_COLUMN="year"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --table)
      TABLE_NAME="$2"
      shift 2
      ;;
    --cutoff-year)
      CUTOFF_YEAR="$2"
      shift 2
      ;;
    --dry-run)
      DRY_RUN="$2"
      shift 2
      ;;
    --partition-column)
      PARTITION_COLUMN="$2"
      shift 2
      ;;
    -h|--help)
      echo "Usage: $0 [options]"
      echo "Options:"
      echo "  --table TABLE_NAME           Table name (default: sales_data)"
      echo "  --cutoff-year YEAR          Remove partitions older than this year (default: 2023)"
      echo "  --dry-run true/false        Preview mode (default: true)"
      echo "  --partition-column COLUMN   Partition column name (default: year)"
      echo ""
      echo "Examples:"
      echo "  $0 --table sales_data --cutoff-year 2023 --dry-run true"
      echo "  $0 --table events --cutoff-year 2022 --dry-run false"
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      exit 1
      ;;
  esac
done

echo "=== Partition Cleanup ==="
echo "Table: $TABLE_NAME"
echo "Cutoff Year: $CUTOFF_YEAR"
echo "Dry Run: $DRY_RUN"
echo "Partition Column: $PARTITION_COLUMN"

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

# Run partition cleanup task
echo "Running partition cleanup task..."
docker exec spark_master /spark/bin/spark-submit \
  --class partition_clean.PartitionCleanupTask \
  --master spark://spark_master:7077 \
  --conf spark.sql.warehouse.dir=hdfs://name_node:9000/user/hive/warehouse \
  --conf hive.metastore.uris=thrift://hive_metastore:9083 \
  /tmp/spark-hive-hdfs-assembly-1.0.jar \
  --table "$TABLE_NAME" \
  --cutoff-year "$CUTOFF_YEAR" \
  --dry-run "$DRY_RUN" \
  --partition-column "$PARTITION_COLUMN"

echo "=== Partition cleanup completed ==="