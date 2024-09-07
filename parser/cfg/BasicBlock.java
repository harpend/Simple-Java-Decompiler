package parser.cfg;

import java.util.HashSet;

public class BasicBlock {
    private int index;
    private String type;
    private int fromOffset;    
    private int toOffset;    
    private BasicBlock next;
    private ControlFlowGraph controlFlowGraph;
    HashSet<BasicBlock> predecessors;

    public BasicBlock(ControlFlowGraph controlFlowGraph, int index, BasicBlock original, HashSet<BasicBlock> predecessors) {
        this.controlFlowGraph = controlFlowGraph;
        this.index = index;
        this.type = original.type;
        this.fromOffset = original.fromOffset;
        this.toOffset = original.toOffset;
        this.next = original.next;
        this.predecessors = predecessors;
    }

    public BasicBlock(ControlFlowGraph controlFlowGraph, int index, String type, int fromOffset, int toOffset, HashSet<BasicBlock> predecessors) {
        this.controlFlowGraph = controlFlowGraph;
        this.index = index;
        this.type = type;
        this.fromOffset = fromOffset;
        this.toOffset = toOffset;
        this.next = null;
        this.predecessors = predecessors;
    }
}
