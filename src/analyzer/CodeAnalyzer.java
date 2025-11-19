package analyzer;

import java.io.*;
import java.util.*;
import model.JavaCodeMetrics;
import model.AdvancedMetrics;
import model.ChurnMetrics;
import utils.FileUtils;
import utils.CodeAnalysisUtils;
import utils.LanguageDetector;
import utils.LanguageHeuristic;
import utils.SecretsDetector;
import utils.HalsteadCalculator;
import utils.CognitiveComplexityCalculator;
import utils.ChurnAnalyzer;
import reports.HtmlReportGenerator;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

/**
 * Core analyzer class - uses ONLY java.util Collections and java.io
 */
public class CodeAnalyzer {

    private List<JavaCodeMetrics> allMetrics;
    private Map<String, Integer> codePatterns;
    private List<String> duplicateBlocks;

    public CodeAnalyzer() {
        this.allMetrics = new ArrayList<>();
        this.codePatterns = new HashMap<>();
        this.duplicateBlocks = new ArrayList<>();
    }

    // Analyze any file type (not limited to .java)
    public void analyzeFile(File file) {
        System.out.println("\n📝 Analyzing: " + file.getName());

        try {
            List<String> lines = FileUtils.readFileLines(file);
            JavaCodeMetrics metrics = new JavaCodeMetrics(file.getName());

            // Choose language-specific heuristic (falls back to generic)
            LanguageHeuristic heuristic = LanguageDetector.getHeuristicForFile(file);
            heuristic.analyze(lines, metrics);

            // Compute advanced metrics
            AdvancedMetrics advancedMetrics = new AdvancedMetrics();
            
            // Halstead metrics
            HalsteadCalculator.computeHalstead(lines, advancedMetrics);
            
            // Cognitive Complexity
            int cognitiveComplexity = CognitiveComplexityCalculator.computeCognitiveComplexity(lines);
            advancedMetrics.setCognitiveComplexity(cognitiveComplexity);
            
            // Maintainability Index: MI = MAX(0, (171 - 5.2*ln(V) - 0.23*CC - 16.2*ln(LOC)) * 100 / 171)
            advancedMetrics.computeMaintainabilityIndex(metrics.getCyclomaticComplexity(), metrics.getCodeLines());
            
            // Per-method/class metrics (simplified - track max complexity)
            advancedMetrics.setMaxMethodComplexity(metrics.getCyclomaticComplexity());
            advancedMetrics.setAvgMethodComplexity(metrics.getMethodCount() > 0 ? 
                metrics.getCyclomaticComplexity() / metrics.getMethodCount() : 0);
            
            metrics.setAdvancedMetrics(advancedMetrics);
            
            // Code churn analysis (if git repo)
            File gitRoot = ChurnAnalyzer.findGitRoot(file);
            if (gitRoot != null) {
                ChurnMetrics churnMetrics = ChurnAnalyzer.analyzeChurn(file, gitRoot);
                if (churnMetrics != null) {
                    metrics.setChurnMetrics(churnMetrics);
                    
                    // Compute risk score: complexity + churn correlation
                    double riskScore = computeRiskScore(metrics);
                    advancedMetrics.setRiskScore(riskScore);
                }
            }

            // Run secrets detection (heuristic)
            try {
                SecretsDetector.detectSecrets(lines, metrics);
            } catch (Throwable t) {
                // non-fatal
            }

            allMetrics.add(metrics);
            System.out.println("\n✓ Analysis complete!");

        } catch (IOException e) {
            System.out.println("✗ Error reading file: " + e.getMessage());
        }
    }

    public void analyzeDirectory(File dir) {
        // default: no excludes, single-threaded, no ext filter, no glob excludes
        analyzeDirectory(dir, Collections.<String>emptyList(), false, Collections.<String>emptyList(), Collections.<String>emptyList());
    }

    /**
     * Analyze directory with optional excludes and parallel option.
     * Excludes are treated as path prefixes to skip (case-insensitive on Windows).
     */
    public void analyzeDirectory(File dir, List<String> excludes, boolean parallel) {
        analyzeDirectory(dir, excludes, parallel, Collections.<String>emptyList(), Collections.<String>emptyList());
    }

