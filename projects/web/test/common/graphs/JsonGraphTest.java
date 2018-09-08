package common.graphs;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class JsonGraphTest {
    
    @Test
    public void parseGraph(){           
        GraphBidirectional<Long> graph = 
                JsonGraph.getParseGraphBidirectional(
                        "{"
                        + "'12':[14],"
                        + "'13':['end_operator'],"
                        + "'14':['end_operator'],"
                        + "'start_operator':[12,13]"
                        + "}");
        assertFalse(graph == null);
    }
}
