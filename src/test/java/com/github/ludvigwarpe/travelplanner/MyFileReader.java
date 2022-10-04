package com.github.ludvigwarpe.travelplanner;

/*
 * @author Ludvig Warpe
 */

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;



public class MyFileReader {

    private Graph graph;
    private FileReader fileReader;
    private Scanner scanner;
    private StringTokenizer tokenizer;

    public MyFileReader(Graph graph) {
        this.graph = graph;
        readTrips();
        readRouteNames();
        readNodes();
        readEdges();
    }

    private void readTrips() {
        try {
            fileReader = new FileReader("info/sl_trips.txt");
            scanner = new Scanner(fileReader);
            String header = scanner.nextLine();
            while (scanner.hasNextLine()) {
                tokenizer = new StringTokenizer(scanner.nextLine(), ",");
                long routeId = Long.parseLong(tokenizer.nextToken());
                tokenizer.nextToken();
                long tripId = Long.parseLong(tokenizer.nextToken());
                graph.addTrip(tripId, routeId);
            }
            fileReader.close();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readRouteNames() {
        try {
            fileReader = new FileReader("info/sl_routes.txt");
            scanner = new Scanner(fileReader);
            String header = scanner.nextLine();
            while (scanner.hasNextLine()) {
                tokenizer = new StringTokenizer(scanner.nextLine(), ",");
                long routeId = Long.parseLong(tokenizer.nextToken());
                tokenizer.nextToken();
                String routeName = tokenizer.nextToken();
                graph.addRoute(routeId, routeName);
            }
            fileReader.close();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readNodes() {
        try {
            fileReader = new FileReader("info/sl_stops.txt");
            scanner = new Scanner(fileReader);
            String info = scanner.nextLine();
            while (scanner.hasNextLine()) {
                tokenizer = new StringTokenizer(scanner.nextLine(), ",");
                int stopId = Integer.parseInt(tokenizer.nextToken());
                String stopName = tokenizer.nextToken();
                double stopLat = Double.parseDouble(tokenizer.nextToken());
                double stopLong = Double.parseDouble(tokenizer.nextToken());

                graph.addNode(new Node(stopId, stopName, stopLat, stopLong));
            }
            fileReader.close();
            scanner.close();
        } catch (IOException e) {
            System.out.println("Message: " + e.getMessage());
        }
    }

    private void readEdges() {
        try {
            fileReader = new FileReader("info/sl_stop_times.txt");
            scanner = new Scanner(fileReader);
            long tripId2 = 0;
            String arrivalTime2 = null;
            String departureTime2 = null;
            int stopId2 = 0;
            String info = scanner.nextLine();
            int stopSeqCounter = 0;

            while (scanner.hasNextLine()) {
                tokenizer = new StringTokenizer(scanner.nextLine(), ",");
                long tripId1 = Long.parseLong(tokenizer.nextToken());
                String arrivalTime1 = tokenizer.nextToken();
                String departureTime1 = tokenizer.nextToken();
                int stopId1 = Integer.parseInt(tokenizer.nextToken());
                int stopSeq = Integer.parseInt(tokenizer.nextToken());

                if (stopSeq != stopSeqCounter + 1) {
                    stopSeqCounter = 0;
                }
                if (tripId1 == tripId2 && stopSeqCounter != 0)
                    graph.addEdge(stopId2, stopId1, arrivalTime1, departureTime2, tripId2);

                tripId2 = tripId1;
                arrivalTime2 = arrivalTime1;
                departureTime2 = departureTime1;
                stopId2 = stopId1;
                stopSeqCounter = stopSeq;
            }
            fileReader.close();
            scanner.close();
        } catch (IOException e) {
            System.out.println("Message: " + e.getMessage());
        }
    }



}
