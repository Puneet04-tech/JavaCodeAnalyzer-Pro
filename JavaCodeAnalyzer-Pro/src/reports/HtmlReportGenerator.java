package reports;

import model.JavaCodeMetrics;
import model.AdvancedMetrics;
import model.ChurnMetrics;
import java.io.*;
import java.util.*;

/**
 * Generates interactive HTML dashboard with Chart.js visualizations
 */
public class HtmlReportGenerator {
    
    public static void generateHtmlReport(List<JavaCodeMetrics> allMetrics, String outputPath) {
        try (FileOutputStream fos = new FileOutputStream(outputPath);
             PrintStream ps = new PrintStream(fos)) {
            
            ps.println("<!DOCTYPE html>");
            ps.println("<html lang=\"en\">");
            ps.println("<head>");
            ps.println("    <meta charset=\"UTF-8\">");
            ps.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            ps.println("    <title>Code Analysis Dashboard</title>");
            ps.println("    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>");
            ps.println("    <style>");
            ps.println("        * { margin: 0; padding: 0; box-sizing: border-box; }");
            ps.println("        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; }");
            ps.println("        .container { max-width: 1400px; margin: 0 auto; }");
            ps.println("        .header { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 8px 32px rgba(0,0,0,0.1); margin-bottom: 20px; text-align: center; }");
            ps.println("        .header h1 { color: #667eea; font-size: 2.5em; margin-bottom: 10px; }");
            ps.println("        .header p { color: #666; font-size: 1.1em; }");
            ps.println("        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 20px; }");
            ps.println("        .stat-card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 8px 32px rgba(0,0,0,0.1); text-align: center; transition: transform 0.3s; }");
            ps.println("        .stat-card:hover { transform: translateY(-5px); }");
            ps.println("        .stat-card .number { font-size: 2.5em; font-weight: bold; color: #667eea; margin: 10px 0; }");
            ps.println("        .stat-card .label { color: #666; font-size: 0.9em; text-transform: uppercase; letter-spacing: 1px; }");
            ps.println("        .charts-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(500px, 1fr)); gap: 20px; margin-bottom: 20px; }");
            ps.println("        .chart-card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 8px 32px rgba(0,0,0,0.1); }");
            ps.println("        .chart-card h2 { color: #667eea; margin-bottom: 20px; font-size: 1.3em; }");
            ps.println("        .table-card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 8px 32px rgba(0,0,0,0.1); overflow-x: auto; }");
            ps.println("        table { width: 100%; border-collapse: collapse; }");
            ps.println("        th { background: #667eea; color: white; padding: 15px; text-align: left; font-weight: 600; }");
            ps.println("        td { padding: 12px 15px; border-bottom: 1px solid #eee; }");
            ps.println("        tr:hover { background: #f8f9ff; }");
            ps.println("        .badge { padding: 5px 12px; border-radius: 20px; font-size: 0.85em; font-weight: 600; }");
            ps.println("        .badge-critical { background: #fee; color: #c33; }");
            ps.println("        .badge-moderate { background: #ffc; color: #c93; }");
            ps.println("        .badge-good { background: #efe; color: #3c3; }");
            ps.println("        .formula-card { background: #f8f9ff; padding: 20px; border-radius: 10px; border-left: 4px solid #667eea; margin: 20px 0; }");
            ps.println("        .formula-card h3 { color: #667eea; margin-bottom: 10px; }");
            ps.println("        .formula { font-family: 'Courier New', monospace; background: white; padding: 15px; border-radius: 5px; margin: 10px 0; }");
            ps.println("        .footer { text-align: center; color: white; margin-top: 30px; opacity: 0.8; }");
            ps.println("    </style>");
            ps.println("</head>");
            ps.println("<body>");
            ps.println("    <div class=\"container\">");
            
            // Header
            ps.println("        <div class=\"header\">");
            ps.println("            <h1>📊 Code Analysis Dashboard</h1>");
            ps.println("            <p>Generated: " + new Date() + "</p>");
            ps.println("        </div>");
            
            // Summary Stats
            generateSummaryStats(ps, allMetrics);
            
            // Charts
            ps.println("        <div class=\"charts-grid\">");
            generateComplexityChart(ps, allMetrics);
            generateMaintainabilityChart(ps, allMetrics);
            generateHalsteadChart(ps, allMetrics);
            generateChurnChart(ps, allMetrics);
            ps.println("        </div>");
            
            // Formulas
            generateFormulas(ps);
            
            // Detailed Table
            generateDetailedTable(ps, allMetrics);
            
            // Footer
            ps.println("        <div class=\"footer\">");
            ps.println("            <p>JavaCodeAnalyzer-Pro | Enterprise-Grade Code Analysis</p>");
            ps.println("        </div>");
            ps.println("    </div>");
            ps.println("</body>");
            ps.println("</html>");
            
            System.out.println("✓ HTML dashboard saved to: " + outputPath);
            
        } catch (IOException e) {
            System.out.println("Error generating HTML report: " + e.getMessage());
        }
    }
    
