package alena.lernen.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import alena.lernen.Student;

public interface StudentStorage {
    List<Student> loadAll() throws IOException;
    void saveAll(List<Student> students) throws IOException;
    void append(Student student) throws IOException; 
    void exportReport(List<Student> students, Path toFile, ReportFormat fmt) throws IOException;
}