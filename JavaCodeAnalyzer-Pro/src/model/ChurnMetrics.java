package model;

/**
 * Code churn and hotspot metrics.
 */
public class ChurnMetrics {
    private int commitCount;
    private int linesAdded;
    private int linesDeleted;
    private int authorsCount;
    private long lastModifiedTimestamp;
    private double churnRate; // changes per day/week
    
    public ChurnMetrics() {
        this.commitCount = 0;
        this.linesAdded = 0;
        this.linesDeleted = 0;
        this.authorsCount = 0;
        this.lastModifiedTimestamp = 0;
        this.churnRate = 0.0;
    }
    
    public int getCommitCount() { return commitCount; }
    public void setCommitCount(int count) { this.commitCount = count; }
    
    public int getLinesAdded() { return linesAdded; }
    public void setLinesAdded(int added) { this.linesAdded = added; }
    
    public int getLinesDeleted() { return linesDeleted; }
    public void setLinesDeleted(int deleted) { this.linesDeleted = deleted; }
    
    public int getAuthorsCount() { return authorsCount; }
    public void setAuthorsCount(int authors) { this.authorsCount = authors; }
    
    public long getLastModifiedTimestamp() { return lastModifiedTimestamp; }
    public void setLastModifiedTimestamp(long ts) { this.lastModifiedTimestamp = ts; }
    
    public double getChurnRate() { return churnRate; }
    public void setChurnRate(double rate) { this.churnRate = rate; }
    
    /**
     * Compute churn rate based on total changes and time span.
     */
    public void computeChurnRate(long firstCommitTimestamp) {
        if (lastModifiedTimestamp > firstCommitTimestamp) {
            long daysSpan = (lastModifiedTimestamp - firstCommitTimestamp) / (1000 * 60 * 60 * 24);
            if (daysSpan > 0) {
                churnRate = (double) commitCount / daysSpan;
            }
        }
    }
}
