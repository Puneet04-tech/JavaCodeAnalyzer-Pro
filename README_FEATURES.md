# JavaCodeAnalyzer-Pro: Enterprise-Grade Code Analysis Tool

## 🚀 Overview

JavaCodeAnalyzer-Pro is a comprehensive, pure-Java code analysis tool that provides enterprise-grade metrics for code quality, maintainability, complexity, and technical debt assessment. It supports multi-language analysis (Java, Python, JavaScript, and more) with no external dependencies beyond the JDK.

## ✨ Features

### Core Metrics
- **Lines of Code**: Total, code, comment, and blank line counts
- **Cyclomatic Complexity**: McCabe's complexity metric for control flow
- **Comment Ratio**: Documentation coverage percentage
- **Method/Class Counts**: Structural analysis

### Advanced Metrics
- **Maintainability Index (MI)**: Microsoft's formula for code maintainability (0-100 scale)
  - Formula: `MI = MAX(0, (171 - 5.2*ln(V) - 0.23*CC - 16.2*ln(LOC)) * 100 / 171)`
  - MI < 20: Red zone (hard to maintain)
  - MI 20-50: Yellow zone (moderate maintainability)
  - MI > 50: Green zone (highly maintainable)

- **Halstead Complexity Metrics**:
  - Volume (V): `N * log2(n)` where N = total operators/operands, n = distinct operators/operands
  - Difficulty (D): `(n1/2) * (N2/n2)`
  - Effort (E): `D * V`

- **Cognitive Complexity**: SonarSource's metric for code understandability
  - Counts control flow structures with nesting penalties
  - More intuitive than cyclomatic complexity for human comprehension

- **Risk Scoring**: Automated risk assessment (0-100 scale)
  - Formula: `(CC_norm * 0.3) + (churn_norm * 0.25) + (dup_norm * 0.2) + ((100-coverage) * 0.25)`
  - Combines complexity, churn, duplication, and coverage metrics

### Code Churn Analysis
When analyzing git repositories, the tool extracts:
- Commit count per file
- Lines added/deleted over time
- Churn rate (commits per day)
- Number of contributing authors
- Last modification timestamp

**Hotspot Detection**: Files with high churn + high complexity = maintenance hotspots

### Security Analysis
- **Secrets Detection**: Identifies hardcoded credentials and sensitive data
  - AWS keys and tokens
  - Private keys (PEM/RSA)
  - High-entropy strings (potential passwords)
  - API keys and tokens

### Multi-Language Support
- **Java**: Full syntax understanding
- **Python**: Comprehends Pythonic constructs (def, class, elif, docstrings)
- **JavaScript/TypeScript**: Arrow functions, classes, JSX
- **Generic**: Heuristic analysis for any text-based language

### Export Formats
1. **Text Report** (`--format=text`): Human-readable detailed report
2. **JSON** (`--format=json`): Structured data for tooling integration
3. **CSV** (`--format=csv`): Spreadsheet-compatible tabular format

### Performance Features
- **Parallel Processing** (`--parallel`): Multi-threaded file analysis
- **Extension Filtering** (`--ext=.java,.py`): Analyze only specific file types
- **Path Exclusions** (`--exclude=test`): Skip directories by prefix
- **Glob Patterns** (`--exclude-glob=**/node_modules/**`): Advanced filtering

## 📦 Installation

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Git (optional, for churn analysis)

### Compilation
```cmd
compile.bat
```

This compiles all 13 source files in the correct dependency order.

## 🎯 Usage

### Interactive Mode
```cmd
run.bat
```

Follow the menu prompts to analyze files or directories.

### Command-Line Mode

#### Basic Analysis
```cmd
analyzeDir.bat <directory>
```

#### With Options
```cmd
analyzeDir.bat src --format=csv --parallel --ext=.java,.kt --exclude=test --exclude-glob=**/build/**
```

### CLI Flags