    /*
     * Analyze directory with optional excludes, parallel option, extension filters, and glob excludes.
     * - excludes: path prefixes to skip (canonicalized)
     * - extensions: list like ".java", ".py" to include only those extensions (empty = all)
     * - globExcludes: glob patterns (for example, node_modules folders) to skip
     */
    public void analyzeDirectory(File dir, List<String> excludes, boolean parallel, List<String> extensions, List<String> globExcludes) {
        System.out.println("\n📂 Scanning directory: " + dir.getAbsolutePath());
        List<File> files = FileUtils.getAllFiles(dir);

        if (files == null || files.isEmpty()) {
            System.out.println("✗ No files found");
            return;
        }

        // Normalize excludes
        List<String> normExcludes = new ArrayList<>();
        for (String e : excludes) {
            if (e == null || e.isEmpty()) continue;
            String p = e.replaceAll("/","\\\\");
            try {
                p = new File(p).getCanonicalPath();
            } catch (IOException ioe) {
                // fallback to raw
            }
            normExcludes.add(p.toLowerCase(Locale.ROOT));
        }

        // Normalize extensions (lowercase, ensure leading dot)
        List<String> normExt = new ArrayList<>();
        if (extensions != null) {
            for (String ex : extensions) {
                if (ex == null || ex.trim().isEmpty()) continue;
                String e = ex.trim().toLowerCase(Locale.ROOT);
                if (!e.startsWith(".")) e = "." + e;
                normExt.add(e);
            }
        }

        // Prepare glob matchers
        List<PathMatcher> globMatchers = new ArrayList<>();
        if (globExcludes != null) {
            for (String g : globExcludes) {
                if (g == null || g.trim().isEmpty()) continue;
                try {
                    PathMatcher pm = FileSystems.getDefault().getPathMatcher("glob:" + g);
                    globMatchers.add(pm);
                } catch (Exception ignore) {
                }
            }
        }

        // Filter files by excludes
        List<File> toAnalyze = new ArrayList<>();
        for (File f : files) {
            try {
                String fp = f.getCanonicalPath();
                boolean skip = false;
                for (String ex : normExcludes) {
                    if (ex.length() == 0) continue;
                    if (fp.toLowerCase(Locale.ROOT).startsWith(ex)) { skip = true; break; }
                }
                if (skip) continue;

                // Glob excludes
                java.nio.file.Path p = f.toPath();
                for (PathMatcher pm : globMatchers) {
                    if (pm.matches(p)) { skip = true; break; }
                }
                if (skip) continue;

                // Extension filter
                if (!normExt.isEmpty()) {
                    String name = f.getName().toLowerCase(Locale.ROOT);
                    boolean matched = false;
                    for (String e : normExt) if (name.endsWith(e)) { matched = true; break; }
                    if (!matched) continue;
                }
                if (!skip) toAnalyze.add(f);
            } catch (IOException ioe) {
                // If canonicalize fails, include the file
                toAnalyze.add(f);
            }
        }

        System.out.println("✓ Found " + toAnalyze.size() + " files to analyze\n");

        if (toAnalyze.isEmpty()) return;

        if (!parallel) {
            for (File file : toAnalyze) {
                analyzeFile(file);
            }
            return;
        }

        // Parallel processing
        int threads = Math.max(1, Runtime.getRuntime().availableProcessors());
        java.util.concurrent.ExecutorService exec = java.util.concurrent.Executors.newFixedThreadPool(threads);
        List<JavaCodeMetrics> results = Collections.synchronizedList(new ArrayList<>());

        for (final File file : toAnalyze) {
            exec.submit(() -> {
                try {
                    List<String> lines = FileUtils.readFileLines(file);
                    JavaCodeMetrics metrics = new JavaCodeMetrics(file.getName());
                    
                    // Language-specific analysis
                    utils.LanguageDetector.getHeuristicForFile(file).analyze(lines, metrics);
                    
                    // Compute advanced metrics
                    AdvancedMetrics advancedMetrics = new AdvancedMetrics();
                    HalsteadCalculator.computeHalstead(lines, advancedMetrics);
                    int cognitiveComplexity = CognitiveComplexityCalculator.computeCognitiveComplexity(lines);
                    advancedMetrics.setCognitiveComplexity(cognitiveComplexity);
                    advancedMetrics.computeMaintainabilityIndex(metrics.getCyclomaticComplexity(), metrics.getCodeLines());
                    advancedMetrics.setMaxMethodComplexity(metrics.getCyclomaticComplexity());
                    advancedMetrics.setAvgMethodComplexity(metrics.getMethodCount() > 0 ? 
                        metrics.getCyclomaticComplexity() / metrics.getMethodCount() : 0);
                    metrics.setAdvancedMetrics(advancedMetrics);
                    
                    // Code churn analysis (if git repo)
                    File gitRoot = ChurnAnalyzer.findGitRoot(file);
                    if (gitRoot != null) {
                        ChurnMetrics churnMetrics = ChurnAnalyzer.analyzeChurn(file, gitRoot);
                        if (churnMetrics != null) {
                            metrics.setChurnMetrics(churnMetrics);
                            double riskScore = computeRiskScore(metrics);
                            advancedMetrics.setRiskScore(riskScore);
                        }
                    }
                    
                    // Secrets detection
                    try {
                        SecretsDetector.detectSecrets(lines, metrics);
                    } catch (Throwable t) {
                        // non-fatal
                    }
                    
                    results.add(metrics);
                    System.out.println("\n✓ Analysis complete: " + file.getName());
                } catch (IOException e) {
                    System.out.println("✗ Error reading file: " + file.getAbsolutePath() + " -> " + e.getMessage());
                }
            });
        }

        exec.shutdown();
        try {
            exec.awaitTermination(1, java.util.concurrent.TimeUnit.HOURS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        // Merge results into allMetrics
        synchronized (allMetrics) {
            allMetrics.addAll(results);
        }
    }
    
    /**
     * Computes risk score combining complexity, churn, duplication, and coverage.
     * Formula: riskScore = (CC_norm * 0.3) + (churn_norm * 0.25) + (dup_norm * 0.2) + ((100-coverage) * 0.25)
     * Higher score = higher risk (0-100 scale)
     */
    private double computeRiskScore(JavaCodeMetrics metrics) {
        double ccScore = Math.min(100, metrics.getCyclomaticComplexity() * 5); // normalize CC (assume max 20)
        double churnScore = 0;
        
        if (metrics.getChurnMetrics() != null) {
            // Normalize churn rate (assume max 10 commits/day = 100 risk)
            churnScore = Math.min(100, metrics.getChurnMetrics().getChurnRate() * 10);
        }
        
        double dupScore = metrics.getDuplicationPercentage(); // already 0-100
        double coverageScore = 100 - metrics.getTestCoverage(); // invert: low coverage = high risk
        
        double riskScore = (ccScore * 0.3) + (churnScore * 0.25) + (dupScore * 0.2) + (coverageScore * 0.25);
        return Math.min(100, Math.max(0, riskScore));
    }

    private void detectPatterns(List<String> lines, JavaCodeMetrics metrics) {
        // Count method definitions
        int methodCount = 0;
        int classCount = 0;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.contains("public ") && trimmed.contains("(")) {
                methodCount++;
            }
            if (trimmed.startsWith("public class ") || trimmed.startsWith("class ")) {
                classCount++;
            }
        }

        metrics.setMethodCount(methodCount);
        metrics.setClassCount(classCount);
    }

