package utils;

import java.util.*;
import java.util.regex.*;

import model.JavaCodeMetrics;

/**
 * Simple secrets detector using regex patterns and entropy checks.
 * This is heuristic-based and will produce false positives — configure per-repo as needed.
 */
public class SecretsDetector {

    // Common secret regexes (basic, not exhaustive)
    private static final Pattern AWS_ACCESS_KEY = Pattern.compile("A(3|K)IA[0-9A-Z]{16}");
    private static final Pattern GENERIC_BASE64 = Pattern.compile("[A-Za-z0-9_\\-]{20,}");
    private static final Pattern PRIVATE_KEY_BEGIN = Pattern.compile("-----BEGIN (RSA|PRIVATE) KEY-----");
    private static final Pattern POSSIBLE_TOKEN = Pattern.compile("(?i)(api|secret|token|passwd|password|key)[\"'\s:=]{0,5}[A-Za-z0-9_\\-]{8,}");

    public static void detectSecrets(List<String> lines, JavaCodeMetrics metrics) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (AWS_ACCESS_KEY.matcher(line).find()) {
                metrics.addFinding("Possible AWS access key at line " + (i+1));
            }

            if (PRIVATE_KEY_BEGIN.matcher(line).find()) {
                metrics.addFinding("Possible embedded private key at line " + (i+1));
            }

            Matcher m = POSSIBLE_TOKEN.matcher(line);
            if (m.find()) {
                String match = m.group();
                // crude entropy check for base64-like token
                Matcher b64 = GENERIC_BASE64.matcher(match);
                if (b64.find() && shannonEntropy(match) > 4.2) {
                    metrics.addFinding("High-entropy token-like string at line " + (i+1));
                } else {
                    metrics.addFinding("Possible token-like string at line " + (i+1));
                }
            }
        }
    }

    // Shannon entropy over characters
    private static double shannonEntropy(String s) {
        int[] freq = new int[256];
        for (char c : s.toCharArray()) freq[c]++;
        double res = 0.0;
        int len = s.length();
        for (int f : freq) {
            if (f == 0) continue;
            double p = (double) f / len;
            res -= p * (Math.log(p) / Math.log(2));
        }
        return res;
    }
}
