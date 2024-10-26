package parser.cfg;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import parser.Instruction;

public class ControlFlowGraph2 {
    private HashSet<Integer> leaders;
    private HashSet<Integer> terminators;
    private List<Integer> instructions;
    private List<BasicBlock2> bbList;
    private HashSet<Integer> fall;
    private boolean fallThrough = false;
    public ControlFlowGraph2() {
        this.leaders = new HashSet<Integer>();
        this.terminators = new HashSet<Integer>();
        this.instructions = new ArrayList<Integer>();
        this.fall = new HashSet<Integer>();
        this.bbList = new ArrayList<>();
    }

    // this adds an instruction to the control flow graph and notes whether it indicates a cf change
    public Instruction addInstruction(Instruction i, boolean cfChange) {
        if (fallThrough) {
            this.leaders.add(i.line);
            fallThrough = false;
        }

        this.instructions.add(i.line);
        if (cfChange) {
            this.terminators.add(i.line);
            // instructions that induce cf change
            if (i.type.equals("if_icmple")) {
                leaders.add(i.index1);
                fall.add(i.line);
                fallThrough = true;
            } else if (i.type.contains("invoke")) {
                fall.add(i.line);
                fallThrough = true;
            } 
        }

        return i;
    }

    public void generateCFG() {
        generateBBS();
        this.head = this.bbList.getFirst();
        linkBBS(); 
        computeDominators();
        findLoops();
        introduceLoops();
    }

    private void generateBBS() {
        Integer prevInstruction = null;
        BasicBlock2 bb = new BasicBlock2(this.instructions.getFirst());
        this.leaders.add(this.instructions.getFirst());
        for (Integer i : this.instructions) {
            if (leaders.contains(i)) {
                if (prevInstruction != null && this.terminators.add(prevInstruction)) {
                    // this won't cause issues with jumps away as they will have already been detected as a terminator
                    this.fall.add(prevInstruction);
                }

                bb = new BasicBlock2(i);
                this.bbList.add(bb);
            } else {
                bb.instructions.add(i);
            }

            prevInstruction = i;
        }
    }
}
