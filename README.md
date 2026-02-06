# BFMSE

## Project Introduction

BFMSE is an efficient storage and query system for multi-set data structures based on hypergraphs. The project provides two implementation methods:

1. **Static_BFMSE**: Static version, unmodifiable after construction.

2. **Dynamic_BFMSE**: Dynamic version, supports add, delete, and update operations.

This system achieves efficient memory usage and query performance while maintaining a low false positive rate. It is particularly suitable for scenarios requiring storage of a large number of key-value pairs where each key is associated with multiple values.

## Datset

Online Reatail: https://archive.ics.uci.edu/dataset/352/online+retail.

Enron: [https://www.cs.cmu.edu/](https://www.cs.cmu.edu/) enron/.

MAWI: http://mawi.nezu.wide.ad.jp/maw

## Core Features

- ✅ **Low False Positive Rate**: Configurable false positive rate control.

- ✅ **Fast Query**: Average O(1) time complexity for queries.

- ✅ **Memory Optimized**: Significantly reduces memory usage compared to traditional data structures.

- ✅ **Two Modes**: Static construction and dynamic update for different use cases.

## System Requirements

- **Java**: Version 8 or higher.

- **Gradle**: Version 8.7 or higher.

- **Memory**: Depends on data scale.

- **Operating System**: Linux, Windows.

## Quick Start

1. Download from https://anonymous.4open.science/r/BFMSE-8B76

2. Using IDEA open this folder 'BFMSE-Github'

3. The D_BFMSE_Construct.java and Staic_BFMSE_Test.java are used to test the BFMSE and D-BFMSE solutions.

## Project Structure

```
BFMSE-Github/
├── src/
│   ├── main/java/org/Modules/
│   │   ├── Dynamic_BFMSE.java      # Dynamic BFMSE implementation
│   │   ├── Static_BFMSE.java       # Static BFMSE implementation
│   │   ├── Hash.java              # Hash function utility
│   │   ├── MurMurHashUtils.java   # MurMur hash utility
│   │   ├── MyBitset.java          # Custom BitSet
│   │   └── Utils.java             # General utility functions
│   └── test/java/
│       ├── D_BFMSE_Construct.java  # Dynamic version test
│       └── Staic_BFMSE_Test.java   # Static version test
├── build.gradle                   # Gradle build configuration
├── settings.gradle                # Gradle settings
├── gradlew                       # Linux/macOS Gradle wrapper
└── gradlew.bat                   # Windows Gradle wrapper
```