| Flag | Description | Example |
|------|-------------|---------|
| `--format=<type>` | Output format: `text`, `json`, or `csv` | `--format=json` |
| `--parallel` | Enable multi-threaded analysis | `--parallel` |
| `--ext=<list>` | Comma-separated file extensions to analyze | `--ext=.java,.py` |
| `--exclude=<path>` | Exclude directories starting with prefix | `--exclude=test,vendor` |
| `--exclude-glob=<pattern>` | Exclude using glob patterns | `--exclude-glob=**/node_modules/**,**/.git/**` |

## 📊 Understanding the Metrics

### Maintainability Index (MI)
- **Range**: 0-100 (higher is better)
- **Interpretation**:
  - **0-20**: Critical - Immediate refactoring needed
  - **20-50**: Moderate - Consider simplification
  - **50-100**: Good - Code is maintainable

### Cyclomatic Complexity (CC)
- **Range**: 1-∞ (lower is better)
- **Guidelines**:
  - **1-10**: Simple, easy to test
  - **11-20**: Complex, needs attention
  - **21+**: Very complex, high risk

### Cognitive Complexity
- **Range**: 0-∞ (lower is better)
- Penalizes nested control structures more than flat ones
- Better predictor of actual code comprehension difficulty

### Risk Score
- **Range**: 0-100 (lower is better)
- **Factors**:
  - 30% Cyclomatic Complexity
  - 25% Code Churn Rate
  - 20% Duplication Percentage
  - 25% Lack of Test Coverage

### Halstead Metrics
- **Volume**: Information content of the code
- **Difficulty**: How hard the code is to write/understand
- **Effort**: Mental effort required to develop/maintain

## 📈 Example Reports

### Text Report
```
File: ComplexExample.java
  Lines of Code: 35
  Cyclomatic Complexity: 8
  Documentation: 7.3%

  Advanced Metrics:
    Maintainability Index: 43.9
    Cognitive Complexity: 19
    Halstead Volume: 1105.5
    Halstead Difficulty: 29.0
    Halstead Effort: 32021.1
    Risk Score: 12.0/100

  Code Churn:
    Commits: 45
    Lines Added: 230
    Lines Deleted: 180
    Churn Rate: 0.35 commits/day
    Authors: 3

  Findings:
    - Possible secret: AWS key pattern at line 12
```

### CSV Report
```csv
file,totalLines,codeLines,complexity,maintainabilityIndex,cognitiveComplexity,halsteadVolume,riskScore,commits,churnRate
ComplexExample.java,41,35,8,43.9,19,1105.5,12.0,45,0.35
```

### JSON Report
```json
{
  "generated": "2025-11-18T00:19:29",
  "files": [
    {
      "file": "ComplexExample.java",
      "complexity": 8,
      "maintainabilityIndex": 43.9,
      "cognitiveComplexity": 19,
      "halsteadVolume": 1105.5,
      "riskScore": 12.0,
      "churn": {
        "commits": 45,
        "churnRate": 0.35
      }
    }
  ]
}
```

## 🏗️ Project Structure

```
JavaCodeAnalyzer-Pro/
├── src/
│   ├── analyzer/
│   │   ├── CodeAnalyzer.java           # Core analysis engine
│   │   └── CodeAnalyzerMain.java       # CLI entry point
│   ├── model/
│   │   ├── JavaCodeMetrics.java        # Base metrics container
│   │   ├── AdvancedMetrics.java        # Halstead, MI, Cognitive Complexity
│   │   └── ChurnMetrics.java           # Git history analysis
│   └── utils/
│       ├── FileUtils.java              # File I/O utilities
│       ├── LanguageHeuristic.java      # Language-specific analyzers
│       ├── LanguageDetector.java       # Extension-based detection
│       ├── SecretsDetector.java        # Security scanning
│       ├── HalsteadCalculator.java     # Halstead metrics computation
│       ├── CognitiveComplexityCalculator.java  # Cognitive complexity
│       └── ChurnAnalyzer.java          # Git history parsing
├── data/test/                          # Sample files
├── output/                             # Generated reports
├── compile.bat                         # Build script
├── run.bat                             # Interactive launcher
└── analyzeDir.bat                      # CLI wrapper
```

