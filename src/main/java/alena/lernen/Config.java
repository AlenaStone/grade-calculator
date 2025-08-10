package alena.lernen;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import alena.lernen.storage.ReportFormat;

public class Config {
    public final Path dataPath;
    public final Path exportPath;
    public final Path subjectsPath;
    public final Path logPath;
    public final Charset encoding;
    public final char sep;
    public final char kv;
    public final ReportFormat reportFormat;

    private Config(Path dataPath, Path exportPath, Path subjectsPath, Path logPath,
                   Charset encoding, char sep, char kv, ReportFormat reportFormat) {
        this.dataPath = dataPath;
        this.exportPath = exportPath;
        this.subjectsPath = subjectsPath;
        this.logPath = logPath;
        this.encoding = encoding;
        this.sep = sep;
        this.kv = kv;
        this.reportFormat = reportFormat;
    }

    public static Config loadOrCreateDefault() throws IOException {
        Path cfg = Paths.get("./config.properties");
        if (Files.notExists(cfg)) {
            // создаём дефолтный конфиг
            String defaults = String.join("\n",
                    "data.path=./data/students.csv",
                    "export.path=./out/report.md",
                    "export.format=MD",
                    "encoding=UTF-8",
                    "separator=;",
                    "kv.separator=:",
                    "subjects.path=./data/subjects.txt",
                    "log.path=./out/app.log",
                    ""
            );
            Files.writeString(cfg, defaults, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Создан дефолтный config.properties");
        }

        Properties p = new Properties();
        try (BufferedReader r = Files.newBufferedReader(cfg, StandardCharsets.UTF_8)) {
            p.load(r);
        }

        Charset enc = safeCharset(p.getProperty("encoding"), StandardCharsets.UTF_8);
        char sep = firstCharOr(p.getProperty("separator"), ';');
        char kv  = firstCharOr(p.getProperty("kv.separator"), ':');

        Path dataPath     = Paths.get(p.getProperty("data.path", "./data/students.csv"));
        Path exportPath   = Paths.get(p.getProperty("export.path", "./out/report.md"));
        Path subjectsPath = Paths.get(p.getProperty("subjects.path", "./data/subjects.txt"));
        Path logPath      = Paths.get(p.getProperty("log.path", "./out/app.log"));

        // MD | TXT
        ReportFormat fmt;
        String fmtStr = p.getProperty("export.format", "MD").trim().toUpperCase();
        try {
            fmt = ReportFormat.valueOf(fmtStr);
        } catch (IllegalArgumentException ex) {
            fmt = ReportFormat.MD;
        }

        // создаём директории, если их нет
        createParentDir(dataPath);
        createParentDir(exportPath);
        createParentDir(subjectsPath);
        createParentDir(logPath);

        return new Config(dataPath, exportPath, subjectsPath, logPath, enc, sep, kv, fmt);
    }

    private static Charset safeCharset(String name, Charset def) {
        if (name == null || name.isBlank()) return def;
        try { return Charset.forName(name.trim()); }
        catch (Exception e) { return def; }
    }

    private static char firstCharOr(String s, char def) {
        return (s == null || s.isEmpty()) ? def : s.charAt(0);
    }

    private static void createParentDir(Path p) throws IOException {
        Path parent = p.getParent();
        if (parent != null) Files.createDirectories(parent);
    }
}
