package alena.lernen;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import alena.lernen.storage.CsvStudentStorage;
import alena.lernen.storage.StudentStorage;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Config cfg;

        StudentStorage storage;

        List<Student> allStudents = new ArrayList<>();

        System.out.println("Welcome to the grade calculator!");
        System.out.println("-----------");

        try {
            cfg = Config.loadOrCreateDefault();

            storage = new CsvStudentStorage(
                    cfg.dataPath,
                    cfg.exportPath,
                    cfg.subjectsPath,
                    cfg.logPath,
                    cfg.encoding,
                    cfg.sep,
                    cfg.kv);

            List<Student> loaded = storage.loadAll();
            System.out.println("Number of students enrolled: " + loaded.size());
            System.out.println("-----------");
            allStudents.addAll(loaded);
            System.out.println("Loaded students:");
            System.out.printf("| %-10s | %-30s | %-6s | %-20s |\n", "Name", "Grades", "Avg", "Problems");
            System.out.println("-----------------------------------------------------------------------------------");
            for (Student s : loaded) {
                System.out.println(s.printTableRow());
            }
        } catch (Exception e) {
            System.err.println("The start was unsuccessful: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("Hi! Here you can calculate your average grade.");
            System.out.print("Whats your name? ");
            String studentName = scanner.nextLine();
            Student student = new Student(studentName);
            allStudents.add(student);
            try {
                storage.append(student);
            } catch (Exception io) {
                System.err.println("Не смогли дозаписать в журнал: " + io.getMessage());
            }
            
            System.out.println("-----------");
            System.out.print("Please tell me how many subjects you have: ");

            try {
                String numberOfSubject = scanner.nextLine();
                System.out.println("-----------");
                int number = Integer.parseInt(numberOfSubject);

                for (int i = 0; i < number; i++) {
                    System.out.print("Pleas enter your subject: ");

                    String subject = scanner.nextLine();
                    System.out.println("-------------------------");
                    System.out.printf("What is your grade in %s: ", subject);
                    String noteString = scanner.nextLine();
                    System.out.println("-------------------------");
                    int note = Integer.parseInt(noteString);
                    student.addGrade(subject, note);

                }
                System.out.printf("| %-10s | %-30s | %-6s | %-20s |\n", "Name", "Grades", "Avg", "Problems");
                System.out
                        .println("-----------------------------------------------------------------------------------");
                System.out.println(student.printTableRow());

                System.out.println("Would you like to start over? (yes/no)");
                String exit = scanner.nextLine().toLowerCase();
                switch (exit) {
                    case "yes":
                        break;
                    case "no":
                        System.out.println("Thank you for using the grade calculator!");
                        System.out.println("Here is the list of all students:");
                        System.out.println(
                                "-----------------------------------------------------------------------------------");
                        System.out.printf("| %-10s | %-30s | %-6s | %-20s |\n", "Name", "Grades", "Avg", "Problems");
                        System.out.println(
                                "-----------------------------------------------------------------------------------");
                        for (Student s : allStudents) {
                            System.out.println(s.printTableRow());
                        }
                        try {
                            storage.saveAll(allStudents);
                            storage.exportReport(allStudents, cfg.exportPath, cfg.reportFormat);
                            System.out.println("Saved. Report: " + cfg.exportPath.toAbsolutePath());
                        } catch (Exception io) {
                            System.err.println("Save/export failed: " + io.getMessage());
                        }

                        return;

                    default:
                        System.out.println("Unknown option. Restarting by default...");
                }
            } catch (Exception e) {
                System.err.println("Oh, something is wrong!");
                System.err.println("Try again!");
                System.out.println("-------------------------");

            }
        }

    }

}