## 🔬 Technical Details

### Halstead Metrics Computation
1. **Tokenize code**: Extract operators and operands
2. **Count distinct (n1, n2)**: Unique operators/operands
3. **Count total (N1, N2)**: All operator/operand occurrences
4. **Calculate**:
   - Volume: `V = N * log2(n)` where `N = N1 + N2`, `n = n1 + n2`
   - Difficulty: `D = (n1/2) * (N2/n2)`
   - Effort: `E = D * V`

### Maintainability Index Formula
```
MI = MAX(0, (171 - 5.2*ln(HalsteadVolume) - 0.23*CyclomaticComplexity - 16.2*ln(LOC)) * 100 / 171)
```
Based on Microsoft's original formula for Visual Studio Code Metrics.

### Cognitive Complexity Rules
- +1 for each: if, for, while, do, switch, catch, ternary (?:), && in conditions, || in conditions
- +1 per nesting level for nested control structures
- No penalty for switch cases or linear function calls

## 🎓 References

1. **Halstead Metrics**: Halstead, M. H. (1977). *Elements of Software Science*. Elsevier.
2. **Cyclomatic Complexity**: McCabe, T. J. (1976). "A Complexity Measure". *IEEE Transactions on Software Engineering*.
3. **Maintainability Index**: Coleman, D., et al. (1994). "Using Metrics to Evaluate Software System Maintainability".
4. **Cognitive Complexity**: G. Ann Campbell (2018). "Cognitive Complexity - A new way of measuring understandability". SonarSource.

## 🛠️ Troubleshooting

### Git Churn Analysis Shows Zeros
- Ensure the analyzed directory is inside a git repository
- Verify git is installed and accessible in PATH
- Files must have commit history to compute churn

### Compilation Errors
- Ensure JDK is in PATH: `java -version`
- Clean build: Delete `bin/` directory and recompile
- Check file encoding: All source files should be UTF-8

### Performance Issues
- Use `--parallel` flag for directories with many files
- Apply `--ext` filters to reduce file count
- Exclude unnecessary directories with `--exclude-glob`

## 📝 License

This project is provided as-is for educational and professional use.

## 🤝 Contributing

This is a pure-Java implementation with zero external dependencies. When contributing:
- Maintain Java 8+ compatibility
- Use only `java.util` and `java.io` packages
- Add unit tests for new metrics
- Update README with new features

## 🚀 Roadmap

### Completed ✅
- Multi-language support
- Halstead metrics
- Maintainability Index
- Cognitive Complexity
- Code churn analysis
- Risk scoring
- Secrets detection
- Parallel processing
- Multiple export formats

### Planned 🔮
- [ ] Enhanced duplication detection (token-based similarity)
- [ ] Coupling/cohesion metrics (LCOM)
- [ ] Test coverage parsing (JaCoCo, Cobertura)
- [ ] SARIF export for CI/CD integration
- [ ] HTML dashboard with Chart.js visualizations
- [ ] Quality gates and threshold enforcement
- [ ] Trend tracking with baseline comparison
- [ ] SAST checks (SQL injection, XSS sinks)
- [ ] Actionable refactoring recommendations
- [ ] Per-method complexity breakdowns

## 📞 Support

For issues, questions, or feature requests, please open an issue on the project repository.

---

**Built with ❤️ using pure Java - Zero external dependencies**
Quick Start:
1. Run `compile.bat` to build the project.
2. Analyze a directory: `java -cp bin analyzer.CodeAnalyzerMain data\test --format=html`
3. Open the dashboard: `Start-Process "output\dashboard.html"`
4. Enable parallel processing for speed: `--parallel`
5. Filter by extensions: `--ext=.java,.py,.js`
6. Export machine-readable output for CI: `--format=json`
7. For churn metrics run inside a Git repository with `git` available on PATH.
8. Report issues or request features by opening an issue on the GitHub repo.
9. Enjoy exploring code health!
