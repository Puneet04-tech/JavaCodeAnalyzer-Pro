package utils;

import java.util.*;
import model.JavaCodeMetrics;

/**
 * Pluggable language-specific heuristics for analyzing files.
 * Implementations should fill `JavaCodeMetrics` based on the file lines.
 */
public interface LanguageHeuristic {
    void analyze(List<String> lines, JavaCodeMetrics metrics);
}

/**
 * Generic heuristic: simple token-based counts (works for any plain-text file).
 */
class GenericHeuristic implements LanguageHeuristic {
    @Override
    public void analyze(List<String> lines, JavaCodeMetrics metrics) {
        int commentLines = 0;
        int blankLines = 0;
        int codeLines = 0;
        int complexity = 1;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                blankLines++;
            } else if (trimmed.startsWith("//") || trimmed.startsWith("#") || trimmed.startsWith("/*") || trimmed.startsWith("*")) {
                commentLines++;
            } else {
                codeLines++;
                String t = trimmed;
                if (t.contains("if ") || t.contains("if(") || t.contains("elif ") || t.contains("elif(")) complexity++;
                if (t.contains("for ") || t.contains("for(") || t.contains("foreach")) complexity++;
                if (t.contains("while ") || t.contains("while(")) complexity++;
                if (t.contains("switch")) complexity++;
                if (t.contains("catch") || t.contains("except")) complexity++;
            }
        }

        metrics.setTotalLines(lines.size());
        metrics.setCodeLines(codeLines);
        metrics.setCommentLines(commentLines);
        metrics.setBlankLines(blankLines);
        metrics.setCyclomaticComplexity(complexity);
        metrics.setCommentRatio(lines.size() == 0 ? 0.0 : commentLines * 100.0 / lines.size());

        // Best-effort method/class counts (very generic)
        int methodCount = 0;
        int classCount = 0;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.matches(".*(def |function |public |private |protected ).*\\(.*\\).*")) methodCount++;
            if (trimmed.matches(".*(class |interface |struct ).*")) classCount++;
        }
        metrics.setMethodCount(methodCount);
        metrics.setClassCount(classCount);
    }
}

/**
 * Python-specific heuristics.
 */
class PythonHeuristic implements LanguageHeuristic {
    @Override
    public void analyze(List<String> lines, JavaCodeMetrics metrics) {
        int commentLines = 0;
        int blankLines = 0;
        int codeLines = 0;
        int complexity = 1;

        boolean inTriple = false;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                blankLines++;
                continue;
            }

            // Simple triple-quote detection for docstrings
            if (trimmed.startsWith("\"\"\"") || trimmed.startsWith("'''")) {
                inTriple = !inTriple;
                commentLines++;
                continue;
            }
            if (inTriple) {
                commentLines++;
                continue;
            }

            if (trimmed.startsWith("#")) {
                commentLines++;
                continue;
            }

            codeLines++;
            String t = trimmed;
            if (t.startsWith("def ") || t.contains(" def ")) metrics.setMethodCount(metrics.getMethodCount() + 1);
            if (t.startsWith("class ") || t.contains(" class ")) metrics.setClassCount(metrics.getClassCount() + 1);

            if (t.startsWith("if ") || t.contains(" if ") || t.contains("elif ") || t.contains("else:")) complexity++;
            if (t.startsWith("for ") || t.contains(" for ")) complexity++;
            if (t.startsWith("while ") || t.contains(" while ")) complexity++;
            if (t.contains("except") || t.contains("with ")) complexity++;
        }

        metrics.setTotalLines(lines.size());
        metrics.setCodeLines(codeLines);
        metrics.setCommentLines(commentLines);
        metrics.setBlankLines(blankLines);
        metrics.setCyclomaticComplexity(complexity);
        metrics.setCommentRatio(lines.size() == 0 ? 0.0 : commentLines * 100.0 / lines.size());
    }
}

/**
 * JavaScript-specific heuristics.
 */
class JavaScriptHeuristic implements LanguageHeuristic {
    @Override
    public void analyze(List<String> lines, JavaCodeMetrics metrics) {
        int commentLines = 0;
        int blankLines = 0;
        int codeLines = 0;
        int complexity = 1;

        boolean inBlock = false;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) { blankLines++; continue; }

            if (trimmed.startsWith("/*")) { inBlock = true; commentLines++; continue; }
            if (inBlock) { commentLines++; if (trimmed.endsWith("*/")) inBlock = false; continue; }
            if (trimmed.startsWith("//")) { commentLines++; continue; }

            codeLines++;
            String t = trimmed;
            if (t.contains("function ") || t.contains("=>")) metrics.setMethodCount(metrics.getMethodCount() + 1);
            if (t.startsWith("class ") || t.contains(" class ")) metrics.setClassCount(metrics.getClassCount() + 1);

            if (t.contains("if ") || t.contains("if(")) complexity++;
            if (t.contains("for ") || t.contains("for(")) complexity++;
            if (t.contains("while ") || t.contains("while(")) complexity++;
            if (t.contains("switch")) complexity++;
            if (t.contains("catch")) complexity++;
        }

        metrics.setTotalLines(lines.size());
        metrics.setCodeLines(codeLines);
        metrics.setCommentLines(commentLines);
        metrics.setBlankLines(blankLines);
        metrics.setCyclomaticComplexity(complexity);
        metrics.setCommentRatio(lines.size() == 0 ? 0.0 : commentLines * 100.0 / lines.size());
    }
}
