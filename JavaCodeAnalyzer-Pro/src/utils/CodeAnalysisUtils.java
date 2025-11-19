package utils;

import java.util.*;

/**
 * Analysis utilities - demonstrates algorithmic thinking
 * Pure Java algorithms - NO external libraries
 */
public class CodeAnalysisUtils {

    /**
     * Find patterns in code using HashMap
     * Demonstrates: Collections, Map, String processing
     */
    public static Map<String, Integer> findPatterns(List<String> codeLines) {
        Map<String, Integer> patterns = new HashMap<>();

        for (String line : codeLines) {
            String trimmed = line.trim();

            // Extract keywords
            if (trimmed.contains("public")) {
                patterns.merge("public_keyword", 1, Integer::sum);
            }
            if (trimmed.contains("private")) {
                patterns.merge("private_keyword", 1, Integer::sum);
            }
            if (trimmed.contains("protected")) {
                patterns.merge("protected_keyword", 1, Integer::sum);
            }
            if (trimmed.contains("static")) {
                patterns.merge("static_keyword", 1, Integer::sum);
            }
            if (trimmed.contains("final")) {
                patterns.merge("final_keyword", 1, Integer::sum);
            }
        }

        return patterns;
    }

    /**
     * Sort patterns by frequency
     * Demonstrates: Collections.sort, Comparator, custom sorting
     */
    public static List<Map.Entry<String, Integer>> sortByFrequency(
            Map<String, Integer> patterns) {

        List<Map.Entry<String, Integer>> list = new ArrayList<>(patterns.entrySet());

        // Sort using Comparator - core Java
        Collections.sort(list, (a, b) -> b.getValue().compareTo(a.getValue()));

        return list;
    }

    /**
     * Calculate complexity score using TreeMap (sorted map)
     */
    public static int calculateComplexity(List<String> lines) {
        int complexity = 1;

        // TreeMap maintains sorted order - demonstrates data structure knowledge
        Map<String, Integer> keywordCount = new TreeMap<>();

        for (String line : lines) {
            if (line.contains("if")) keywordCount.merge("if", 1, Integer::sum);
            if (line.contains("for")) keywordCount.merge("for", 1, Integer::sum);
            if (line.contains("while")) keywordCount.merge("while", 1, Integer::sum);
            if (line.contains("switch")) keywordCount.merge("switch", 1, Integer::sum);
        }

        // Sum all complexity contributors
        for (int count : keywordCount.values()) {
            complexity += count;
        }

        return complexity;
    }

    /**
     * Find longest method using custom comparator
     * Demonstrates: Comparator, Algorithm design
     */
    public static int findLongestMethodLength(List<String> lines) {
        int maxLength = 0;
        int currentMethodLength = 0;
        boolean inMethod = false;

        for (String line : lines) {
            String trimmed = line.trim();

            // Detect method start
            if ((trimmed.contains("public ") || trimmed.contains("private ")) 
                && trimmed.contains("(") && trimmed.contains(")")) {
                inMethod = true;
                currentMethodLength = 0;
            }

            if (inMethod) {
                currentMethodLength++;

                // Detect method end
                if (trimmed.equals("}")) {
                    maxLength = Math.max(maxLength, currentMethodLength);
                    inMethod = false;
                }
            }
        }

        return maxLength;
    }

    /**
     * Detect code violations using Set (no duplicates)
     */
    public static Set<String> detectViolations(List<String> lines) {
        Set<String> violations = new HashSet<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            // Check for violations
            if (line.length() > 120) {
                violations.add("Line " + (i+1) + ": Exceeds 120 chars");
            }

            if (line.contains("TODO")) {
                violations.add("Line " + (i+1) + ": Contains TODO");
            }

            if (line.contains("FIXME")) {
                violations.add("Line " + (i+1) + ": Contains FIXME");
            }
        }

        return violations;
    }
}
