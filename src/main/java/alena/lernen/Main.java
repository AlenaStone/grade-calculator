package alena.lernen;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Hi! Here you can calculate your average grade.");
            System.out.print("Please tell me how many subjects you have: ");

            try {
                String numberOfSubject = scanner.nextLine();
                System.out.println("-----------");
                int number = Integer.parseInt(numberOfSubject);
                HashMap<String, Integer> subjectAndNote = new HashMap<>();
                int sum = 0;
                for (int i = 0; i < number; i++) {
                    System.out.print("Pleas enter your subject: ");

                    String subject = scanner.nextLine();
                    System.out.println("-------------------------");
                    System.out.printf("What is your grade in %s: ", subject);
                    String noteString = scanner.nextLine();
                    System.out.println("-------------------------");
                    int note = Integer.parseInt(noteString);
                    sum += note;
                    subjectAndNote.put(subject, note);

                }
                System.out.printf("All your subjects: %s%n", subjectAndNote);

                for (Map.Entry<String, Integer> allSubjectAndNote : subjectAndNote.entrySet()) {
                    System.out.println(allSubjectAndNote.getKey() + ": " + allSubjectAndNote.getValue());
                    if (allSubjectAndNote.getValue() >= 5) {
                        System.out.printf("Oh no, that's very bad. You could fail this course: %s%n",
                                allSubjectAndNote.getKey());
                    }
                }

                double averageRating = sum / (double) number;
                System.out.printf("Your current average grade: %s%n", averageRating);
                System.out.println("Would you like to start over? (yes/no)");
                String exit = scanner.nextLine().toLowerCase();
                switch (exit){
                    case "yes": break;
                    case "no":System.exit(0);
                    default: System.out.println("Unknown option. Restarting by default...");
                }
            } catch (Exception e) {
                System.err.println("Oh, something is wrong!");
                System.err.println("Try again!");
                System.out.println("-------------------------");

            }
        }
    }
}