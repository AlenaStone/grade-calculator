package alena.lernen;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        List<Student> allStudents = new ArrayList<>();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Hi! Here you can calculate your average grade.");
            System.out.print("Whats your name? ");
            String studentName = scanner.nextLine();
            Student student = new Student(studentName);
            allStudents.add(student);
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
                        System.out.printf("| %-10s | %-30s | %-6s | %-20s |\n", "Name", "Grades", "Avg", "Problems");
                        System.out.println(
                                "-----------------------------------------------------------------------------------");

                        for (Student s : allStudents) {
                            System.out.println(s.printTableRow());
                        }
                        System.exit(0);
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