#!/bin/bash

echo "Compiling JavaCodeAnalyzer Pro..."
echo "=================================="

# Create bin directory
mkdir -p bin

# Compile in order
echo "[1/5] Compiling FileUtils.java..."
javac -d bin src/utils/FileUtils.java

echo "[2/5] Compiling CodeAnalysisUtils.java..."
javac -d bin src/utils/CodeAnalysisUtils.java

echo "[3/5] Compiling JavaCodeMetrics.java..."
javac -d bin -cp bin src/model/JavaCodeMetrics.java

echo "[4/5] Compiling CodeAnalyzer.java..."
javac -d bin -cp bin src/analyzer/CodeAnalyzer.java

echo "[5/5] Compiling CodeAnalyzerMain.java..."
javac -d bin -cp bin src/analyzer/CodeAnalyzerMain.java

if [ $? -eq 0 ]; then
    echo ""
    echo "[SUCCESS] Compilation complete!"
    echo ""
    echo "To run: java -cp bin analyzer.CodeAnalyzerMain"
else
    echo ""
    echo "[ERROR] Compilation failed!"
fi
