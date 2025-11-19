# JavaCodeAnalyzer Pro - Pure Java Core Edition

**Real Code Quality Analysis Tool - 100% Core Java Only**

## ✅ What Makes This Special?

```
✅ ZERO External Libraries (only java.*)
✅ ZERO Frameworks (no Spring, no Maven, no Gradle)
✅ ONLY Java Core Features:
   • java.io (BufferedReader, FileWriter, PrintStream)
   • java.util (ArrayList, HashMap, TreeMap, HashSet)
   • java.lang (String, Math, System)
   • java.util.Collections (sorting, searching)
✅ Pure Algorithms & Data Structures
✅ Demonstrates Core Java Mastery
```

---

## Project Overview

**JavaCodeAnalyzer Pro** analyzes Java source code to detect:
- Code complexity (cyclomatic complexity)
- Code quality metrics
- Documentation ratio
- Duplicate code blocks
- Code violations and smells

**No Frameworks. No External Libraries. Pure Java Core.**

---

## Core Java Features Demonstrated

### 1. File I/O (java.io)
```java
// BufferedReader with try-with-resources
try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
    String line;
    while ((line = reader.readLine()) != null) {
        lines.add(line);
    }
}
```

### 2. Collections Framework (java.util)
```java
// HashMap - for storing metrics
Map<String, Integer> patterns = new HashMap<>();

// ArrayList - for storing lines
List<String> lines = new ArrayList<>();

// TreeMap - automatically sorted
Map<String, Integer> sorted = new TreeMap<>();

// HashSet - unique values
Set<String> violations = new HashSet<>();
```

### 3. Algorithms & Sorting
```java
// Custom comparator for sorting
Collections.sort(list, (a, b) -> b.getValue().compareTo(a.getValue()));

// Recursion for directory traversal
javaFiles.addAll(getAllJavaFiles(file));
```

### 4. String Processing (java.lang)
```java
// Pattern matching
if (trimmed.contains("if")) complexity++;

// String manipulation
String block = lines.get(i) + "\n" + lines.get(i+1);
```

---

## File Structure

```
JavaCodeAnalyzer-Pro/
│
├── src/
│   ├── analyzer/
│   │   ├── CodeAnalyzerMain.java      (Entry point, UI)
│   │   └── CodeAnalyzer.java          (Main logic)
│   ├── model/
│   │   └── JavaCodeMetrics.java       (Data model)
│   └── utils/
│       ├── FileUtils.java             (File I/O)
│       └── CodeAnalysisUtils.java     (Algorithms)
│
├── data/test/                         (Test Java files)
├── output/                            (Generated reports)
└── README.md
```

---

## Features

### 1. Analyze Single File
- Line counting (code, comments, blank)
- Cyclomatic complexity calculation
- Comment ratio
- Method and class count

### 2. Analyze Directory
- Batch analyze multiple Java files
- Summary statistics

### 3. Find Duplicate Code
- Detects repeated code blocks
- Uses HashMap for O(1) lookup
- Reports frequency

### 4. Generate Reports
- Save analysis to text file
- Professional formatting

---

## Compile & Run (Updated)

What's New: this release adds enterprise-grade metrics (Halstead, Maintainability Index, Cognitive Complexity), Git-based churn analysis, parallel processing, and an interactive HTML dashboard (`output/dashboard.html`).

### Quick Steps (Windows)

1) Open PowerShell and change to the project directory:

```powershell
cd 'd:\toolbox\JavaCodeAnalyzer-Pro\JavaCodeAnalyzer-Pro'
```

2) Compile everything (convenience script included):

```powershell
.
\compile.bat
# or: cmd /c compile.bat
```

3) Run the analyzer (examples):

- Interactive text UI (menu):

```powershell
java -cp bin analyzer.CodeAnalyzerMain
```

- Non-interactive analysis (text report):

```powershell
java -cp bin analyzer.CodeAnalyzerMain <path-to-code> --format=text
```

- Generate CSV/JSON reports:

```powershell
java -cp bin analyzer.CodeAnalyzerMain <path-to-code> --format=csv
java -cp bin analyzer.CodeAnalyzerMain <path-to-code> --format=json
```

- Generate the interactive HTML dashboard (recommended):

```powershell
java -cp bin analyzer.CodeAnalyzerMain <path-to-code> --format=html
# Example: java -cp bin analyzer.CodeAnalyzerMain data\test --format=html
```

4) Open the generated dashboard in your default browser:

```powershell
Start-Process "d:\toolbox\JavaCodeAnalyzer-Pro\JavaCodeAnalyzer-Pro\output\dashboard.html"
```

