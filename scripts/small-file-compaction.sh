#!/bin/bash

# Small File Compaction Script
# Addresses HDFS small file problem by merging small files into larger ones

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
JAR_FILE="$PROJECT_ROOT/target/scala-2.12/spark-hive-hdfs-assembly-1.0.jar"

# Default values
INPUT_PATH=""
OUTPUT_PATH=""
THRESHOLD_MB=32
TARGET_SIZE_MB=128
DRY_RUN=false
HELP=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --input-path)
      INPUT_PATH="$2"
      shift 2
      ;;
    --output-path)
      OUTPUT_PATH="$2"
      shift 2
      ;;
    --threshold-mb)
      THRESHOLD_MB="$2"
      shift 2
      ;;
    --target-size-mb)
      TARGET_SIZE_MB="$2"
      shift 2
      ;;
    --dry-run)
      DRY_RUN="$2"
      shift 2
      ;;
    --help)
      HELP=true
      shift
      ;;
    *)
      echo "Unknown option: $1"
      exit 1
      ;;
  esac
done

# Show help
if [ "$HELP" = true ]; then
  echo "Usage: $0 [options]"
  echo ""
  echo "Options:"
  echo "  --input-path <path>      HDFS directory path containing small files to compact"
  echo "  --output-path <path>     Target directory for compacted files (optional)"
  echo "  --threshold-mb <size>    File size threshold in MB to consider as 'small' (default: 32)"
  echo "  --target-size-mb <size>  Target size for compacted files in MB (default: 128)"
  echo "  --dry-run <true/false>   Preview mode to analyze without actual compaction"
  echo "  --help                   Show this help message"
  echo ""
  echo "Examples:"
  echo "  $0 --input-path /data/logs --dry-run true"
  echo "  $0 --input-path /data/logs --output-path /data/compacted"
  echo "  $0 --input-path /data/events --threshold-mb 64 --target-size-mb 256"
  exit 0
fi

# Validate required parameters
if [ -z "$INPUT_PATH" ]; then
  echo "Error: --input-path is required"
  echo "Use --help for usage information"
  exit 1
fi

# Check if JAR file exists
if [ ! -f "$JAR_FILE" ]; then
  echo "Error: JAR file not found at $JAR_FILE"
  echo "Please run 'sbt assembly' to build the project first"
  exit 1
fi

# Build spark-submit command
SPARK_SUBMIT_CMD="spark-submit \
  --class small_file_compaction.SmallFileCompactionTask \
  --master spark://spark_master:7077 \
  --deploy-mode client \
  --executor-memory 2g \
  --executor-cores 2 \
  --num-executors 2 \
  $JAR_FILE \
  --input-path $INPUT_PATH \
  --threshold-mb $THRESHOLD_MB \
  --target-size-mb $TARGET_SIZE_MB \
  --dry-run $DRY_RUN"

# Add optional output path
if [ -n "$OUTPUT_PATH" ]; then
  SPARK_SUBMIT_CMD="$SPARK_SUBMIT_CMD --output-path $OUTPUT_PATH"
fi

echo "Starting HDFS Small File Compaction..."
echo "Input Path: $INPUT_PATH"
echo "Threshold: ${THRESHOLD_MB}MB"
echo "Target Size: ${TARGET_SIZE_MB}MB"
echo "Dry Run: $DRY_RUN"
if [ -n "$OUTPUT_PATH" ]; then
  echo "Output Path: $OUTPUT_PATH"
fi
echo ""

# Execute the command
eval $SPARK_SUBMIT_CMD