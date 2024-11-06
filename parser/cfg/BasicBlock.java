package parser.cfg;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import parser.Instruction;

public class BasicBlock {
    public Instruction leader;
    public Instruction terminator;
    public boolean visited;
    public int id; // reverse postorder numbering
    public List<Instruction> instructions;
    public Set<BasicBlock> successors;
    public Set<BasicBlock> predecessors;
    public BitSet dominators;
    public int dsfpPos;
    public Integer loopHeader;

    public BasicBlock(Instruction l) {
        this.leader = l;
        this.instructions = new ArrayList<Instruction>();
        this.successors = new HashSet<>();
        this.predecessors = new HashSet<>();
        this.instructions.add(l);
        this.dsfpPos = 0;
        this.loopHeader = null;
    }

    public void addInstruction(Instruction i) {
        this.instructions.add(i);
    }

    public void stringify() {
        for (Instruction i : this.instructions) {
            System.out.println("\t" + i.line + " " + i.type + " " + i.index1 + " " + i.index2);
        }

        System.out.println("Predecessors:");
        System.out.println(this.predecessors);

        System.out.println();
        System.out.println("Successors:");
        System.out.println(this.successors);

        System.out.println();
    }
}
