package alena.lernen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {
    private String name;
    private Map<String, Integer> grades;

    public Student(String name) {
        this.name = name;
        this.grades = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addGrade(String subject, int grade) {
        this.grades.put(subject, grade);
    }

    public String getGradesAsString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : grades.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("; ");
        }
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    public double getAverage() {
        if (grades.isEmpty())
            return 0;
        int sum = 0;
        for (int grade : this.grades.values()) {
            sum += grade;

        }
        double result = sum / (double) this.grades.size();
        return result;
    }

    public List<String> getBadGrades() {
        List<String> badSubjects = new ArrayList<>();

        for (Map.Entry<String, Integer> grade : grades.entrySet()) {
            if (grade.getValue() == 5) {
                badSubjects.add("Failed: " + grade.getKey());
            }
            if (grade.getValue() == 4) {
                badSubjects.add("At risk: " + grade.getKey());

            }
        }
        return badSubjects;
    }

    public String getBadGradesAsString() {
        List<String> issues = getBadGrades();

        if (issues.isEmpty())
            return "";

        return String.join("; ", issues);

    }

    public String printTableRow() {
        return String.format("| %-10s | %-30s | %-6.2f | %-20s |", name, getGradesAsString(), getAverage(),
                getBadGradesAsString());
    }
    public java.util.Map<String, Integer> getGradesMap() {
    
    return new java.util.HashMap<>(grades);
}

}
