# Spark-Hive-HDFS Cluster

A complete enterprise-grade big data ecosystem setup using Docker Compose with Hadoop HDFS, Apache Spark, and Apache Hive, featuring automated partition management functionality.

## 🏗️ Architecture

- **HDFS**: Distributed file system with NameNode and 2 DataNodes
- **YARN**: Resource manager and node manager for cluster resource management  
- **Spark**: Master and 2 worker nodes for distributed processing
- **Hive**: SQL interface with PostgreSQL metastore
- **Partition Management**: Automated cleanup of old table partitions

## 🚀 Quick Setup & Run

### Prerequisites
- **Docker & Docker Compose**: Latest version
- **SBT (Scala Build Tool)**: Version 1.8+
- **Java**: Version 8 or 11
- **Git**: For cloning the repository

### 1. Clone & Setup
```bash
git clone https://github.com/anish-diliyan/Spark-Hive-Hdfs.git
cd Spark-Hive-Hdfs
```

### 2. Start the Cluster
```bash
# Start all services (takes 2-3 minutes for full startup)
docker compose up -d

# Check all services are running
docker compose ps

# Wait for services to be healthy (especially YARN Resource Manager)
docker logs resource_manager
```

### 3. Build the Application
```bash
# Compile and run tests
sbt clean compile test

# Build deployment JAR
sbt assembly
```

### 4. Run Tasks

#### Cluster Validation
```bash
# Test all cluster components
./scripts/cluster-test.sh --test-type all --record-count 1000

# Test specific component
./scripts/cluster-test.sh --test-type spark --record-count 500

# Get help
./scripts/cluster-test.sh --help
```

#### Partition Management
```bash
# Dry run (preview what will be deleted)
./scripts/partition-cleanup.sh --table sales_data --cutoff-year 2023 --dry-run true

# Actual cleanup (remove partitions older than 2023)
./scripts/partition-cleanup.sh --table sales_data --cutoff-year 2023 --dry-run false

# Get help
./scripts/partition-cleanup.sh --help
```

## 🌐 Web UIs

Once the cluster is running, access these interfaces:

- **Spark Master**: http://localhost:8080
- **HDFS NameNode**: http://localhost:9870
- **Hadoop History Server**: http://localhost:8188
- **YARN Resource Manager**: http://localhost:8088 (takes a few minutes to start)

**Note**: Hive Server runs on port 10000/10002 but doesn't provide a web UI - use Beeline CLI instead.

## 🧪 Development & Testing

### Run Tests
```bash
# Run all unit tests
sbt test

# Run with coverage analysis
sbt coverage test coverageReport

# Run specific test packages
sbt "testOnly cluster_test.*"     # Cluster component tests
sbt "testOnly partition_clean.*"  # Partition cleanup tests
sbt "testOnly common.*"           # Common utility tests
```

### Build Commands
```bash
sbt clean                    # Clean build artifacts
sbt compile                  # Compile source code
sbt test                     # Run unit tests
sbt assembly                 # Create fat JAR for deployment
sbt coverageReport           # Generate coverage report
```

## 📁 Project Structure

```
├── docker-compose.yml           # Docker services configuration
├── docker-compose.env           # Docker environment variables
├── build.sbt                    # SBT build configuration with coverage
├── src/
│   ├── main/
│   │   ├── scala/
│   │   │   ├── cluster_test/        # Cluster testing components
│   │   │   │   ├── ClusterTest.scala         # Test interface
│   │   │   │   ├── ClusterTestTask.scala     # Main executable
│   │   │   │   ├── SparkTest.scala           # Spark validation
│   │   │   │   ├── HDFSTest.scala            # HDFS validation
│   │   │   │   ├── HiveTest.scala            # Hive validation
│   │   │   │   └── PerformanceTest.scala     # Performance testing
│   │   │   ├── partition_clean/     # Partition cleanup components
│   │   │   │   ├── ArgumentParser.scala      # Command line parsing
│   │   │   │   ├── PartitionCleanupTask.scala # Main executable
│   │   │   │   ├── PartitionManager.scala    # Core cleanup logic
│   │   │   │   └── PartitionInfoDisplay.scala # Info display
│   │   │   └── common/              # Common utilities
│   │   │       └── SparkSessionManager.scala # Spark session management
│   │   └── resources/           # Configuration files
│   │       ├── application.conf     # Production config
│   │       └── logback.xml          # Production logging
│   └── test/
│       ├── scala/               # Unit tests (mirrors main structure)
│       │   ├── cluster_test/        # 5 tests - component validation
│       │   ├── partition_clean/     # 6 tests - business logic
│       │   └── common/              # 1 test - utility validation
│       └── resources/           # Test configuration files
│           ├── application.conf     # Test config (local mode)
│           └── logback-test.xml     # Test logging (less verbose)
├── scripts/
│   ├── cluster-test.sh          # Cluster validation script
│   └── partition-cleanup.sh     # Partition cleanup script
└── project/                     # SBT project configuration
    ├── build.properties
    └── plugins.sbt             # SBT plugins (assembly, scoverage)
```

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

