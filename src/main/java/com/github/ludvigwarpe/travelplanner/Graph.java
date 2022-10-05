package com.github.ludvigwarpe.travelplanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/*
 * @author Ludvig Warpe
 * 
 */

public class Graph {

    private MyFileReader myFileReader;
    private HashMap<Node, List<Edge>> adjList = new HashMap<>();
    //Maps the correspondning routeId and routeName, used in creation of Edge-object.
    private HashMap<Long, String> routeMap = new HashMap<>();
    // Maps the correspondning tripId and routeId, used in creation of Edge-object.
    private HashMap<Long, Long> tripMap = new HashMap<>();

    public Graph() {
        myFileReader = new MyFileReader(this);
    }

    /**
     * <p>
     * This method calculates the best path from @start to @destination given a
     * starting @time,
     * Finds the path with the A* algorithm which uses a heuristic value
     * {@link #hValue(Node, Node)}.
     * <a href= https://stackabuse.com/graphs-in-java-a-star-algorithm/></a>
     * </p>
     *
     * @param start       the name of the node which is the origin of the path
     * @param destination the name of the node where the path ends
     * @param time        of the departure from start
     * @return destination node if there is a path between start and destination,
     *         else null is returned
     * @throws IllegalArgumentException if @start or @destination is not the name of
     *                                  a node in the adjacency list
     *                                  {@link #adjList}.
     */
    public Node shortestPath(String start, String destination, LocalTime time) {
        PriorityQueue<Node> open = new PriorityQueue<>(); // Encountered nodes, not yet analyzed
        PriorityQueue<Node> closed = new PriorityQueue<>(); // Current shortest path calculated
        Node startNode = null;
        Node endNode = null;
        LocalTime now = time;

        for (Map.Entry<Node, List<Edge>> entry : adjList.entrySet()) {
            if (entry.getKey().getStopName().equals(start)) {

                startNode = entry.getKey();

            }
            if (entry.getKey().getStopName().equals(destination)) {
                endNode = entry.getKey();
            }
        }
        if (startNode == null || endNode == null)
            throw new IllegalArgumentException("Given station[s] can't be found.");

        open.add(startNode);
        startNode.setDepartureTime(now);

        while (!open.isEmpty()) {
            Node current = open.peek();
            if (current != startNode) {
                now = current.getDepartureTime();
            }
            if (current.equals(endNode)) {
                return current;
            }

            for (Edge edge : adjList.get(current)) {
                Node edgeDestination = edge.getDestination();
                edge.calculateG(now);
                double totalWeight = edgeDestination.getG() + edge.getG();

                if (!open.contains(edgeDestination) && !closed.contains(edgeDestination)) {
                    edgeDestination.setParent(current);

                    LocalTime timePlus = now.plus((long) edge.getG(), ChronoUnit.MINUTES);
                    edgeDestination.setDepartureTime(timePlus);

                    edgeDestination.setG(totalWeight);
                    edgeDestination.setF(edge.getG() + hValue(edgeDestination, endNode));
                    edgeDestination.setLineNrDeparture(edge.getLineNr());
                    open.add(edgeDestination);

                } else {
                    if (totalWeight < edgeDestination.getG()) {
                        edgeDestination.setParent(current);
                        edgeDestination.setG(totalWeight);
                        edgeDestination.setF(edge.getG() + hValue(edgeDestination, endNode));
                        if (closed.contains(edgeDestination)) {
                            closed.remove(edgeDestination);
                            open.add(edgeDestination);
                        }
                    }
                }
            }
            open.remove(current);
            closed.add(current);
        }
        return null;
    }


    /*
    * Hueristic value used in A* algorithm. 
    * H-value based on the assumption that the average speed of any transport is 50 km/h.
    * Used for calculating the distance between two nodes from the bird's eye view.
    */
    private double hValue(Node current, Node destination) {
        double averageSpeed = 50.0;
        return ((getDistanceBetween(current, destination) / averageSpeed) * 60.0);
    }

    private double getDistanceBetween(Node first, Node second) {
        if (first == null || second == null)
            throw new IllegalArgumentException("No such node exists");

        return haversineFormula(first.getLatitude(), first.getLongitude(), second.getLatitude(), second.getLongitude());
    }

