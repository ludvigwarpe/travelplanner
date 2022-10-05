package com.github.ludvigwarpe.travelplanner;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class AppTest 
{

    Graph testGraph = new Graph();
   
    @Test
    public void graphShouldContainAllNodes(){
        assertTrue(443 == testGraph.getNodes().size());
    }

/*     @Test
    public void stationShouldHaveCorrectEdges(){
        String stationName = "Telefonplan T-bana";
        List<Edge> edges = testGraph.getNodeEdges(stationName);
    } */
}
