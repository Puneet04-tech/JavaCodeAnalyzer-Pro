package model;

/**
 * Data model for storing code metrics
 * Pure Java - no frameworks
 */
public class JavaCodeMetrics {

    private String fileName;
    private int totalLines;
    private int codeLines;
    private int commentLines;
    private int blankLines;
    private int cyclomaticComplexity;
    private double commentRatio;
    private int methodCount;
    private int classCount;
    private java.util.List<String> findings;
    private AdvancedMetrics advancedMetrics;
    private ChurnMetrics churnMetrics;
    private int duplicationPercentage;
    private double testCoverage;

    public JavaCodeMetrics(String fileName) {
        this.fileName = fileName;
        this.findings = new java.util.ArrayList<>();
        this.advancedMetrics = new AdvancedMetrics();
        this.churnMetrics = new ChurnMetrics();
        this.duplicationPercentage = 0;
        this.testCoverage = 0.0;
    }

    // Getters and Setters
    public String getFileName() { return fileName; }

    public int getTotalLines() { return totalLines; }
    public void setTotalLines(int totalLines) { this.totalLines = totalLines; }

    public int getCodeLines() { return codeLines; }
    public void setCodeLines(int codeLines) { this.codeLines = codeLines; }

    public int getCommentLines() { return commentLines; }
    public void setCommentLines(int commentLines) { this.commentLines = commentLines; }

    public int getBlankLines() { return blankLines; }
    public void setBlankLines(int blankLines) { this.blankLines = blankLines; }

    public int getCyclomaticComplexity() { return cyclomaticComplexity; }
    public void setCyclomaticComplexity(int complexity) { this.cyclomaticComplexity = complexity; }

    public double getCommentRatio() { return commentRatio; }
    public void setCommentRatio(double ratio) { this.commentRatio = ratio; }

    public int getMethodCount() { return methodCount; }
    public void setMethodCount(int count) { this.methodCount = count; }

    public int getClassCount() { return classCount; }
    public void setClassCount(int count) { this.classCount = count; }

    public java.util.List<String> getFindings() { return findings; }
    public void addFinding(String f) { this.findings.add(f); }
    
    public AdvancedMetrics getAdvancedMetrics() { return advancedMetrics; }
    public void setAdvancedMetrics(AdvancedMetrics am) { this.advancedMetrics = am; }
    
    public ChurnMetrics getChurnMetrics() { return churnMetrics; }
    public void setChurnMetrics(ChurnMetrics cm) { this.churnMetrics = cm; }
    
    public int getDuplicationPercentage() { return duplicationPercentage; }
    public void setDuplicationPercentage(int dup) { this.duplicationPercentage = dup; }
    
    public double getTestCoverage() { return testCoverage; }
    public void setTestCoverage(double coverage) { this.testCoverage = coverage; }
}
