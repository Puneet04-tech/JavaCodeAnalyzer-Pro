package utils;

import java.io.File;
import java.util.*;

/**
 * Simple extension-based language detector and heuristic registry.
 */
public class LanguageDetector {

    private static final Map<String, LanguageHeuristic> registry = new HashMap<>();

    static {
        // Register known heuristics
        LanguageHeuristic generic = new GenericHeuristic();
        registry.put("default", generic);

        LanguageHeuristic python = new PythonHeuristic();
        registry.put("py", python);

        LanguageHeuristic js = new JavaScriptHeuristic();
        registry.put("js", js);
        registry.put("jsx", js);
        registry.put("mjs", js);
        registry.put("ts", js);

        // Common textual files fall back to generic
        registry.put("txt", generic);
        registry.put("md", generic);
        registry.put("html", generic);
        registry.put("xml", generic);
        registry.put("c", generic);
        registry.put("cpp", generic);
        registry.put("h", generic);
        registry.put("hpp", generic);
        registry.put("sh", generic);
        registry.put("rb", generic);
        registry.put("php", generic);
        registry.put("go", generic);
        registry.put("kt", generic);
        registry.put("rs", generic);
    }

    public static LanguageHeuristic getHeuristicForFile(File file) {
        String name = file.getName();
        int idx = name.lastIndexOf('.');
        if (idx < 0) return registry.get("default");
        String ext = name.substring(idx + 1).toLowerCase(Locale.ROOT);
        LanguageHeuristic h = registry.get(ext);
        if (h == null) return registry.get("default");
        return h;
    }
}