## 🧹 Partition Management

### Purpose
Remove partitions from Hive tables that are older than a specified year. This is a common data lifecycle management task in big data environments.

### Usage Examples
```bash
# Preview what would be deleted (recommended first)
./scripts/partition-cleanup.sh --table sales_data --cutoff-year 2023 --dry-run true

# Remove partitions older than 2023 (removes 2020, 2021, 2022)
./scripts/partition-cleanup.sh --table sales_data --cutoff-year 2023 --dry-run false

# Custom partition column
./scripts/partition-cleanup.sh --table events --cutoff-year 2022 --partition-column event_year --dry-run false
```

### Parameters
- `--table`: Table name (default: sales_data)
- `--cutoff-year`: Remove partitions older than this year (default: 2023)
- `--dry-run`: Preview mode - true/false (default: true)
- `--partition-column`: Partition column name (default: year)

## 🔍 Troubleshooting

### Common Issues

**Services not starting:**
```bash
# Check logs
docker compose logs [service_name]

# Restart specific service
docker compose restart [service_name]

# YARN Resource Manager takes time to start
docker logs resource_manager
```

**Port conflicts:**
```bash
# Check what's using ports
netstat -tulpn | grep :8080
```

**Memory issues:**
```bash
# Increase Docker memory allocation
# Docker Desktop: Settings > Resources > Memory (recommend 8GB+)
```

**Build issues:**
```bash
# Clean and rebuild
sbt clean compile

# Check Java version
java -version  # Should be 8 or 11
```

### Useful Commands
```bash
# Access Spark shell
docker exec -it spark_master /spark/bin/spark-shell

# Access HDFS
docker exec -it spark_master hdfs dfs -ls /

# Access Hive CLI
docker exec -it hive_server beeline -u jdbc:hive2://localhost:10000

# Check service status
docker compose ps

# View service logs
docker logs [service_name]

# Restart all services
docker compose restart
```

## 🧪 Quality Assurance

### Test Coverage
- **Unit Tests**: 11 tests covering all components
- **Coverage Threshold**: 80% statement coverage (enforced)
- **Coverage Tool**: Scoverage with HTML/XML reports

### Code Quality
- **Architecture**: SOLID principles implementation
- **Testing**: Comprehensive unit test suite
- **Documentation**: Inline code documentation
- **Logging**: Structured logging with different levels

### Test Categories
```bash
# Component validation tests
sbt "testOnly cluster_test.ClusterTestSpec"

# Business logic tests  
sbt "testOnly partition_clean.ArgumentParserSpec"
sbt "testOnly partition_clean.PartitionManagerSpec"

# Utility tests
sbt "testOnly common.SparkSessionManagerSpec"
```

## ⚠️ Important Notes

1. **Always run dry run first** to see what will be deleted
2. **Backup important data** before running cleanup
3. **Partition cleanup is irreversible** - deleted partitions cannot be recovered
4. **Check dependencies** - ensure no downstream processes depend on old partitions
5. **Wait for cluster startup** - YARN Resource Manager takes 2-3 minutes to be ready

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Run tests: `sbt test`
4. Ensure coverage: `sbt coverage test coverageReport`
5. Commit changes: `git commit -m "Add new feature"`
6. Push branch: `git push origin feature/new-feature`
7. Create Pull Request

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 🆘 Support

For issues and questions:
- Create an [Issue](https://github.com/anish-diliyan/Spark-Hive-Hdfs/issues)
- Check existing [Discussions](https://github.com/anish-diliyan/Spark-Hive-Hdfs/discussions)

---

**Built with ❤️ for the Big Data community**