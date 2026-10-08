# Chat Message Search System

A Data Structures and Algorithms project that implements and benchmarks classical string-matching algorithms for searching words and phrases across chat messages.

## Project Overview

As the number of chat messages increases, finding a specific word or phrase in a large conversation history becomes difficult and time-consuming.

The **Chat Message Search System** addresses this problem by implementing multiple classical string-matching algorithms and comparing their performance on the same chat message dataset.

The project demonstrates the practical application of Data Structures and Algorithms to real-world text-search problems.

## Objectives

- Search for words and phrases within chat messages
- Implement multiple classical string-matching algorithms
- Compare algorithms using execution time and character comparisons
- Retrieve and display messages containing the searched pattern
- Demonstrate practical applications of DSA in text searching
- Benchmark different algorithms using the same dataset

## Algorithms Implemented

The system implements five classical string-matching algorithms:

### 1. Naïve String Matching

Checks the pattern at every possible position in the text.

- No preprocessing required
- Used as the baseline algorithm
- Time Complexity: `O(nm)`

### 2. Knuth-Morris-Pratt (KMP)

Uses the **LPS (Longest Prefix Suffix)** array to avoid unnecessary comparisons.

- Preprocessing: `O(m)`
- Search: `O(n)`
- Extra Space: `O(m)`

### 3. Rabin-Karp

Uses hashing and a rolling hash to identify potential matches before performing character-level verification.

- Preprocessing: `O(m)`
- Average Search: `O(n)`
- Effective when hash filtering eliminates unnecessary comparisons

### 4. Z Algorithm

Uses the Z-array to determine prefix matches efficiently.

- Time Complexity: `O(n + m)`
- Extra Space: `O(n + m)`

### 5. Aho-Corasick

Uses a Trie and failure links for efficient multi-pattern searching.

- Suitable for searching multiple keywords simultaneously
- Search: `O(n + matches)`
- Particularly useful for multi-pattern search

## System Workflow

```text
Chat Dataset
     |
     v
Preprocessing
     |
     v
User Search Query
     |
     v
Algorithm Selection
     |
     v
Pattern Matching
     |
     +-----------------------------+
     |                             |
     v                             v
Matching Messages          Performance Metrics
     |                             |
     |                    +--------+--------+
     |                    |                 |
     v                    v                 v
Sender & Timestamp   Execution Time   Comparisons
     |
     v
Search Results
```

## Conclusion

The **Chat Message Search System** bridges theoretical Data Structures and Algorithms with a practical text-search application.

By implementing and benchmarking five classical string-matching algorithms on the same chat dataset, the project demonstrates how different algorithmic strategies affect execution time, character comparisons, and search performance.
