package parser.cfg.types;

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
    public int id; 
    public List<Instruction> instructions;
    public Set<BasicBlock> successors;
    public Set<BasicBlock> predecessors;
    public BitSet dominators;
    public int dfspPos;
    public Edge loopEdge;

    public BasicBlock(Instruction l, int id) {
        this.leader = l;
        this.instructions = new ArrayList<Instruction>();
        this.successors = new HashSet<>();
        this.predecessors = new HashSet<>();
        this.instructions.add(l);
        this.dfspPos = 0;
        this.loopEdge = null;
        this.id = id;
    }

    public void addInstruction(Instruction i) {
        this.instructions.add(i);
    }

    public void stringify() {
        for (Instruction i : this.instructions) {
            System.out.println("\t" + i.line + " " + i.type + " " + i.index1 + " " + i.index2);
        }

        System.out.println("Predecessors:");
        for (BasicBlock pred : this.predecessors) {
            System.out.print(pred.id + " ");
        }
        System.out.println();

        System.out.println();
        System.out.println("Successors:");
        for (BasicBlock succ : this.successors) {
            System.out.print(succ.id + " ");
        }
        System.out.println();

        System.out.println();
    }
}
