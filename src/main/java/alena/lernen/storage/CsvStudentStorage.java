package alena.lernen.storage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import alena.lernen.Student;

public class CsvStudentStorage implements StudentStorage {
    private final Path dataPath;
    private final Path exportPath;
    private final Path subjectsPath;
    private final Path logPath;
    private final Charset encoding;
    private final char sep;
    private final char kv;

    public CsvStudentStorage(Path dataPath,
            Path exportPath,
            Path subjectsPath,
            Path logPath,
            Charset encoding,
            char sep,
            char kv) {
        this.dataPath = dataPath;
        this.exportPath = exportPath;
        this.subjectsPath = subjectsPath;
        this.logPath = logPath;
        this.encoding = encoding;
        this.sep = sep;
        this.kv = kv;
    }

    @Override
    public List<Student> loadAll() throws IOException {
        List<Student> result = new ArrayList<>();

        if (java.nio.file.Files.notExists(dataPath)) {
            if (dataPath.getParent() != null)
                java.nio.file.Files.createDirectories(dataPath.getParent());
            java.nio.file.Files.createFile(dataPath);
            return result;
        }

        try (java.io.BufferedReader r = java.nio.file.Files.newBufferedReader(dataPath, encoding)) {
            String line;
            int lineNo = 0;
            while ((line = r.readLine()) != null) {
                lineNo++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                String[] parts = line.split(java.util.regex.Pattern.quote(String.valueOf(sep)));
                if (parts.length == 0)
                    continue;

                String name = parts[0].trim();
                if (name.isEmpty() || name.indexOf(sep) >= 0 || name.indexOf(kv) >= 0) {
                    logWarn("Line " + lineNo + ": bad name");
                    continue;
                }

                Student s = new Student(name);

                for (int i = 1; i < parts.length; i++) {
                    String p = parts[i].trim();
                    if (p.isEmpty())
                        continue;

                    String[] kvParts = p.split(java.util.regex.Pattern.quote(String.valueOf(kv)));
                    if (kvParts.length != 2) {
                        logWarn("Line " + lineNo + ": malformed pair '" + p + "'");
                        continue;
                    }

                    String subject = kvParts[0].trim();
                    String gradeStr = kvParts[1].trim();

                    if (subject.isEmpty() || subject.indexOf(sep) >= 0 || subject.indexOf(kv) >= 0) {
                        logWarn("Line " + lineNo + ": bad subject '" + subject + "'");
                        continue;
                    }

                    int grade;
                    try {
                        grade = Integer.parseInt(gradeStr);
                    } catch (NumberFormatException nfe) {
                        logWarn("Line " + lineNo + ": not a number '" + gradeStr + "'");
                        continue;
                    }
                    if (grade < 1 || grade > 5) {
                        logWarn("Line " + lineNo + ": grade out of range '" + grade + "'");
                        continue;
                    }

                    s.addGrade(subject, grade);
                }

                result.add(s);
            }
        }

        return result;
    }

    private void logWarn(String msg) {
        try (java.io.BufferedWriter w = java.nio.file.Files.newBufferedWriter(
                logPath, encoding,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND)) {
            w.write(msg);
            w.write('\n');
        } catch (IOException ignored) {
        }
    }

