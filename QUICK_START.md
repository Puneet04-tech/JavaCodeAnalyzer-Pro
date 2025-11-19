# 🚀 JavaCodeAnalyzer Pro - Quick Start

## Compilation

### Windows
```batch
# Compile all classes
javac -d bin src/utils/FileUtils.java
javac -d bin src/utils/CodeAnalysisUtils.java
javac -d bin -cp bin src/model/JavaCodeMetrics.java
javac -d bin -cp bin src/analyzer/CodeAnalyzer.java
javac -d bin -cp bin src/analyzer/CodeAnalyzerMain.java
```

### Linux/Mac
```bash
# Create bin directory
mkdir -p bin

# Compile
javac -d bin src/utils/FileUtils.java
javac -d bin src/utils/CodeAnalysisUtils.java
javac -d bin -cp bin src/model/JavaCodeMetrics.java
javac -d bin -cp bin src/analyzer/CodeAnalyzer.java
javac -d bin -cp bin src/analyzer/CodeAnalyzerMain.java
```

## Run

```bash
java -cp bin analyzer.CodeAnalyzerMain
```

## Menu Options

1. **Analyze Single File** - Paste file path like: src/analyzer/CodeAnalyzer.java
2. **Analyze Directory** - Paste dir path like: src/analyzer/
3. **Find Duplicates** - Paste dir path to find duplicate code blocks
4. **Generate Report** - Creates output/code_analysis_report.txt

## Example Usage

```
Enter choice: 1
Enter file path: src/analyzer/CodeAnalyzer.java

📝 Analyzing: CodeAnalyzer.java
✓ Analysis complete!

╔════════════════════════════════════════╗
║          ANALYSIS RESULTS             ║
╚════════════════════════════════════════╝

File: CodeAnalyzer.java
  Total Lines: 250
  Code Lines: 180
  Comment Lines: 45
  Blank Lines: 25
  Cyclomatic Complexity: 12
  Comment Ratio: 18.0%
  Methods: 8 | Classes: 1
  Quality: ⭐⭐ GOOD
```

Done! ✨
