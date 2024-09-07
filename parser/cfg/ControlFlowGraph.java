package parser.cfg;

import java.util.Dictionary;
import java.util.List;

public class ControlFlowGraph {
    private Dictionary<String, Object> method;
    private List<BasicBlock> bbList;
    
    public ControlFlowGraph(Dictionary<String, Object> method) {
        this.method = method;
    }
}
