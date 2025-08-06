# Spark-Hive-HDFS Cluster

A complete big data ecosystem setup using Docker Compose with Hadoop HDFS, Apache Spark, and Apache Hive.

## Architecture

- **HDFS**: Distributed file system with NameNode and 2 DataNodes
- **YARN**: Resource manager and node manager for cluster resource management
- **Spark**: Master and 2 worker nodes for distributed processing
- **Hive**: SQL interface with PostgreSQL metastore

## Quick Start

1. **Start the cluster:**
   ```bash
   docker compose up -d
   ```

2. **Run tests:**
   ```bash
   ./test_cluster.sh
   ```

3. **Access Web UIs:**
   - Spark Master: http://localhost:8080
   - YARN Resource Manager: http://localhost:8088
   - HDFS NameNode: http://localhost:9870

## Development

### Build and Test
```bash
sbt compile
sbt test
sbt assembly
```

### Individual Tests
```bash
# Run specific test
sbt "testOnly HDFSSpec"
```

## Project Structure
```
├── docker-compose.yml          # Docker services configuration
├── hadoop.env                  # Hadoop environment variables
├── build.sbt                   # SBT build configuration
├── src/
│   ├── main/
│   │   ├── scala/              # Main source code
│   │   └── resources/          # Configuration files
│   └── test/
│       └── scala/              # Test specifications
└── project/                    # SBT project configuration
```