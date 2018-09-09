package common.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Graph <T> {    
    
    private Map<T,Set<T>> connections;
    
    public Graph() {
        this.connections = new HashMap<>();
    }
    
    public boolean hasVertex(final T fromNode, final T toNode) { 
        Set<T> nodeConnections = connections.get(fromNode);
        if(nodeConnections != null && nodeConnections.contains(toNode)) {
            return true;    
        }        
        return false;        
    }    

    public void addVertex(final T fromNode, final T toNode) {        
        Set<T> nodeConnections = connections.get(fromNode);
        if(nodeConnections == null) {
            nodeConnections = new TreeSet<T>();
            connections.put(fromNode, nodeConnections);
        }        
        nodeConnections.add(toNode);
    }
    
    public void delVertex(final T fromNode, final T toNode) {        
        Set<T> nodeConnections = connections.get(fromNode);
        if(nodeConnections != null) {
            connections.remove(toNode);
        }        
    }
    
    public List<T> getNextAdjacentNodes(final T fromNode) {        
        List<T> nodes = new ArrayList<>(); 
        Set<T> nodeConnections = connections.get(fromNode);
        if(nodeConnections != null) {            
            for (T toNode : nodeConnections) {
                nodes.add(toNode);
            }
        }        
        return nodes;        
    }
        
    public List<T> getDependenciesForwards(final T fromNode){
        TreeSet<T> visited = new TreeSet<>();
        getDependenciesForwardsList(fromNode, visited);    
        visited.remove(fromNode);
        return new ArrayList<T> (visited);
    }
    
    private void getDependenciesForwardsList(
            final T fromNode, 
            final Set<T> visited){    
        if(visited.contains(fromNode)) {
            return;
        }
        visited.add(fromNode);        
        Set<T> nodeConnections = connections.get(fromNode);
        if(nodeConnections == null) {
            return;
        }
        for (T toNode : nodeConnections) {
            getDependenciesForwardsList(toNode, visited);
        }   
    }        
}