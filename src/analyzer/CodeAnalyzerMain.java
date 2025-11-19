package analyzer;

import java.io.File;
import java.util.*;

/**
 * CodeAnalyzer Pro - Pure Java Core Edition
 * Analyzes files for quality metrics and patterns (not limited to Java)
 * 
 * ONLY uses: java.io, java.util, java.time, java.lang
 * NO frameworks, NO external libraries
 */
public class CodeAnalyzerMain {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   CodeAnalyzer Pro - Pure Java Core   ║");
        System.out.println("║        File Code Quality Analyzer     ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        CodeAnalyzer analyzer = new CodeAnalyzer();

        // If args provided, run non-interactively: first arg is directory
        if (args != null && args.length > 0) {
            String dirPath = args[0];
            boolean parallel = false;
            String format = "text";
            List<String> excludes = new ArrayList<>();
            List<String> extensions = new ArrayList<>();
            List<String> globExcludes = new ArrayList<>();

            for (int i = 1; i < args.length; i++) {
                String a = args[i];
                if ("--parallel".equalsIgnoreCase(a) || "-p".equalsIgnoreCase(a)) {
                    parallel = true;
                } else if (a.startsWith("--format=")) {
                    format = a.substring(a.indexOf('=') + 1);
                } else if (a.startsWith("--ext=")) {
                    String raw = a.substring(a.indexOf('=') + 1);
                    String[] parts = raw.split(",");
                    for (String s : parts) if (!s.isEmpty()) extensions.add(s.trim());
                } else if (a.startsWith("--exclude-glob=")) {
                    String raw = a.substring(a.indexOf('=') + 1);
                    String[] parts = raw.split(";");
                    for (String s : parts) if (!s.isEmpty()) globExcludes.add(s.trim());
                } else if (a.startsWith("--exclude=")) {
                    String raw = a.substring(a.indexOf('=') + 1);
                    // multiple excludes separated by semicolon
                    String[] parts = raw.split(";");
                    for (String s : parts) if (!s.isEmpty()) excludes.add(s);
                }
            }

            File dir = new File(dirPath);
            if (!dir.isDirectory()) {
                System.out.println("✗ Directory not found: " + dirPath);
                return;
            }
            analyzer.analyzeDirectory(dir, excludes, parallel, extensions, globExcludes);
            analyzer.printAnalysis();
            analyzer.generateDetailedReport(format);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    analyzeFile(scanner, analyzer);
                    break;
                case "2":
                    analyzeDirectory(scanner, analyzer);
                    break;
                case "3":
                    detectDuplicates(scanner, analyzer);
                    break;
                case "4":
                    generateReport(analyzer);
                    break;
                case "5":
                    running = false;
                    System.out.println("\n✓ Thank you for using CodeAnalyzer Pro!");
                    break;
                default:
                    System.out.println("✗ Invalid choice");
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              MAIN MENU                ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  1. Analyze Single File               ║");
        System.out.println("║  2. Analyze Directory                 ║");
        System.out.println("║  3. Find Duplicate Code               ║");
        System.out.println("║  4. Generate Detailed Report          ║");
        System.out.println("║  5. Exit                              ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("Enter choice: ");
    }

    private static void analyzeFile(Scanner scanner, CodeAnalyzer analyzer) {
        System.out.print("\nEnter file path: ");
        String filePath = scanner.nextLine().trim();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("✗ File not found");
            return;
        }

        analyzer.analyzeFile(file);
        analyzer.printAnalysis();
    }

    private static void analyzeDirectory(Scanner scanner, CodeAnalyzer analyzer) {
        System.out.print("\nEnter directory path: ");
        String dirPath = scanner.nextLine().trim();
        File dir = new File(dirPath);

        if (!dir.isDirectory()) {
            System.out.println("✗ Directory not found");
            return;
        }

        analyzer.analyzeDirectory(dir);
        analyzer.printAnalysis();
    }

    private static void detectDuplicates(Scanner scanner, CodeAnalyzer analyzer) {
        System.out.print("\nEnter directory path: ");
        String dirPath = scanner.nextLine().trim();
        File dir = new File(dirPath);

        if (!dir.isDirectory()) {
            System.out.println("✗ Directory not found");
            return;
        }

        analyzer.findDuplicateCode(dir);
    }

    private static void generateReport(CodeAnalyzer analyzer) {
        System.out.println("\n📊 Generating detailed report...");
        analyzer.generateDetailedReport();
        System.out.println("✓ Report generated successfully!");
    }
}