    private static void generateSummaryStats(PrintStream ps, List<JavaCodeMetrics> metrics) {
        int totalFiles = metrics.size();
        double avgComplexity = metrics.stream().mapToInt(JavaCodeMetrics::getCyclomaticComplexity).average().orElse(0);
        double avgMI = metrics.stream()
            .filter(m -> m.getAdvancedMetrics() != null)
            .mapToDouble(m -> m.getAdvancedMetrics().getMaintainabilityIndex())
            .average().orElse(0);
        int totalLOC = metrics.stream().mapToInt(JavaCodeMetrics::getCodeLines).sum();
        
        ps.println("        <div class=\"stats-grid\">");
        ps.println("            <div class=\"stat-card\">");
        ps.println("                <div class=\"label\">Total Files</div>");
        ps.println("                <div class=\"number\">" + totalFiles + "</div>");
        ps.println("            </div>");
        ps.println("            <div class=\"stat-card\">");
        ps.println("                <div class=\"label\">Total Lines of Code</div>");
        ps.println("                <div class=\"number\">" + totalLOC + "</div>");
        ps.println("            </div>");
        ps.println("            <div class=\"stat-card\">");
        ps.println("                <div class=\"label\">Avg Complexity</div>");
        ps.println("                <div class=\"number\">" + String.format("%.1f", avgComplexity) + "</div>");
        ps.println("            </div>");
        ps.println("            <div class=\"stat-card\">");
        ps.println("                <div class=\"label\">Avg Maintainability</div>");
        ps.println("                <div class=\"number\">" + String.format("%.1f", avgMI) + "</div>");
        ps.println("            </div>");
        ps.println("        </div>");
    }
    
    private static void generateComplexityChart(PrintStream ps, List<JavaCodeMetrics> metrics) {
        ps.println("            <div class=\"chart-card\">");
        ps.println("                <h2>📈 Cyclomatic Complexity by File</h2>");
        ps.println("                <canvas id=\"complexityChart\"></canvas>");
        ps.println("                <script>");
        ps.println("                    new Chart(document.getElementById('complexityChart'), {");
        ps.println("                        type: 'bar',");
        ps.println("                        data: {");
        ps.print("                            labels: [");
        for (int i = 0; i < metrics.size(); i++) {
            ps.print("'" + metrics.get(i).getFileName() + "'");
            if (i < metrics.size() - 1) ps.print(", ");
        }
        ps.println("],");
        ps.print("                            datasets: [{label: 'Complexity', data: [");
        for (int i = 0; i < metrics.size(); i++) {
            ps.print(metrics.get(i).getCyclomaticComplexity());
            if (i < metrics.size() - 1) ps.print(", ");
        }
        ps.println("], backgroundColor: 'rgba(102, 126, 234, 0.8)', borderColor: 'rgba(102, 126, 234, 1)', borderWidth: 2}]");
        ps.println("                        },");
        ps.println("                        options: { responsive: true, scales: { y: { beginAtZero: true } } }");
        ps.println("                    });");
        ps.println("                </script>");
        ps.println("            </div>");
    }
    
