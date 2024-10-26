package parser.cfg;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BasicBlock2 {
    public Integer leader;
    public Integer terminator;
    public boolean visited;
    public int id;
    public List<Integer> instructions;
    public Set<BasicBlock> successors;
    public Set<BasicBlock> predecessors;
    public BitSet dominators;

    public BasicBlock2(Integer l) {
        this.leader = l;
        this.instructions = new ArrayList<Integer>();
        this.successors = new HashSet<>();
        this.predecessors = new HashSet<>();
        this.instructions.add(l);
    }

    public void addInstruction(Integer i) {
        this.instructions.add(i);
    }

    public void stringify() {
        // for (Instruction i : this.instructions) {
        //     System.out.println("\t" + i.line + " " + i.type + " " + i.index1 + " " + i.index2);
        // }

        System.out.println("Predecessors:");
        System.out.println(this.predecessors);

        System.out.println();
        System.out.println("Successors:");
        System.out.println(this.successors);

        System.out.println();
    }
}
