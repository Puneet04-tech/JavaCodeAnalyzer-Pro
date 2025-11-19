package utils;

import model.ChurnMetrics;
import java.io.*;
import java.util.*;

/**
 * Analyzes code churn by parsing git log for file history.
 * Requires git to be installed and project to be a git repository.
 */
public class ChurnAnalyzer {
    
    /**
     * Computes churn metrics for a specific file using git log.
     * Returns null if git is not available or file has no history.
     */
    public static ChurnMetrics analyzeChurn(File file, File repoRoot) {
        if (!isGitRepo(repoRoot)) return null;
        
        try {
            String relativePath = getRelativePath(repoRoot, file);
            
            // Get commit count
            int commitCount = getCommitCount(repoRoot, relativePath);
            if (commitCount == 0) return null;
            
            // Get lines added/deleted
            int[] linesChanged = getLinesChanged(repoRoot, relativePath);
            
            // Get author count
            int authorsCount = getAuthorsCount(repoRoot, relativePath);
            
            // Get timestamps
            long[] timestamps = getTimestamps(repoRoot, relativePath);
            
            ChurnMetrics metrics = new ChurnMetrics();
            metrics.setCommitCount(commitCount);
            metrics.setLinesAdded(linesChanged[0]);
            metrics.setLinesDeleted(linesChanged[1]);
            metrics.setAuthorsCount(authorsCount);
            metrics.setLastModifiedTimestamp(timestamps[1]);
            metrics.computeChurnRate(timestamps[0]);
            
            return metrics;
        } catch (Exception e) {
            return null; // git command failed or not available
        }
    }
    
    private static boolean isGitRepo(File dir) {
        File gitDir = new File(dir, ".git");
        return gitDir.exists() && gitDir.isDirectory();
    }
    
    private static String getRelativePath(File base, File file) {
        return base.toURI().relativize(file.toURI()).getPath();
    }
    
    private static int getCommitCount(File repoRoot, String filePath) throws Exception {
        String output = executeGitCommand(repoRoot, "git", "log", "--oneline", "--", filePath);
        return output.trim().isEmpty() ? 0 : output.split("\n").length;
    }
    
    private static int[] getLinesChanged(File repoRoot, String filePath) throws Exception {
        String output = executeGitCommand(repoRoot, "git", "log", "--numstat", "--pretty=format:", "--", filePath);
        int added = 0, deleted = 0;
        
        for (String line : output.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            
            String[] parts = trimmed.split("\\s+");
            if (parts.length >= 2) {
                try {
                    added += Integer.parseInt(parts[0]);
                    deleted += Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    // Binary file or parse error, skip
                }
            }
        }
        
        return new int[]{added, deleted};
    }
    
    private static int getAuthorsCount(File repoRoot, String filePath) throws Exception {
        String output = executeGitCommand(repoRoot, "git", "log", "--format=%ae", "--", filePath);
        Set<String> authors = new HashSet<>();
        for (String email : output.split("\n")) {
            if (!email.trim().isEmpty()) {
                authors.add(email.trim());
            }
        }
        return authors.size();
    }
    
    private static long[] getTimestamps(File repoRoot, String filePath) throws Exception {
        // Get first commit timestamp
        String firstOutput = executeGitCommand(repoRoot, "git", "log", "--reverse", "--format=%ct", "--", filePath);
        long firstTimestamp = 0;
        String[] lines = firstOutput.split("\n");
        if (lines.length > 0 && !lines[0].trim().isEmpty()) {
            firstTimestamp = Long.parseLong(lines[0].trim()) * 1000; // convert to milliseconds
        }
        
        // Get last commit timestamp
        String lastOutput = executeGitCommand(repoRoot, "git", "log", "-1", "--format=%ct", "--", filePath);
        long lastTimestamp = System.currentTimeMillis();
        if (!lastOutput.trim().isEmpty()) {
            lastTimestamp = Long.parseLong(lastOutput.trim()) * 1000;
        }
        
        return new long[]{firstTimestamp, lastTimestamp};
    }
    
    private static String executeGitCommand(File workingDir, String... command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDir);
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        StringBuilder output = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        process.waitFor();
        return output.toString();
    }
    
    /**
     * Finds the git repository root directory by walking up from the given file.
     */
    public static File findGitRoot(File file) {
        File current = file.isDirectory() ? file : file.getParentFile();
        
        while (current != null) {
            if (new File(current, ".git").exists()) {
                return current;
            }
            current = current.getParentFile();
        }
        
        return null;
    }
}