### Helpful Flags

- `--format=text|json|csv|html` — output format (HTML creates `output/dashboard.html`).
- `--parallel` — enable multi-threaded analysis for large repositories.
- `--ext=.java,.py,.js` — analyze only the listed extensions (comma-separated).
- `--exclude=<path>` — comma-separated path prefixes to skip (e.g., `--exclude=test,docs`).
- `--exclude-glob=<pattern>` — glob pattern to exclude (e.g., `**/node_modules/**`).

### Example (analyze src and save HTML dashboard)

```powershell
compile.bat
java -cp bin analyzer.CodeAnalyzerMain src --format=html --parallel --ext=.java
Start-Process "output\dashboard.html"
```

### Packaging (optional)

To create a runnable JAR (manual steps):

```powershell
mkdir dist
jar cfe dist\codeanalyzer.jar analyzer.CodeAnalyzerMain -C bin .
java -jar dist\codeanalyzer.jar <path> --format=html
```

Notes:
- If you want Git churn metrics, run the analyzer from within a Git repository (git must be installed and available in PATH).
- The HTML dashboard uses Chart.js via CDN; an internet connection is required to load charts unless you vendor Chart.js locally.

If you'd like, I can add a `build.bat` that produces the runnable JAR automatically and updates `run.bat` to accept the same flags.

---

## Sample Output

## Advanced Metrics (new)

This release adds a set of enterprise-oriented metrics per file and aggregated in reports:

- **Halstead Metrics**: Vocabulary, Length, Volume, Difficulty, Effort — estimated implementation complexity and effort.
- **Maintainability Index (MI)**: Composite score (0-100) indicating ease of maintenance (higher is better).
- **Cognitive Complexity**: A nesting-aware complexity metric focusing on human understandability.
- **Git Churn Metrics**: Commit count, lines added/deleted, number of authors, churn rate (commits/day).
- **Risk Score**: Heuristic combining MI, churn, and cognitive complexity to highlight risky files.

Each analyzed file contains an `advancedMetrics` block in JSON-like reports and the HTML dashboard. The HTML dashboard displays interactive charts for these values and a sortable file table.

### How to interpret

- Halstead Volume: larger numbers imply more mental effort to understand the file.
- Maintainability Index: values > 85 = excellent, 65-85 = moderate, < 65 = needs attention.
- Cognitive Complexity: lower is better; favors flatter, simpler control flow.
- Churn: high recent churn with low MI indicates risky churn.


## Sample Output (with Advanced Metrics)

```
╔════════════════════════════════════════════════════════╗
║                   ANALYSIS RESULTS                     ║
╚════════════════════════════════════════════════════════╝

File: src/analyzer/CodeAnalyzer.java
  Total Lines: 250
  Code Lines: 180
  Comment Lines: 45
  Blank Lines: 25
  Cyclomatic Complexity: 12
  Comment Ratio: 18.0%
  Methods: 8 | Classes: 1

  Advanced Metrics:
    Halstead: Volume=1320.4, Difficulty=24.3, Effort=32053
    Maintainability Index: 62.4
    Cognitive Complexity: 18
    Git Churn: commits=12, linesAdded=184, linesDeleted=50, authors=3, churnRate=0.12 commits/day
    Risk Score: 0.72 (0-1, higher = more risky)

  Quality: ⭐⭐ NEEDS ATTENTION

SUMMARY:
  Files Analyzed: 5
  Average Cyclomatic Complexity: 6.4
  Average Maintainability Index: 71.8
  Total Code Lines: 720
  High-Risk Files: 2

Reports saved to:
  - output/code_analysis_report.txt
  - output/code_analysis_report.csv
  - output/code_analysis_report.json
  - output/dashboard.html (interactive)
```

---

## Core Java Concepts Demonstrated

### Data Structures
- **ArrayList**: Dynamic array implementation
- **HashMap**: Hash table for O(1) insertion/lookup
- **TreeMap**: Red-black tree with sorted order
- **HashSet**: Unique values, no duplicates
- **List & Map interfaces**: Polymorphism

### Algorithms
- **Linear search**: O(n) pattern matching
- **HashMap operations**: O(1) average lookup
- **Collections.sort**: Merge sort O(n log n)
- **Recursion**: Directory tree traversal
- **Custom comparators**: Sorting by value

### File I/O
- **BufferedReader**: Efficient line-by-line reading
- **FileWriter**: Writing output
- **Try-with-resources**: Automatic resource management
- **PrintStream**: Formatted output

### String Processing
- **String.contains()**: Substring matching
- **String concatenation**: Building larger strings
- **String.trim()**: Removing whitespace

