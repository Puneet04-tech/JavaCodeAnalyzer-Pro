package utils;

import model.AdvancedMetrics;
import java.util.*;

/**
 * Computes Halstead complexity metrics by analyzing operators and operands in code.
 * Reference: Halstead, M. H. (1977). Elements of Software Science.
 */
public class HalsteadCalculator {
    
    // Java/Python/JS operators
    private static final Set<String> OPERATORS = new HashSet<>(Arrays.asList(
        "+", "-", "*", "/", "%", "=", "==", "!=", "<", ">", "<=", ">=",
        "&&", "||", "!", "&", "|", "^", "~", "<<", ">>", ">>>",
        "++", "--", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=",
        "?", ":", ".", ",", ";", "(", ")", "[", "]", "{", "}",
        "if", "else", "for", "while", "do", "switch", "case", "break", "continue",
        "return", "throw", "try", "catch", "finally", "new", "import", "class",
        "def", "lambda", "async", "await", "yield", "with", "as", "in", "is",
        "function", "var", "let", "const", "=>", "typeof", "instanceof"
    ));
    
    /**
     * Analyzes code lines to compute Halstead metrics and populate AdvancedMetrics object.
     */
    public static void computeHalstead(List<String> lines, AdvancedMetrics metrics) {
        Map<String, Integer> operatorCounts = new HashMap<>();
        Map<String, Integer> operandCounts = new HashMap<>();
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("//") || trimmed.startsWith("#")) continue;
            
            // Tokenize line (simple split by whitespace and symbols)
            String[] tokens = tokenize(trimmed);
            
            for (String token : tokens) {
                if (token.isEmpty()) continue;
                
                if (OPERATORS.contains(token)) {
                    operatorCounts.put(token, operatorCounts.getOrDefault(token, 0) + 1);
                } else if (isOperand(token)) {
                    operandCounts.put(token, operandCounts.getOrDefault(token, 0) + 1);
                }
            }
        }
        
        metrics.setTotalN1(operatorCounts.values().stream().mapToInt(Integer::intValue).sum());
        metrics.setTotalN2(operandCounts.values().stream().mapToInt(Integer::intValue).sum());
        metrics.setN1(operatorCounts.size());
        metrics.setN2(operandCounts.size());
        
        metrics.computeHalstead();
    }
    
    /**
     * Simple tokenizer - splits by whitespace and separates operators.
     */
    private static String[] tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (Character.isWhitespace(c)) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
            } else if (isOperatorChar(c)) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                
                // Handle multi-char operators like ++, ==, <=, etc.
                String op = String.valueOf(c);
                if (i + 1 < line.length()) {
                    char next = line.charAt(i + 1);
                    String twoChar = c + String.valueOf(next);
                    if (OPERATORS.contains(twoChar)) {
                        op = twoChar;
                        i++;
                    }
                }
                tokens.add(op);
            } else {
                token.append(c);
            }
        }
        
        if (token.length() > 0) {
            tokens.add(token.toString());
        }
        
        return tokens.toArray(new String[0]);
    }
    
    private static boolean isOperatorChar(char c) {
        return "+-*/%=!<>&|^~?:.,;()[]{}".indexOf(c) != -1;
    }
    
    private static boolean isOperand(String token) {
        // Operands: variables, literals, function names
        if (token.matches("\\d+")) return true; // numeric literal
        if (token.matches("\".*\"") || token.matches("'.*'")) return true; // string literal
        if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) return true; // identifier
        return false;
    }
}
