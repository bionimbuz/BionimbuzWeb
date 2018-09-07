package common.graphs;

import java.util.List;

public class GraphBidirectional<T> extends Graph<T> {
    
    private Graph<T> graphReverse;
    
    public GraphBidirectional(){
        this.graphReverse = new Graph<>();
    }    

    public void addVertex(final T fromNode, final T toNode) {        
        super.addVertex(fromNode, toNode);
        graphReverse.addVertex(toNode, fromNode);        
    }
    
    public void delVertex(final T fromNode, final T toNode) {        
        super.delVertex(fromNode, toNode);       
        graphReverse.delVertex(toNode, fromNode);        
    }
    
    public List<T> getDependenciesList(final T fromNode) {
        return graphReverse.getDependentsList(fromNode);
    }
}
