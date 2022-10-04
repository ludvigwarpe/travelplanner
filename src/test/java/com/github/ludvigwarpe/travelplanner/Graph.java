package com.github.ludvigwarpe.travelplanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/*
 * @author Ludvig Warpe
 * 
 */

 import java.util.*;;

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

}
