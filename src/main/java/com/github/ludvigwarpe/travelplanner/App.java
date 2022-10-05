package com.github.ludvigwarpe.travelplanner;

import java.time.LocalTime;
import java.util.Scanner;

/**
 * @author Ludvig Warpe
 *
 */

public class App {
    private Scanner scanner = new Scanner(System.in);

    public void run() {
        Graph graph = new Graph();
        System.out.println("Welcome!");

        String command;
        do {
            System.out.println("Please enter one of the following commands:\n" + "[1] - travel planner\n"
                    + "[2] - exit");
            command = scanner.nextLine();
            switch (command) {
                case "1":
                    pathFinder(graph);
                    break;
                case "2":
                    System.out.println("Program terminated.");
                    break;
                default:
                    System.out.println("Invalid command! Try again.");
            }
        } while (!command.equals("2"));
        scanner.close();
    }

    private void pathFinder(Graph graph) {
        System.out.print("From: ");
        String from = scanner.nextLine();
        System.out.println();
        System.out.print("To: ");
        String to = scanner.nextLine();
        System.out.println();
        System.out.print("At what time? (HH:mm:ss): ");
        String time = scanner.nextLine();
        LocalTime departureTime = LocalTime.parse(time);
        graph.printPath(graph.shortestPath(from, to, departureTime));
    }

    public static void main(String[] arg) {
        App app = new App();
        app.run();
    }
}
