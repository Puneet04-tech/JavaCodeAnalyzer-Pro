@echo off
REM Wrapper to run CodeAnalyzer non-interactively
REM Usage: analyzeDir.bat <directory> [--format=json] [--parallel] [--exclude=C:\path;C:\other]
cd /d %~dp0
if "%1"=="" (
  echo Usage: analyzeDir.bat ^<directory^> [--format=json] [--parallel] [--exclude=path1;path2]
  goto :eof
)
java -cp bin analyzer.CodeAnalyzerMain %*
