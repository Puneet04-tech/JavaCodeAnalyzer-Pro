package model;

/**
 * Advanced code metrics including Halstead, Maintainability Index, and Cognitive Complexity.
 */
public class AdvancedMetrics {
    
    // Halstead metrics
    private int n1; // distinct operators
    private int n2; // distinct operands
    private int N1; // total operators
    private int N2; // total operands
    private double volume;
    private double difficulty;
    private double effort;
    
    // Cognitive complexity
    private int cognitiveComplexity;
    
    // Maintainability Index
    private double maintainabilityIndex;
    
    // Method/class level
    private int maxMethodComplexity;
    private int avgMethodComplexity;
    private int longestMethodLines;
    private int largestClassLines;
    
    // Coupling/Cohesion
    private int coupling; // afferent + efferent
    private double cohesion; // LCOM or similar
    
    // Risk score (0-100, higher = riskier)
    private double riskScore;
    
    public AdvancedMetrics() {
        this.volume = 0.0;
        this.difficulty = 0.0;
        this.effort = 0.0;
        this.cognitiveComplexity = 0;
        this.maintainabilityIndex = 100.0;
        this.riskScore = 0.0;
    }
    
    // Halstead getters/setters
    public int getN1() { return n1; }
    public void setN1(int n1) { this.n1 = n1; }
    
    public int getN2() { return n2; }
    public void setN2(int n2) { this.n2 = n2; }
    
    public int getTotalN1() { return N1; }
    public void setTotalN1(int N1) { this.N1 = N1; }
    
    public int getTotalN2() { return N2; }
    public void setTotalN2(int N2) { this.N2 = N2; }
    
    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
    
    public double getDifficulty() { return difficulty; }
    public void setDifficulty(double difficulty) { this.difficulty = difficulty; }
    
    public double getEffort() { return effort; }
    public void setEffort(double effort) { this.effort = effort; }
    
    // Cognitive complexity
    public int getCognitiveComplexity() { return cognitiveComplexity; }
    public void setCognitiveComplexity(int cc) { this.cognitiveComplexity = cc; }
    
    // Maintainability Index
    public double getMaintainabilityIndex() { return maintainabilityIndex; }
    public void setMaintainabilityIndex(double mi) { this.maintainabilityIndex = mi; }
    
    // Method/class metrics
    public int getMaxMethodComplexity() { return maxMethodComplexity; }
    public void setMaxMethodComplexity(int max) { this.maxMethodComplexity = max; }
    
    public int getAvgMethodComplexity() { return avgMethodComplexity; }
    public void setAvgMethodComplexity(int avg) { this.avgMethodComplexity = avg; }
    
    public int getLongestMethodLines() { return longestMethodLines; }
    public void setLongestMethodLines(int lines) { this.longestMethodLines = lines; }
    
    public int getLargestClassLines() { return largestClassLines; }
    public void setLargestClassLines(int lines) { this.largestClassLines = lines; }
    
    // Coupling/Cohesion
    public int getCoupling() { return coupling; }
    public void setCoupling(int coupling) { this.coupling = coupling; }
    
    public double getCohesion() { return cohesion; }
    public void setCohesion(double cohesion) { this.cohesion = cohesion; }
    
    // Risk score
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double risk) { this.riskScore = risk; }
    
    /**
     * Compute Halstead volume: V = N * log2(n)
     * where N = N1 + N2, n = n1 + n2
     */
    public void computeHalstead() {
        int N = N1 + N2;
        int n = n1 + n2;
        if (n > 0) {
            volume = N * (Math.log(n) / Math.log(2));
            if (n2 > 0 && N2 > 0) {
                difficulty = (n1 / 2.0) * (N2 / (double) n2);
            }
            effort = difficulty * volume;
        }
    }
    
    /**
     * Compute Maintainability Index using Microsoft formula:
     * MI = MAX(0, (171 - 5.2 * ln(V) - 0.23 * CC - 16.2 * ln(LOC)) * 100 / 171)
     */
    public void computeMaintainabilityIndex(int cyclomaticComplexity, int loc) {
        if (volume <= 0 || loc <= 0) {
            maintainabilityIndex = 100.0;
            return;
        }
        double lnV = Math.log(volume);
        double lnLOC = Math.log(loc);
        double raw = 171 - 5.2 * lnV - 0.23 * cyclomaticComplexity - 16.2 * lnLOC;
        maintainabilityIndex = Math.max(0, (raw * 100) / 171);
    }
}
