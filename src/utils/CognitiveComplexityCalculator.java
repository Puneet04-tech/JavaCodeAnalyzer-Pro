package utils;

import java.util.*;

/**
 * Computes Cognitive Complexity as defined by SonarSource.
 * Reference: G. Ann Campbell (2018), "Cognitive Complexity - A new way of measuring understandability"
 * 
 * Cognitive Complexity increments:
 * - +1 for if, else if, ternary ?, switch, for, while, do-while, catch, && in conditions, || in conditions
 * - +1 per nesting level for break/continue with labels
 * - Nesting penalty: increment nested control flow by current nesting depth
 */
public class CognitiveComplexityCalculator {
    
    public static int computeCognitiveComplexity(List<String> lines) {
        int complexity = 0;
        int nestingLevel = 0;
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("//") || trimmed.startsWith("#")) continue;
            
            // Track nesting depth
            int openBraces = countOccurrences(trimmed, '{');
            int closeBraces = countOccurrences(trimmed, '}');
            
            // Increment complexity for control flow keywords
            if (matchesControlFlow(trimmed)) {
                complexity += 1 + nestingLevel; // +1 base + nesting penalty
            }
            
            // Increment for logical operators in conditions (each && or ||)
            if (trimmed.contains("if") || trimmed.contains("while") || trimmed.contains("for")) {
                complexity += countOccurrences(trimmed, "&&");
                complexity += countOccurrences(trimmed, "||");
            }
            
            // Increment for catch blocks
            if (trimmed.startsWith("catch") || trimmed.contains("} catch")) {
                complexity += 1 + nestingLevel;
            }
            
            // Update nesting level after processing line
            nestingLevel += openBraces;
            nestingLevel -= closeBraces;
            if (nestingLevel < 0) nestingLevel = 0; // safety
        }
        
        return complexity;
    }
    
    private static boolean matchesControlFlow(String line) {
        return line.startsWith("if") || line.startsWith("else if") || line.startsWith("else") ||
               line.startsWith("for") || line.startsWith("while") || line.startsWith("do") ||
               line.startsWith("switch") || line.contains("? ") || // ternary
               line.contains("elif ");
    }
    
    private static int countOccurrences(String str, String substr) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(substr, index)) != -1) {
            count++;
            index += substr.length();
        }
        return count;
    }
    
    private static int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) count++;
        }
        return count;
    }
}
