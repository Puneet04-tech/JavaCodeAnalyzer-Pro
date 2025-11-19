@echo off
REM Ensure script runs from its own directory so relative paths work
cd /d %~dp0
echo Compiling JavaCodeAnalyzer Pro...
echo ================================

REM Create bin directory
if not exist bin mkdir bin

REM Compile in order (dependencies first)
echo [1/13] Compiling FileUtils.java...
javac -d bin src\utils\FileUtils.java

echo [2/13] Compiling CodeAnalysisUtils.java...
javac -d bin src\utils\CodeAnalysisUtils.java

echo [3/13] Compiling LanguageHeuristic.java...
javac -d bin -cp bin src\utils\LanguageHeuristic.java

echo [4/13] Compiling LanguageDetector.java...
javac -d bin -cp bin src\utils\LanguageDetector.java

echo [5/13] Compiling SecretsDetector.java...
javac -d bin -cp bin src\utils\SecretsDetector.java

echo [6/13] Compiling AdvancedMetrics.java...
javac -d bin -cp bin src\model\AdvancedMetrics.java

echo [7/13] Compiling ChurnMetrics.java...
javac -d bin -cp bin src\model\ChurnMetrics.java

echo [8/13] Compiling JavaCodeMetrics.java...
javac -d bin -cp bin src\model\JavaCodeMetrics.java

echo [9/13] Compiling HalsteadCalculator.java...
javac -d bin -cp bin src\utils\HalsteadCalculator.java

echo [10/13] Compiling CognitiveComplexityCalculator.java...
javac -d bin -cp bin src\utils\CognitiveComplexityCalculator.java

echo [11/13] Compiling ChurnAnalyzer.java...
javac -d bin -cp bin src\utils\ChurnAnalyzer.java

echo [12/13] Compiling HtmlReportGenerator.java...
javac -d bin -cp bin src\reports\HtmlReportGenerator.java

echo [13/13] Compiling CodeAnalyzer.java...
javac -d bin -cp bin src\analyzer\CodeAnalyzer.java

echo [14/14] Compiling CodeAnalyzerMain.java...
javac -d bin -cp bin src\analyzer\CodeAnalyzerMain.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] Compilation complete!
    echo.
    echo To run: java -cp bin analyzer.CodeAnalyzerMain
) else (
    echo.
    echo [ERROR] Compilation failed!
)

pause
