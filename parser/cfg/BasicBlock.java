package parser.cfg;

import java.util.ArrayList;
import java.util.List;
import parser.Instruction;

public class BasicBlock {
    public Instruction leader;
    public Instruction terminator;
    public List<Instruction> instructions;
    public List<BasicBlock> successors;
    public List<BasicBlock> predecessors;

    public BasicBlock(Instruction l) {
        this.leader = l;
        this.instructions = new ArrayList<Instruction>();
        this.successors = new ArrayList<BasicBlock>();
        this.predecessors = new ArrayList<BasicBlock>();
        this.instructions.add(l);
    }

    public void addInstruction(Instruction i) {
        this.instructions.add(i);
    }

    public void stringify() {
        for (Instruction i : this.instructions) {
            System.out.println("\t" + i.line + " " + i.type + " " + i.index1 + " " + i.index2);
        }

        System.out.println("Predecessors:");
        for (BasicBlock bb : this.predecessors) {
            System.out.print(bb.leader.line + " ");
        }

        System.out.println();
        System.out.println("Successors:");
        for (BasicBlock bb : this.successors) {
            System.out.print(bb.leader.line + " ");
        }

        System.out.println();
    }
}
