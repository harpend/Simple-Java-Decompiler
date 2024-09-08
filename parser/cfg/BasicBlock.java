package parser.cfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import parser.Instruction;

public class BasicBlock {
    private int index;
    private String type;
    private int fromOffset;    
    private int toOffset;    
    private BasicBlock next;
    private ControlFlowGraph controlFlowGraph;
    HashSet<BasicBlock> predecessors;
    private List<Instruction> instructions;

    public BasicBlock(ControlFlowGraph controlFlowGraph, int index, BasicBlock original, HashSet<BasicBlock> predecessors) {
        this.controlFlowGraph = controlFlowGraph;
        this.index = index;
        this.type = original.type;
        this.fromOffset = original.fromOffset;
        this.toOffset = original.toOffset;
        this.next = original.next;
        this.predecessors = predecessors;
        this.instructions = new ArrayList<Instruction>(); // this may be incorrect for this but again this constructor may not be needed
    }

    public BasicBlock(ControlFlowGraph controlFlowGraph, int index, String type, int fromOffset, int toOffset, HashSet<BasicBlock> predecessors) {
        this.controlFlowGraph = controlFlowGraph;
        this.index = index;
        this.type = type;
        this.fromOffset = fromOffset;
        this.toOffset = toOffset;
        this.next = null;
        this.predecessors = predecessors;
        this.instructions = new ArrayList<Instruction>();
    }

    public void addInstruction(Instruction i) {
        instructions.add(i);
    }
}