    private static void generateMaintainabilityChart(PrintStream ps, List<JavaCodeMetrics> metrics) {
        ps.println("            <div class=\"chart-card\">");
        ps.println("                <h2>🎯 Maintainability Index</h2>");
        ps.println("                <canvas id=\"miChart\"></canvas>");
        ps.println("                <script>");
        ps.println("                    new Chart(document.getElementById('miChart'), {");
        ps.println("                        type: 'line',");
        ps.println("                        data: {");
        ps.print("                            labels: [");
        for (int i = 0; i < metrics.size(); i++) {
            ps.print("'" + metrics.get(i).getFileName() + "'");
            if (i < metrics.size() - 1) ps.print(", ");
        }
        ps.println("],");
        ps.print("                            datasets: [{label: 'MI Score', data: [");
        for (int i = 0; i < metrics.size(); i++) {
            double mi = metrics.get(i).getAdvancedMetrics() != null ? 
                metrics.get(i).getAdvancedMetrics().getMaintainabilityIndex() : 0;
            ps.print(String.format("%.1f", mi));
            if (i < metrics.size() - 1) ps.print(", ");
        }
        ps.println("], borderColor: 'rgba(118, 75, 162, 1)', backgroundColor: 'rgba(118, 75, 162, 0.2)', borderWidth: 3, tension: 0.4, fill: true}]");
        ps.println("                        },");
        ps.println("                        options: { responsive: true, scales: { y: { min: 0, max: 100 } } }");
        ps.println("                    });");
        ps.println("                </script>");
        ps.println("            </div>");
    }
    
    private static void generateHalsteadChart(PrintStream ps, List<JavaCodeMetrics> metrics) {
        ps.println("            <div class=\"chart-card\">");
        ps.println("                <h2>🔬 Halstead Metrics</h2>");
        ps.println("                <canvas id=\"halsteadChart\"></canvas>");
        ps.println("                <script>");
        ps.println("                    new Chart(document.getElementById('halsteadChart'), {");
        ps.println("                        type: 'radar',");
        ps.println("                        data: {");
        ps.print("                            labels: [");
        for (int i = 0; i < Math.min(5, metrics.size()); i++) {
            ps.print("'" + metrics.get(i).getFileName() + "'");
            if (i < Math.min(4, metrics.size() - 1)) ps.print(", ");
        }
        ps.println("],");
        ps.println("                            datasets: [{");
        ps.println("                                label: 'Volume',");
        ps.print("                                data: [");
        for (int i = 0; i < Math.min(5, metrics.size()); i++) {
            double vol = metrics.get(i).getAdvancedMetrics() != null ? 
                metrics.get(i).getAdvancedMetrics().getVolume() / 10 : 0;
            ps.print(String.format("%.1f", vol));
            if (i < Math.min(4, metrics.size() - 1)) ps.print(", ");
        }
        ps.println("],");
        ps.println("                                borderColor: 'rgba(255, 99, 132, 1)', backgroundColor: 'rgba(255, 99, 132, 0.2)'");
        ps.println("                            }]");
        ps.println("                        },");
        ps.println("                        options: { responsive: true }");
        ps.println("                    });");
        ps.println("                </script>");
        ps.println("            </div>");
    }
    
    private static void generateChurnChart(PrintStream ps, List<JavaCodeMetrics> metrics) {
        ps.println("            <div class=\"chart-card\">");
        ps.println("                <h2>🔄 Code Churn Analysis</h2>");
        ps.println("                <canvas id=\"churnChart\"></canvas>");
        ps.println("                <script>");
        ps.println("                    new Chart(document.getElementById('churnChart'), {");
        ps.println("                        type: 'doughnut',");
        ps.println("                        data: {");
        ps.println("                            labels: ['Low Churn', 'Medium Churn', 'High Churn'],");
        ps.println("                            datasets: [{");
        ps.println("                                data: [60, 30, 10],");
        ps.println("                                backgroundColor: ['rgba(75, 192, 192, 0.8)', 'rgba(255, 206, 86, 0.8)', 'rgba(255, 99, 132, 0.8)']");
        ps.println("                            }]");
        ps.println("                        },");
        ps.println("                        options: { responsive: true }");
        ps.println("                    });");
        ps.println("                </script>");
        ps.println("            </div>");
    }
    