    /**
     * @source: https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
     */
    private double haversineFormula(double lat1, double long1, double lat2, double long2) {
        double earthRadius = 6372.8;
        double diffLat = Math.toRadians(lat2 - lat1);
        double diffLong = Math.toRadians(long2 - long1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(diffLat / 2), 2) +
                Math.pow(Math.sin(diffLong / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);

        double c = 2 * Math.asin(Math.sqrt(a));
        return earthRadius * c;
    }

    public void addNode(Node node){
        adjList.put(node, new LinkedList<>());
    }

    public void addTrip(long tripId, long routeId){
        tripMap.put(tripId, routeId);
    }

    public void addRoute(long routeId, String routeName){
        routeMap.put(routeId, routeName);
    }

    public void addEdge(int stopId2, int stopId1, String arrivalTime, String departureTime, long tripId) {
        for (Node from : adjList.keySet())
            if (from.getStopId() == stopId1)
                for (Node to : adjList.keySet())
                    if (to.getStopId() == stopId2) {
                        long routeId = tripMap.get(tripId);
                        String line = routeMap.get(routeId);
                        Edge edge = new Edge(from, to);
                        if (!adjList.get(from).contains(edge)) {
                            adjList.get(from).add(edge);
                            calculateWeight(edge, departureTime, arrivalTime);
                            edge.addToTimeTable(line, arrivalTime);
                        } else
                            findEdge(from, edge).addToTimeTable(line, departureTime);
                    }
    }

    private Edge findEdge(Node from, Edge edge) {
        Edge foundEdge = null;
        List<Edge> edges = new ArrayList<>();
        for (Map.Entry<Node, List<Edge>> entry : adjList.entrySet()) {
            if (entry.getKey().equals(from))
                edges = entry.getValue();
        }
        for (int i = 0; i < edges.size(); i++) {
            if (edges.get(i).equals(edge))
                foundEdge = edges.get(i);
        }
        if (foundEdge == null)
            throw new IllegalArgumentException("Edge can't be found");
        return foundEdge;
    }

    private void calculateWeight(Edge edge, String startTime, String stopTime) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date start = null;
        Date stop = null;
        long diffMinutes = 0;
        try {
            start = format.parse(startTime);
            stop = format.parse(stopTime);
            long diff = stop.getTime() - start.getTime();
            diffMinutes = diff / (60 * 1000) % 60;

            if (diffMinutes < 0)
                throw new IllegalStateException("Weight can't be less than 0!");

            edge.setWeight((int) diffMinutes);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void printPath(Node target) {
        Node n = target;
        List<Node> nodesList = new ArrayList<>();

        if (n == null)
            throw new IllegalArgumentException("Node not found.");

        while (n.getParent() != null) {
            nodesList.add(n);
            n = n.getParent();

        }
        nodesList.add(n);
        Collections.reverse(nodesList);

        System.out.println("Trip to: " + target.getStopName());
        for (Node node : nodesList) {
            System.out.println("From station: " + node.getStopName() + " at: " + node.getDepartureTime());
        }
        System.out.println("");
    }

    public void printNodesAndEdges() {
        for (Map.Entry<Node, List<Edge>> entry : adjList.entrySet()) {
            System.out.println("*******STATION*******");
            System.out.println(entry.getKey() + "\n");
            System.out.println("*******Connections*******");
            for (Edge edge : entry.getValue())
                System.out.println(edge.toString());
            System.out.println();
        }
    }

    public void printEdgeTimeTable(String from, String to) {
        Node start = null;
        Node stop = null;
        for (Map.Entry<Node, List<Edge>> entry : adjList.entrySet()) {
            if (entry.getKey().getStopName().equalsIgnoreCase(from))
                start = entry.getKey();
            if (entry.getKey().getStopName().equalsIgnoreCase(to))
                stop = entry.getKey();
        }
        if (start == null || stop == null)
            throw new IllegalArgumentException("Incorrect input of station name.");

        for (Edge edge : adjList.get(start)) {
            if (edge.getDestination().equals(stop)) {
                edge.printTimeTable();
            }
        }
    }

    public Set<Node> getNodes(){
        return Collections.unmodifiableSet(adjList.keySet());
    }

    public List<Edge> getNodeEdges(String stopName){
        List<Edge> stationEdges = null;
        
        for (Map.Entry<Node, List<Edge>> entry : adjList.entrySet()) {
            if (entry.getKey().getStopName().equals(stopName))
                stationEdges = entry.getValue();
        }
        return Collections.unmodifiableList(stationEdges);
    }
}