### Object-Oriented Design
- **Encapsulation**: Private fields, public getters
- **Separation of concerns**: Model, Utils, Analyzer
- **Single Responsibility**: Each class has one job
- **Interfaces**: List, Map, Comparable

---

## Interview Talking Points

### "Explain your algorithm for detecting complexity"
```
I count control flow statements:
- if statements: +1
- for loops: +1
- while loops: +1
- switch cases: +1
- catch blocks: +1

This is McCabe's Cyclomatic Complexity metric.
Complexity 1-5 = simple, 6-10 = moderate, 11+ = complex.
```

### "How do you handle large files efficiently?"
```
I use BufferedReader instead of reading entire file at once:
- Reads line by line (fixed memory usage)
- O(n) time complexity where n = number of lines
- Works with files of any size
- Try-with-resources ensures cleanup
```

### "How do you find duplicates without O(n²) complexity?"
```
I use HashMap for O(1) lookup:
1. Create HashMap of code blocks
2. For each 3-line block, check if seen before
3. If yes, increment counter
4. Result: O(n) instead of O(n²)
```

### "Why use TreeMap instead of HashMap?"
```
HashMap: Fast O(1) but unordered
TreeMap: Ordered O(log n), automatically sorted

I use TreeMap when I need results in sorted order
without extra sorting step.
```

---

## Resume Bullets

✓ Developed code quality analysis tool in pure Java using only 
  java.io, java.util, and java.lang packages

✓ Implemented HashMap-based duplicate detection algorithm with 
  O(n) time complexity vs. naive O(n²) approach

✓ Designed recursive directory traversal algorithm for analyzing 
  multiple Java files with visitor pattern

✓ Created custom Comparator for sorting analysis results by 
  complexity metrics and frequency distribution

✓ Applied multiple data structures (ArrayList, HashMap, TreeMap, 
  HashSet) for optimal performance in different use cases

✓ Implemented McCabe's Cyclomatic Complexity calculation using 
  control flow graph pattern recognition

✓ Built robust file I/O with BufferedReader, try-with-resources, 
  and error handling for production reliability

✓ Designed layered architecture (Model-Utils-Analyzer) demonstrating 
  separation of concerns and SOLID principles

---

## Interview Questions You Can Answer

**Q: "Why use Collections.sort instead of bubble sort?"**
A: Collections.sort uses TimSort (merge sort variant) with O(n log n) complexity vs. bubble sort O(n²). Java's built-in is optimized and battle-tested.

**Q: "How would you handle 1GB file?"**
A: BufferedReader reads line-by-line with small fixed buffer. Only current line in memory, not entire file.

**Q: "Explain HashMap vs. TreeMap trade-off"**
A: HashMap is faster O(1) average but unsorted. TreeMap is O(log n) but maintains sort order. Choose based on needs.

**Q: "What if code block frequency map gets huge?"**
A: Use LinkedHashMap for insertion order or implement custom Map if memory is critical.

**Q: "How to extend this to other languages?"**
A: Create Language interface with pattern methods. Different implementations for Java, Python, C++.

---

## Testing

### Test File 1: SimpleClass.java
```java
public class SimpleClass {
    public void method1() {
        System.out.println("Hello");
    }
}
```
Expected: Low complexity, high quality

### Test File 2: ComplexClass.java
```java
public class ComplexClass {
    public void complexMethod() {
        if (x > 0) {
            for (int i = 0; i < 10; i++) {
                if (condition) {
                    switch(value) {
                        case 1: break;
                    }
                }
            }
        }
    }
}
```
Expected: High complexity, needs refactoring

---

## Scalability

**Current**: O(n) per file where n = lines of code

**For 10K files**:
- Implement parallel processing (parallel streams)
- Use memory-mapped files for huge files
- Add caching for repeated analyses
- Distribute across multiple threads

**Architecture stays pure Java core** - no frameworks needed!

---

## What You'll Learn

### Java Core Mastery
✓ Collections framework deeply
✓ File I/O best practices
✓ Algorithm complexity analysis
✓ Data structure selection
✓ Custom comparators and sorting

### Software Engineering
✓ Layered architecture
✓ Separation of concerns
✓ SOLID principles
✓ Algorithm optimization
✓ Error handling

### Interview Readiness
✓ Can discuss any algorithm used
✓ Can optimize on the fly
✓ Can handle edge cases
✓ Shows practical thinking

---

## License

MIT License - Use for learning

---

**This is your competitive advantage for placement.** 

Most candidates memorize frameworks.
You built something with pure Java core.

That's 10x more impressive! 🚀
