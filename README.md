# Dynamic Microservice Composition Framework (PoC)

## Overview
This project is a Proof of Concept (PoC) developed for an academic research laboratory. It provides a framework for the dynamic composition and execution of stateless microservices (functions) within an edge-cloud continuum. The system chains loosely coupled Java functions together using a central, data-driven Wrapper.

## Current Project Status
The core execution engine (Wrapper) and the foundation for the function ecosystem are successfully implemented. 

### 1. Function Ecosystem
* **I/O Contract**: All functions implement the `java.util.function.Function<String, String>` interface. Data transfer between functions is strictly file-based (passing file paths), with complex data structures serialized to JSON using `Gson`.
* **Strict Isolation**: The project uses a Bazel Monorepo structure. Every function resides in its own directory with an independent `BUILD` file. This guarantees complete compile-time isolation and allows functions to be executed and tested independently.

### 2. The Orchestrator (Wrapper)
A robust, dynamic Java Wrapper has been developed to orchestrate the execution chain:
* **Data-Driven Execution**: The Wrapper acts as a generic engine. It reads the execution sequence from an external configuration file (`chain-config.json`) and instantiates functions at runtime using the Java Reflection API.
* **Parallel Processing (Scatter-Gather)**: If a function outputs a list of items, the Wrapper automatically parallelizes the execution of the subsequent function. It allocates threads based on the available CPU cores using `CompletableFuture`, launching instances simultaneously and synchronizing their outputs.
* **Defensive Architecture**: 
  * *Thread-per-Instance*: Prevents race conditions during parallel execution by ensuring each thread receives its own dedicated function instance in memory.
  * *I/O Garbage Collection*: A global cleanup mechanism tracks all temporary and intermediate files (using UUIDs to prevent naming collisions). A `finally` block strictly deletes these files after execution or upon failure, ensuring the container's virtual disk does not fill up.

### 3. The Builder Module (Code Generator)
A Python-based generation engine utilizing `Jinja2` templates automates the configuration of the Bazel build system.
* **Automated Manifest Generation**: The `generate.py` script traverses the function directories, parses the `metadata.json` files, and automatically generates the necessary Bazel `BUILD` files for each function.
* **Dynamic Build Hierarchies**: The script dynamically injects the discovered function targets into the Wrapper's dependency graph and generates the root `MODULE.bazel` file, ensuring a highly scalable and maintainable monorepo architecture.

## Next Steps
* Further refining the build logic.
* Implementing the Adapter module to deploy the generated image via Docker Compose.
* Integrating execution time monitoring (latency tracking) into the Wrapper to report metrics.
