package com.github.ludvigwarpe.travelplanner;

import java.time.LocalTime;

/*
 * @author Ludvig Warpe
 * 
 */

public class Node implements Comparable<Node> {

    private final int stopId;
    private final String stopName;
    private final double latitude;
    private final double longitude;
    private Node parent = null;
    private LocalTime departureTime;
    private String lineNrDeparture;
    private double f = Double.MAX_VALUE;
    private double h = Double.MAX_VALUE;
    private double g = Double.MAX_VALUE;

    public Node(int stopId, String stopName, double latitude, double longitude) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getLineNrDeparture() {
        return lineNrDeparture;
    }

    public void setLineNrDeparture(String lineNrDeparture) {
        this.lineNrDeparture = lineNrDeparture;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public int getStopId() {
        return stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    //Used for priority queues in A* algortihm
    @Override
    public int compareTo(Node other) {
        double f1 = this.getF();
        double f2 = other.getF();
        return Double.compare(f1, f2);
    }

    @Override
    public String toString() {
        return "Node{" +
                "stopId=" + stopId +
                ", stopName='" + stopName + '\'' +
                '}';
    }

}