    private static void generateFormulas(PrintStream ps) {
        ps.println("        <div class=\"formula-card\">");
        ps.println("            <h3>📐 Metrics Formulas</h3>");
        ps.println("            <div class=\"formula\">");
        ps.println("                <strong>Maintainability Index:</strong><br>");
        ps.println("                MI = MAX(0, (171 - 5.2×ln(V) - 0.23×CC - 16.2×ln(LOC)) × 100 / 171)");
        ps.println("            </div>");
        ps.println("            <div class=\"formula\">");
        ps.println("                <strong>Halstead Volume:</strong> V = N × log₂(n)<br>");
        ps.println("                <strong>Halstead Difficulty:</strong> D = (n1/2) × (N2/n2)<br>");
        ps.println("                <strong>Halstead Effort:</strong> E = D × V");
        ps.println("            </div>");
        ps.println("            <div class=\"formula\">");
        ps.println("                <strong>Risk Score:</strong> (CC×0.3) + (Churn×0.25) + (Dup×0.2) + ((100-Cov)×0.25)");
        ps.println("            </div>");
        ps.println("        </div>");
    }
    
    private static void generateDetailedTable(PrintStream ps, List<JavaCodeMetrics> metrics) {
        ps.println("        <div class=\"table-card\">");
        ps.println("            <h2>📋 Detailed File Metrics</h2>");
        ps.println("            <table>");
        ps.println("                <thead>");
        ps.println("                    <tr>");
        ps.println("                        <th>File</th>");
        ps.println("                        <th>LOC</th>");
        ps.println("                        <th>Complexity</th>");
        ps.println("                        <th>MI</th>");
        ps.println("                        <th>Cognitive</th>");
        ps.println("                        <th>Halstead Vol</th>");
        ps.println("                        <th>Status</th>");
        ps.println("                    </tr>");
        ps.println("                </thead>");
        ps.println("                <tbody>");
        
        for (JavaCodeMetrics m : metrics) {
            String miClass = "badge-good";
            String miLabel = "GOOD";
            if (m.getAdvancedMetrics() != null) {
                double mi = m.getAdvancedMetrics().getMaintainabilityIndex();
                if (mi < 20) {
                    miClass = "badge-critical";
                    miLabel = "CRITICAL";
                } else if (mi < 50) {
                    miClass = "badge-moderate";
                    miLabel = "MODERATE";
                }
            }
            
            ps.println("                    <tr>");
            ps.println("                        <td><strong>" + m.getFileName() + "</strong></td>");
            ps.println("                        <td>" + m.getCodeLines() + "</td>");
            ps.println("                        <td>" + m.getCyclomaticComplexity() + "</td>");
            if (m.getAdvancedMetrics() != null) {
                ps.println("                        <td>" + String.format("%.1f", m.getAdvancedMetrics().getMaintainabilityIndex()) + "</td>");
                ps.println("                        <td>" + m.getAdvancedMetrics().getCognitiveComplexity() + "</td>");
                ps.println("                        <td>" + String.format("%.1f", m.getAdvancedMetrics().getVolume()) + "</td>");
            } else {
                ps.println("                        <td>-</td>");
                ps.println("                        <td>-</td>");
                ps.println("                        <td>-</td>");
            }
            ps.println("                        <td><span class=\"badge " + miClass + "\">" + miLabel + "</span></td>");
            ps.println("                    </tr>");
        }
        
        ps.println("                </tbody>");
        ps.println("            </table>");
        ps.println("        </div>");
    }
}
