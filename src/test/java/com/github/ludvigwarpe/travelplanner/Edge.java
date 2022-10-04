package com.github.ludvigwarpe.travelplanner;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Edge {

    private final Node current;
    private final Node destination;
    private String lineNr;
    private double weight;

    private HashMap<String, List<LocalTime>> timetable = new HashMap<>();
    private double g = Double.MAX_VALUE;

    public Edge(Node current, Node destination) {
        this.current = current;
        this.destination = destination;
    }

    public void addToTimeTable(String lineNr, String departureTime) {
        if (departureTime.length() == 7)
            departureTime = "0" + departureTime;
        if (departureTime.startsWith("24"))
            departureTime = "00".concat(departureTime.substring(2));

        if (!timetable.containsKey(lineNr))
            timetable.put(lineNr, new ArrayList<>());

        LocalTime time = LocalTime.parse(departureTime);
        timetable.get(lineNr).add(time);
        Collections.sort(timetable.get(lineNr));
    }

    public void setWeight(int diffMinutes) {
        this.weight = (double) diffMinutes;
    }

    public void printTimeTable() {
        if (timetable.isEmpty())
            throw new IllegalStateException("Timetable is empty");

        for (Map.Entry<String, List<LocalTime>> entry : timetable.entrySet()) {
            System.out.println("LineNr: " + entry.getKey());
            for (LocalTime time : entry.getValue())
                System.out.println(time);
        }
    }

    public void calculateG(LocalTime now) {
        ArrayList<LocalTime> departuresAfter = new ArrayList<>();
        for (Map.Entry<String, List<LocalTime>> entry : timetable.entrySet()) {
            for (LocalTime time : entry.getValue()) {
                if (time.equals(now) || time.isAfter(now))
                    departuresAfter.add(time);
            }
        }
        if (!departuresAfter.isEmpty()) {
            LocalTime next = Collections.min(departuresAfter);

            for (String line : timetable.keySet()) {
                if (timetable.get(line).contains(next))
                    lineNr = line;
            }

            long diff = now.until(next, ChronoUnit.MINUTES);
            this.g = weight + (double) diff;
        }
    }

    public Node getCurrent() {
        return current;
    }

    public Node getDestination() {
        return destination;
    }

    public String getLineNr() {
        return lineNr;
    }

    public double getWeight() {
        return weight;
    }

    public double getG() {
        return g;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Edge))
            return false;
        Edge edge = (Edge) o;
        return Objects.equals(getCurrent(), edge.getCurrent())
                && Objects.equals(getDestination(), edge.getDestination());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrent(), getDestination());
    }

    @Override
    public String toString() {
        return "Edge{" +
                "current=" + current +
                ", destination=" + destination +
                ", weight=" + weight +
                '}';
    }

}
