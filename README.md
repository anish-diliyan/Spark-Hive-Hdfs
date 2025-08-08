<div align="center">

# 🚀 Spark-Hive-HDFS Cluster

*A complete enterprise-grade big data ecosystem setup using Docker Compose with Hadoop HDFS, Apache Spark, and Apache Hive, featuring automated partition management functionality.*

---

</div>

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Prerequisites](#prerequisites)
3. [Installation & Setup](#installation--setup)
4. [Web Interfaces](#web-interfaces)
5. [Docker Services](#docker-services)
6. [Configuration](#configuration)
7. [Tasks](#tasks)

---

## 🏗️ Architecture Overview

The cluster consists of the following components:

- **HDFS**: Distributed file system with NameNode and 2 DataNodes
- **YARN**: Resource manager and node manager for cluster resource management  
- **Spark**: Master and 2 worker nodes for distributed processing
- **Hive**: SQL interface with PostgreSQL metastore

---

## 📋 Prerequisites

Ensure the following software is installed:

- **Docker & Docker Compose**: Latest version
- **SBT (Scala Build Tool)**: Version 1.8+
- **Java**: Version 8 or 11
- **Git**: For cloning the repository

---

## 🚀 Installation & Setup

### Step 1: Clone Repository
```bash
  git clone https://github.com/anish-diliyan/Spark-Hive-Hdfs.git

  cd Spark-Hive-Hdfs
```

### Step 2: Start the Cluster
```bash
  # Start all services (takes 2-3 minutes for full startup)
  docker compose up -d

  # Check all services are running
  docker compose ps

  # Wait for services to be healthy (especially YARN Resource Manager)
  docker logs resource_manager
```

### Step 3: Build the Application
```bash
  # Compile and run tests
  sbt clean compile test

  # Build deployment JAR
  sbt assembly
```

---

## 🌐 Web Interfaces

Once the cluster is running, access these interfaces:

| Service | URL | Description |
|---------|-----|-------------|
| Spark Master | http://localhost:8080 | Spark cluster management |
| HDFS NameNode | http://localhost:9870 | HDFS file system browser |
| Hadoop History Server | http://localhost:8188 | Job history and logs |
| YARN Resource Manager | http://localhost:8088 | Resource management (takes a few minutes to start) |

> **Note**: Hive Server runs on port 10000/10002 but doesn't provide a web UI - use Beeline CLI instead.

---

## 🐳 Docker Services

| Service | Container | Ports | Purpose |
|---------|-----------|-------|---------|
| NameNode | `name_node` | 9870, 9000 | HDFS metadata management |
| DataNode 1 | `data_node_one` | - | HDFS data storage |
| DataNode 2 | `data_node_two` | - | HDFS data storage |
| Resource Manager | `resource_manager` | 8088 | YARN resource management |
| Node Manager | `node_manager` | - | YARN container management |
| Spark Master | `spark_master` | 8080, 7077 | Spark cluster coordination |
| Spark Worker 1 | `spark_worker-one` | 8081 | Spark task execution |
| Spark Worker 2 | `spark_worker_two` | 8082 | Spark task execution |
| History Server | `history_server` | 8188 | Job history tracking |
| Hive Server | `hive_server` | 10000, 10002 | SQL interface |
| Hive Metastore | `hive_metastore` | 9083 | Metadata storage |
| PostgreSQL | `hive_metastore_postgresql` | - | Metastore database |

---

## 🔧 Configuration

### Application Configuration
- **Production**: `src/main/resources/application.conf` (cluster URLs)
- **Test**: `src/test/resources/application.conf` (local URLs)

### Logging Configuration  
- **Production**: `src/main/resources/logback.xml` (INFO level)
- **Test**: `src/test/resources/logback-test.xml` (ERROR/WARN levels)

### Docker Configuration
- **Services**: `docker-compose.yml`
- **Environment**: `docker-compose.env`

---

<div align="center">

### ✅ Cluster Test Task

</div>

The cluster test task validates all components of the big data ecosystem by running comprehensive tests across HDFS, Spark, and Hive services.

#### 🎯 Purpose
- **Component Validation**: Tests HDFS file operations, Spark job execution, and Hive query processing
- **Data Pipeline Testing**: Validates end-to-end data flow from ingestion to processing
- **Performance Benchmarking**: Measures cluster performance with configurable data volumes
- **Health Monitoring**: Ensures all services are properly configured and communicating

#### 💻 Usage
```bash
  # Test all components with default settings
  ./scripts/cluster-test.sh --test-type all --record-count 1000

  # Test specific component
  ./scripts/cluster-test.sh --test-type spark --record-count 500
  ./scripts/cluster-test.sh --test-type hdfs --record-count 100
  ./scripts/cluster-test.sh --test-type hive --record-count 200

  # Get help and see all options
  ./scripts/cluster-test.sh --help
```

#### 📋 Test Types
- **`all`**: Runs comprehensive tests on all cluster components
- **`hdfs`**: Tests HDFS file operations (create, read, write, delete)
- **`spark`**: Tests Spark job submission and execution
- **`hive`**: Tests Hive table operations and SQL queries

#### ⚙️ Parameters
- **`--test-type`**: Specifies which components to test (all, hdfs, spark, hive)
- **`--record-count`**: Number of test records to generate (default: 1000)
- **`--help`**: Displays usage information and available options

#### 📄 Output
The task generates detailed logs showing:
- Test execution status for each component
- Performance metrics (execution time, throughput)
- Error details if any component fails
- Summary report with pass/fail status

---

<div align="center">

### 🧹 Partition Cleanup Task

</div>

The partition cleanup task provides automated management of Hive table partitions, enabling efficient removal of old or obsolete data partitions to optimize storage and query performance.

#### 🎯 Purpose
- **Storage Optimization**: Removes old partitions to free up disk space
- **Performance Enhancement**: Reduces metadata overhead for faster query execution
- **Data Lifecycle Management**: Implements retention policies for time-based data
- **Automated Maintenance**: Provides scheduled cleanup capabilities

#### 💻 Usage
```bash
  # Dry run (preview what will be deleted)
  ./scripts/partition-cleanup.sh --table sales_data --cutoff-year 2023 --dry-run true

  # Actual cleanup (remove partitions older than 2023)
  ./scripts/partition-cleanup.sh --table sales_data --cutoff-year 2023 --dry-run false

  # Cleanup with custom retention period
  ./scripts/partition-cleanup.sh --table user_events --cutoff-days 90 --dry-run false

  # Get help and see all options
  ./scripts/partition-cleanup.sh --help
```

#### 🔄 Cleanup Modes
- **`dry-run`**: Preview mode that shows which partitions would be deleted without actual removal
- **`execute`**: Performs actual partition deletion based on specified criteria
- **`batch`**: Processes multiple tables using configuration file

#### ⚙️ Parameters
- **`--table`**: Target Hive table name for partition cleanup
- **`--cutoff-year`**: Remove partitions older than specified year
- **`--cutoff-days`**: Remove partitions older than specified number of days
- **`--dry-run`**: Enable preview mode (true/false)
- **`--help`**: Displays usage information and available options

#### 🔒 Safety Features
- **Preview Mode**: Always test with dry-run before actual execution
- **Backup Verification**: Ensures data backup exists before deletion
- **Rollback Support**: Maintains metadata for potential partition recovery
- **Logging**: Comprehensive audit trail of all cleanup operations

#### 📄 Output
The task provides detailed information including:
- List of partitions identified for cleanup
- Storage space that will be freed
- Execution status and any errors encountered
- Summary statistics of cleanup operation

---

<div align="center">

### 📦 HDFS Small File Compaction Task

</div>

The HDFS small file compaction task addresses the small file problem in HDFS by merging multiple small files into larger, more efficient files to improve cluster performance and reduce NameNode memory overhead.

#### 🎯 Purpose
- **Performance Optimization**: Reduces NameNode memory usage by decreasing file count
- **Storage Efficiency**: Improves HDFS block utilization and reduces metadata overhead
- **Query Performance**: Enhances MapReduce and Spark job performance by reducing task overhead
- **Cluster Health**: Maintains optimal HDFS performance by managing file fragmentation

#### 💻 Usage
```bash
  # Analyze small files in a directory (dry run)
  ./scripts/small-file-compaction.sh --input-path /data/logs --dry-run true

  # Compact small files with default settings
  ./scripts/small-file-compaction.sh --input-path /data/logs --output-path /data/compacted

  # Compact with custom file size threshold
  ./scripts/small-file-compaction.sh --input-path /data/events --threshold-mb 64 --target-size-mb 256

  # Get help and see all options
  ./scripts/small-file-compaction.sh --help
```

#### 🔧 Compaction Modes
- **`analyze`**: Scans directories to identify small file issues without modification
- **`compact`**: Merges small files into larger files based on specified criteria
- **`partition-aware`**: Maintains partition structure while compacting files within partitions

#### ⚙️ Parameters
- **`--input-path`**: HDFS directory path containing small files to compact
- **`--output-path`**: Target directory for compacted files (optional, defaults to input path)
- **`--threshold-mb`**: File size threshold in MB to consider as "small" (default: 32MB)
- **`--target-size-mb`**: Target size for compacted files in MB (default: 128MB)
- **`--dry-run`**: Preview mode to analyze without actual compaction (true/false)
- **`--help`**: Displays usage information and available options

#### 🔍 Analysis Features
- **File Size Distribution**: Shows histogram of file sizes in the target directory
- **Impact Assessment**: Calculates potential memory savings and performance improvements
- **Partition Detection**: Automatically identifies partitioned data structures
- **Compression Analysis**: Evaluates current compression ratios and recommendations

#### 📄 Output
The task provides comprehensive reporting including:
- Number of small files identified and processed
- Before/after file count and size statistics
- NameNode memory savings achieved
- Processing time and throughput metrics
- Recommendations for optimal file organization

---

<div align="center">

**Built with ❤️ for the Big Data community**

</div>