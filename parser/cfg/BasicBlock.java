package parser.cfg;

import java.util.ArrayList;
import java.util.List;
import parser.Instruction;

public class BasicBlock {
    public Instruction leader;
    public Instruction terminator;
    public List<Instruction> instructions;
    public List<Integer> successors;
    public List<Integer> predecessors;

    public BasicBlock(Instruction l) {
        this.leader = l;
        this.instructions = new ArrayList<Instruction>();
        this.successors = new ArrayList<Integer>();
        this.predecessors = new ArrayList<Integer>();
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
        for (Integer i : this.predecessors) {
            System.out.print(i + " ");
        }

        System.out.println();
        System.out.println("Successors:");
        for (Integer i : this.successors) {
            System.out.print(i + " ");
        }

        System.out.println();
    }
}