    @Override
    public void saveAll(List<Student> students) throws IOException {

        if (dataPath.getParent() != null) {
            java.nio.file.Files.createDirectories(dataPath.getParent());
        }

        java.nio.file.Path tmp = dataPath.resolveSibling(dataPath.getFileName().toString() + ".tmp");
        java.nio.file.Path bak = dataPath.resolveSibling(dataPath.getFileName().toString() + ".bak");

        try (java.io.BufferedWriter w = java.nio.file.Files.newBufferedWriter(
                tmp, encoding,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Student s : students) {
                w.write(toCsvLine(s));
                w.write('\n');
            }
        }

        if (java.nio.file.Files.exists(dataPath)) {
            java.nio.file.Files.copy(dataPath, bak,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            java.nio.file.Files.move(tmp, dataPath,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (java.nio.file.AtomicMoveNotSupportedException ex) {

            java.nio.file.Files.move(tmp, dataPath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String toCsvLine(Student s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s.getName());

        java.util.List<java.util.Map.Entry<String, Integer>> entries = new java.util.ArrayList<>(
                s.getGradesMap().entrySet());
        entries.sort(java.util.Comparator.comparing(java.util.Map.Entry::getKey, String.CASE_INSENSITIVE_ORDER));

        for (java.util.Map.Entry<String, Integer> e : entries) {
            sb.append(sep).append(e.getKey()).append(kv).append(e.getValue());
        }
        return sb.toString();
    }

    @Override
    public void append(Student student) throws IOException {

        if (dataPath.getParent() != null) {
            java.nio.file.Files.createDirectories(dataPath.getParent());
        }
        try (java.io.BufferedWriter w = java.nio.file.Files.newBufferedWriter(
                dataPath, encoding,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND)) {
            w.write(toCsvLine(student));
            w.write('\n');
        }
    }

    @Override
    public void exportReport(List<Student> students, Path toFile, ReportFormat fmt) throws IOException {
        if (fmt != ReportFormat.TXT && fmt != ReportFormat.MD && fmt != ReportFormat.PDF) {
            throw new UnsupportedOperationException("Unsupported report format: " + fmt);
        }
        if (toFile.getParent() != null) {
            java.nio.file.Files.createDirectories(toFile.getParent());
        }

        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparingDouble(Student::getAverage)
                .thenComparing(Student::getName, String.CASE_INSENSITIVE_ORDER));

        String now = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        StringBuilder sb = new StringBuilder();
        sb.append("# Grade Report — ").append(now).append("\n\n");
        sb.append("| # | Name | Subjects | Average | Problems |\n");
        sb.append("|---|------|----------|---------|----------|\n");

        int i = 1;
        for (Student s : sorted) {
            sb.append("| ").append(i++).append(" | ")
                    .append(escapeMd(s.getName())).append(" | ")
                    .append(escapeMd(subjectsDisplay(s))).append(" | ")
                    .append(String.format(java.util.Locale.US, "%.2f", s.getAverage())).append(" | ")
                    .append(problemsLabel(s)).append(" |\n");
        }

        Stats stats = computeStats(sorted);

        sb.append("\n**Students:** ").append(stats.count).append("  \n")
                .append("**Group average:** ").append(String.format(java.util.Locale.US, "%.2f", stats.groupAvg))
                .append("  \n")
                .append("**Failed:** ").append(stats.failed).append(", **At risk:** ").append(stats.atRisk)
                .append("  \n")
                .append("**TOP-3:** ").append(stats.top3).append("\n");

        java.nio.file.Files.writeString(
                toFile, sb.toString(), encoding,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String subjectsDisplay(Student s) {
        java.util.List<java.util.Map.Entry<String, Integer>> es = new java.util.ArrayList<>(
                s.getGradesMap().entrySet());
        es.sort(java.util.Comparator.comparing(java.util.Map.Entry::getKey, String.CASE_INSENSITIVE_ORDER));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < es.size(); i++) {
            java.util.Map.Entry<String, Integer> e = es.get(i);
            if (i > 0)
                sb.append(", ");
            sb.append(e.getKey()).append(":").append(e.getValue());
        }
        return sb.toString();
    }

    private String problemsLabel(Student s) {
        boolean has5 = false, has4 = false;
        for (int g : s.getGradesMap().values()) {
            if (g == 5)
                has5 = true;
            if (g == 4)
                has4 = true;
        }
        if (has5)
            return "Failed";
        double avg = s.getAverage();
        if (has4 || avg >= 3.5)
            return "At risk";
        return "";
    }

    private static final class Stats {
        int count, failed, atRisk;
        double groupAvg;
        String top3;
    }

    private Stats computeStats(java.util.List<Student> sorted) {
        Stats st = new Stats();
        st.count = sorted.size();

        long totalSum = 0, totalCnt = 0;

        java.util.List<Student> byAvgAsc = new java.util.ArrayList<>(sorted);
        byAvgAsc.sort(java.util.Comparator.comparingDouble(Student::getAverage));

        java.util.List<String> top = new java.util.ArrayList<>();
        for (int i = 0; i < Math.min(3, byAvgAsc.size()); i++) {
            Student s = byAvgAsc.get(i);
            top.add(s.getName() + " (" + String.format(java.util.Locale.US, "%.2f", s.getAverage()) + ")");
        }
        st.top3 = String.join(", ", top);

        for (Student s : sorted) {
            boolean has5 = false, has4 = false;
            for (int g : s.getGradesMap().values()) {
                totalSum += g;
                totalCnt++;
                if (g == 5)
                    has5 = true;
                else if (g == 4)
                    has4 = true;
            }
            if (has5)
                st.failed++;
            else if (has4 || s.getAverage() >= 3.5)
                st.atRisk++;
        }

        st.groupAvg = (totalCnt == 0) ? 0.0 : (double) totalSum / totalCnt;
        return st;
    }

    private String escapeMd(String s) {
        return s == null ? "" : s.replace("|", "\\|");
    }

}
