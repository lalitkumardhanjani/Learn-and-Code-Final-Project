package org.Client;

import java.util.Scanner;
import java.io.PrintWriter;

public class Utility {
    public static int readIntInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    public static double readDoubleInput(Scanner scanner) {
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
        }
        return scanner.nextDouble();
    }

    public static String readStringInput(Scanner scanner) {
        return scanner.nextLine();
    }

    public static void logout(PrintWriter out) {
        System.out.println("Logged out successfully.");
    }
}
