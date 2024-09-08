package parser.cfg;

import java.util.ArrayList;
import java.util.List;
import parser.Instruction;

public class BasicBlock {
    public Instruction leader;
    public List<Instruction> instructions;
    public List<BasicBlock> successors;
    public List<BasicBlock> predecessors;

    public BasicBlock(Instruction l) {
        this.leader = l;
        this.instructions = new ArrayList<Instruction>();
        this.successors = new ArrayList<BasicBlock>();
        this.instructions.add(l);
    }

    public void addInstruction(Instruction i) {
        this.instructions.add(i);
    }
}
