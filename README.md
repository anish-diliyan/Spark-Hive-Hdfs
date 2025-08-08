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

**Built with ❤️ for the Big Data community**

</div>