    public void findDuplicateCode(File dir) {
        System.out.println("\n🔍 Searching for duplicate code blocks...");

        Map<String, Integer> codeBlockFrequency = new HashMap<>();
        List<File> files = FileUtils.getAllFiles(dir);

        if (files == null || files.isEmpty()) return;

        for (File file : files) {
            try {
                List<String> lines = FileUtils.readFileLines(file);

                // Find 3+ line blocks
                for (int i = 0; i < lines.size() - 2; i++) {
                    String block = lines.get(i) + "\n" + 
                                 lines.get(i+1) + "\n" + 
                                 lines.get(i+2);

                    codeBlockFrequency.put(block, 
                        codeBlockFrequency.getOrDefault(block, 0) + 1);
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        // Find duplicates
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(codeBlockFrequency.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("\n📊 Duplicate Code Blocks Found:\n");
        int count = 0;
        for (Map.Entry<String, Integer> entry : sorted) {
            if (entry.getValue() > 1 && count < 5) {
                System.out.println("Found " + entry.getValue() + " times:");
                System.out.println(entry.getKey());
                System.out.println("---");
                count++;
            }
        }
    }

    public void printAnalysis() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    DETAILED ANALYSIS RESULTS                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝\n");

        double totalCyclomaticComplexity = 0;
        double totalCodeLines = 0;
        double totalCommentRatio = 0;
        double totalMI = 0;
        int miCount = 0;

        for (JavaCodeMetrics metric : allMetrics) {
            System.out.println("┌──────────────────────────────────────────────────────────────────────┐");
            System.out.printf("│ 📄 FILE: %-60s │\n", metric.getFileName());
            System.out.println("├──────────────────────────────────────────────────────────────────────┤");
            System.out.printf("│   Lines of Code: %-51d │\n", metric.getCodeLines());
            System.out.printf("│   Cyclomatic Complexity: %-43d │\n", metric.getCyclomaticComplexity());
            System.out.printf("│   Documentation: %-50.1f%% │\n", metric.getCommentRatio());
            
            // Advanced Metrics
            if (metric.getAdvancedMetrics() != null) {
                AdvancedMetrics am = metric.getAdvancedMetrics();
                System.out.println("│                                                                      │");
                System.out.println("│ 📊 ADVANCED METRICS:                                                 │");
                
                // Maintainability Index with color indicator
                String miStatus = getMIStatus(am.getMaintainabilityIndex());
                System.out.printf("│   • Maintainability Index: %-25.1f %s │\n", 
                    am.getMaintainabilityIndex(), miStatus);
                System.out.printf("│   • Cognitive Complexity: %-42d │\n", am.getCognitiveComplexity());
                System.out.printf("│   • Halstead Volume: %-47.1f │\n", am.getVolume());
                System.out.printf("│   • Halstead Difficulty: %-43.1f │\n", am.getDifficulty());
                System.out.printf("│   • Halstead Effort: %-47.1f │\n", am.getEffort());
                if (am.getRiskScore() > 0) {
                    System.out.printf("│   • Risk Score: %-40.1f/100 │\n", am.getRiskScore());
                }
                totalMI += am.getMaintainabilityIndex();
                miCount++;
            }
            
            // Churn Metrics
            if (metric.getChurnMetrics() != null) {
                ChurnMetrics cm = metric.getChurnMetrics();
                System.out.println("│                                                                      │");
                System.out.println("│ 🔄 CODE CHURN:                                                       │");
                System.out.printf("│   • Commits: %-55d │\n", cm.getCommitCount());
                System.out.printf("│   • Lines Added: %-51d │\n", cm.getLinesAdded());
                System.out.printf("│   • Lines Deleted: %-49d │\n", cm.getLinesDeleted());
                System.out.printf("│   • Churn Rate: %-39.2f commits/day │\n", cm.getChurnRate());
                System.out.printf("│   • Authors: %-55d │\n", cm.getAuthorsCount());
            }
            
            // Findings
            if (!metric.getFindings().isEmpty()) {
                System.out.println("│                                                                      │");
                System.out.println("│ ⚠️  FINDINGS:                                                         │");
                for (String f : metric.getFindings()) {
                    String truncated = f.length() > 60 ? f.substring(0, 57) + "..." : f;
                    System.out.printf("│   • %-64s │\n", truncated);
                }
            }
            
            System.out.println("└──────────────────────────────────────────────────────────────────────┘\n");

            totalCyclomaticComplexity += metric.getCyclomaticComplexity();
            totalCodeLines += metric.getCodeLines();
            totalCommentRatio += metric.getCommentRatio();
        }

        if (!allMetrics.isEmpty()) {
            System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                           📈 SUMMARY                                 ║");
            System.out.println("╠══════════════════════════════════════════════════════════════════════╣");
            System.out.printf("║  Average Complexity: %-49.1f ║\n", totalCyclomaticComplexity / allMetrics.size());
            System.out.printf("║  Average Comment Ratio: %-46.1f%% ║\n", totalCommentRatio / allMetrics.size());
            if (miCount > 0) {
                System.out.printf("║  Average Maintainability Index: %-39.1f ║\n", totalMI / miCount);
            }
            System.out.printf("║  Total Code Lines: %-50.0f ║\n", totalCodeLines);
            System.out.printf("║  Total Files Analyzed: %-45d ║\n", allMetrics.size());
            System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
        }
    }
    
    /**
     * Returns a visual status indicator for Maintainability Index
     */
    private String getMIStatus(double mi) {
        if (mi < 20) {
            return "🔴 CRITICAL";
        } else if (mi < 50) {
            return "🟡 MODERATE";
        } else {
            return "🟢 GOOD    ";
        }
    }

    private String assessQuality(JavaCodeMetrics metric) {
        int complexity = metric.getCyclomaticComplexity();
        double commentRatio = metric.getCommentRatio();

        if (complexity <= 5 && commentRatio >= 20) {
            return "⭐⭐⭐ EXCELLENT";
        } else if (complexity <= 10 && commentRatio >= 15) {
            return "⭐⭐ GOOD";
        } else if (complexity <= 15) {
            return "⭐ FAIR";
        } else {
            return "⚠ NEEDS IMPROVEMENT";
        }
    }

    public void generateDetailedReport() {
        generateDetailedReport("text");
    }

    /**
     * Generate report in specified format: "text" (default), "json", "csv", or "html".
     */
    public void generateDetailedReport(String format) {
        if (format == null) format = "text";
        format = format.toLowerCase(Locale.ROOT);
        
        if ("html".equals(format)) {
            HtmlReportGenerator.generateHtmlReport(allMetrics, "output/dashboard.html");
            return;
        }

        if ("json".equals(format)) {
            try {
                String reportPath = "output/code_analysis_report.json";
                try (FileOutputStream fos = new FileOutputStream(reportPath);
                     PrintStream ps = new PrintStream(fos)) {

                    ps.println("{");
                    ps.println("  \"generated\": \"" + new java.util.Date() + "\",");
                    ps.println("  \"files\": [");

                    for (int i = 0; i < allMetrics.size(); i++) {
                        JavaCodeMetrics m = allMetrics.get(i);
                        ps.println("    {");
                        ps.println("      \"file\": \"" + m.getFileName() + "\",");
                        ps.println("      \"totalLines\": " + m.getTotalLines() + ",");
                        ps.println("      \"codeLines\": " + m.getCodeLines() + ",");
                        ps.println("      \"commentLines\": " + m.getCommentLines() + ",");
                        ps.println("      \"blankLines\": " + m.getBlankLines() + ",");
                        ps.println("      \"complexity\": " + m.getCyclomaticComplexity() + ",");
                        ps.println("      \"documentation\": \"" + String.format("%.1f%%", m.getCommentRatio()) + "\",");
                        ps.println("      \"methods\": " + m.getMethodCount() + ",");
                        ps.println("      \"classes\": " + m.getClassCount() + ",");
                        
                        // Advanced metrics
                        if (m.getAdvancedMetrics() != null) {
                            AdvancedMetrics am = m.getAdvancedMetrics();
                            ps.println("      \"maintainabilityIndex\": " + String.format("%.1f", am.getMaintainabilityIndex()) + ",");
                            ps.println("      \"cognitiveComplexity\": " + am.getCognitiveComplexity() + ",");
                            ps.println("      \"halsteadVolume\": " + String.format("%.1f", am.getVolume()) + ",");
                            ps.println("      \"halsteadDifficulty\": " + String.format("%.1f", am.getDifficulty()) + ",");
                            ps.println("      \"halsteadEffort\": " + String.format("%.1f", am.getEffort()) + ",");
                            ps.println("      \"riskScore\": " + String.format("%.1f", am.getRiskScore()) + ",");
                        }
                        
                        // Churn metrics
                        if (m.getChurnMetrics() != null) {
                            ChurnMetrics cm = m.getChurnMetrics();
                            ps.println("      \"churn\": {");
                            ps.println("        \"commits\": " + cm.getCommitCount() + ",");
                            ps.println("        \"linesAdded\": " + cm.getLinesAdded() + ",");
                            ps.println("        \"linesDeleted\": " + cm.getLinesDeleted() + ",");
                            ps.println("        \"churnRate\": " + String.format("%.2f", cm.getChurnRate()) + ",");
                            ps.println("        \"authors\": " + cm.getAuthorsCount());
                            ps.println("      },");
                        }
                        
                        ps.println("      \"findings\": [");
                        for (int j = 0; j < m.getFindings().size(); j++) {
                            ps.print("        \"" + m.getFindings().get(j).replace("\"", "\\\"") + "\"");
                            if (j < m.getFindings().size() - 1) ps.println(","); else ps.println();
                        }
                        ps.println("      ]");
                        ps.print("    }");
                        if (i < allMetrics.size() - 1) ps.println(","); else ps.println();
                    }

                    ps.println("  ]");
                    ps.println("}");
                }
                System.out.println("✓ JSON report saved to: " + reportPath);
            } catch (IOException e) {
                System.out.println("Error writing JSON report: " + e.getMessage());
            }
            return;
        }

        if ("csv".equals(format)) {
            generateCsvReport("output/code_analysis_report.csv");
            return;
        }

        // fallback: text
        try {
            String reportPath = "output/code_analysis_report.txt";
            try (FileOutputStream fos = new FileOutputStream(reportPath);
                 PrintStream ps = new PrintStream(fos)) {

                ps.println("════════════════════════════════════════════");
                ps.println("  CODE ANALYSIS DETAILED REPORT");
                ps.println("════════════════════════════════════════════\n");
                ps.println("Generated: " + new java.util.Date() + "\n");

                for (JavaCodeMetrics metric : allMetrics) {
                    ps.println("File: " + metric.getFileName());
                    ps.println("  Lines of Code: " + metric.getCodeLines());
                    ps.println("  Cyclomatic Complexity: " + metric.getCyclomaticComplexity());
                    ps.println("  Documentation: " + String.format("%.1f%%", metric.getCommentRatio()));
                    
                    // Advanced Metrics
                    if (metric.getAdvancedMetrics() != null) {
                        AdvancedMetrics am = metric.getAdvancedMetrics();
                        ps.println("\n  Advanced Metrics:");
                        ps.println("    Maintainability Index: " + String.format("%.1f", am.getMaintainabilityIndex()));
                        ps.println("    Cognitive Complexity: " + am.getCognitiveComplexity());
                        ps.println("    Halstead Volume: " + String.format("%.1f", am.getVolume()));
                        ps.println("    Halstead Difficulty: " + String.format("%.1f", am.getDifficulty()));
                        ps.println("    Halstead Effort: " + String.format("%.1f", am.getEffort()));
                        if (am.getRiskScore() > 0) {
                            ps.println("    Risk Score: " + String.format("%.1f/100", am.getRiskScore()));
                        }
                    }
                    
                    // Churn Metrics
                    if (metric.getChurnMetrics() != null) {
                        ChurnMetrics cm = metric.getChurnMetrics();
                        ps.println("\n  Code Churn:");
                        ps.println("    Commits: " + cm.getCommitCount());
                        ps.println("    Lines Added: " + cm.getLinesAdded());
                        ps.println("    Lines Deleted: " + cm.getLinesDeleted());
                        ps.println("    Churn Rate: " + String.format("%.2f commits/day", cm.getChurnRate()));
                        ps.println("    Authors: " + cm.getAuthorsCount());
                    }
                    
                    // Findings
                    if (!metric.getFindings().isEmpty()) {
                        ps.println("\n  Findings:");
                        for (String f : metric.getFindings()) {
                            ps.println("    - " + f);
                        }
                    }
                    ps.println();
                }
            }
            System.out.println("✓ Report saved to: " + reportPath);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // CSV export
    public void generateCsvReport(String path) {
        try (FileOutputStream fos = new FileOutputStream(path);
             PrintStream ps = new PrintStream(fos)) {
            ps.println("file,totalLines,codeLines,commentLines,blankLines,complexity,commentRatio,methods,classes,maintainabilityIndex,cognitiveComplexity,halsteadVolume,riskScore,commits,churnRate,findings");
            for (JavaCodeMetrics m : allMetrics) {
                String findings = String.join("; ", m.getFindings()).replace("\n", " ").replace(",", " ");
                
                // Get advanced metrics (if available)
                String mi = m.getAdvancedMetrics() != null ? String.format("%.1f", m.getAdvancedMetrics().getMaintainabilityIndex()) : "";
                String cc = m.getAdvancedMetrics() != null ? String.valueOf(m.getAdvancedMetrics().getCognitiveComplexity()) : "";
                String hv = m.getAdvancedMetrics() != null ? String.format("%.1f", m.getAdvancedMetrics().getVolume()) : "";
                String rs = m.getAdvancedMetrics() != null ? String.format("%.1f", m.getAdvancedMetrics().getRiskScore()) : "";
                
                // Get churn metrics (if available)
                String commits = m.getChurnMetrics() != null ? String.valueOf(m.getChurnMetrics().getCommitCount()) : "";
                String churnRate = m.getChurnMetrics() != null ? String.format("%.2f", m.getChurnMetrics().getChurnRate()) : "";
                
                ps.printf("\"%s\",%d,%d,%d,%d,%d,%.1f,%d,%d,%s,%s,%s,%s,%s,%s,\"%s\"\n",
                    m.getFileName(), m.getTotalLines(), m.getCodeLines(), m.getCommentLines(), m.getBlankLines(),
                    m.getCyclomaticComplexity(), m.getCommentRatio(), m.getMethodCount(), m.getClassCount(),
                    mi, cc, hv, rs, commits, churnRate, findings);
            }
            System.out.println("✓ CSV report saved to: " + path);
        } catch (IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
        }
    }
}
