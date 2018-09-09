package common.graphs;

import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonGraph {
    
    public static final Long START_OPERATOR_ID = -1l;
    public static final Long END_OPERATOR_ID = -2l;
    private static final String START_OPERATOR_ID_STR = "start_operator";
    private static final String END_OPERATOR_ID_STR = "end_operator";
    private static final String START_OPERATOR_ID_STR_QUOTE = "\"start_operator\"";
    private static final String END_OPERATOR_ID_STR_QUOTE = "\"end_operator\"";
    
    public static GraphBidirectional<Long> getParseGraphBidirectional(
            final String jsonData){      
        GraphBidirectional<Long> graph = new GraphBidirectional<>();
        JsonElement jsonTree = new JsonParser().parse(jsonData);    
        Long fromNodeId = 0l, toNodeId = 0l;
        for(Entry<String, JsonElement> node : jsonTree.getAsJsonObject().entrySet()) {  
            fromNodeId = parseNodeId(node.getKey());
            for(JsonElement connection : node.getValue().getAsJsonArray()) {
                toNodeId = parseNodeId(connection.toString());
                graph.addVertex(fromNodeId, toNodeId);
            }
        }        
        return graph;
    }
    
    private static Long parseNodeId(final String nodeIdStr) {
        if(START_OPERATOR_ID_STR_QUOTE.equals(nodeIdStr)
                || START_OPERATOR_ID_STR.equals(nodeIdStr)) {
            return START_OPERATOR_ID;
        } else if(END_OPERATOR_ID_STR_QUOTE.equals(nodeIdStr)
                || END_OPERATOR_ID_STR.equals(nodeIdStr)) {
            return END_OPERATOR_ID;
        } 
        return Long.valueOf(nodeIdStr);
    }
}
