package parser.cfg;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import parser.Instruction;

public class ControlFlowGraph {
    private Dictionary<String, Object> method;
    private List<Instruction> instructions;
    public List<BasicBlock> bbList;
    public HashMap<Integer, BasicBlock> i2bb;
    private HashSet<Integer> terminators;
    private HashSet<Integer> leaders;
    private HashSet<Integer> fall;
    private boolean fallThrough = false;
    private BasicBlock head = null;
    private BasicBlock fakeEnd = null;
    private BasicBlock curBB = null;
    private Instruction prevInstruction = null;
    private BasicBlock prevBB = null;
    private List<List<BasicBlock>> loopList;

    public ControlFlowGraph(Dictionary<String, Object> method) {
        this.method = method;
        this.instructions = new ArrayList<Instruction>();
        this.bbList = new ArrayList<BasicBlock>();
        this.terminators = new HashSet<Integer>();
        this.leaders = new HashSet<Integer>();
        this.fall = new HashSet<Integer>();
        this.i2bb = new HashMap<Integer, BasicBlock>();
    }

    public Instruction addInstruction(Instruction i, boolean cfChange) {
        if (fallThrough) {
            leaders.add(i.line);
            fallThrough = false;
        }

        this.instructions.add(i);
        if (cfChange) {
            terminators.add(i.line);
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
        this.leaders.add(this.instructions.getFirst().line);
        for (Instruction instruction : this.instructions) {
            if (leaders.contains(instruction.line)) {
                if (this.prevInstruction != null && terminators.add(this.prevInstruction.line)) {
                    this.fall.add(this.prevInstruction.line);
                }

                this.curBB = new BasicBlock(instruction);
                this.i2bb.put(instruction.line, this.curBB);
                this.bbList.add(curBB);
                if (instruction.equals(this.instructions.getFirst())) {
                    this.head = this.curBB;
                }
            } else {
                this.curBB.addInstruction(instruction);
            }

            this.prevInstruction = instruction;
        }
    }

    private void linkBBS() {
        boolean fallToNext = false;
        this.fakeEnd = new BasicBlock(new Instruction(-1, null, 0, 0));
        for (BasicBlock bb : this.bbList) {
            if (fallToNext) {
                bb.predecessors.add(this.prevBB);
                this.prevBB.successors.add(bb);
            }

            Instruction t = bb.instructions.getLast();
            if (this.terminators.contains(t.line)) {
                if (this.fall.contains(t.line)) {
                    fallToNext = true;
                }

                if (t.type.equals("if_icmple")) {
                    BasicBlock bbSwap = this.i2bb.get(t.index1);
                    bb.successors.add(bbSwap);
                    bbSwap.predecessors.add(bb);
                } else if (t.type.contains("return")) {
                    // bb.successors.add(this.fakeEnd.leader.line); i dont think this line is needed?
                    this.fakeEnd.predecessors.add(bb);
                }
            } else {
                System.out.println("error with terminators");
                System.exit(1);
            }
            this.prevBB = bb;
        }
    }

    private void computeDominators() {
        int i = 0;
        for (BasicBlock bb : this.bbList) {
            bb.id = i++;
            bb.dominators = new BitSet(this.bbList.size());
            bb.dominators.set(0, this.bbList.size());
        }

        this.head.dominators.set(1, this.bbList.size(), false);

        boolean changed = false;
        do { 
            changed = false;
            for (BasicBlock bb : this.bbList) {
                if (bb.equals(this.head)) {
                    continue;
                }

                BitSet hold = new BitSet(this.bbList.size());
                hold.set(0, this.bbList.size());
                for (BasicBlock pred : bb.predecessors) {
                    hold.and(pred.dominators);
                }

                hold.set(bb.id);
                if (!hold.equals(bb.dominators)) {
                    changed = true;
                    bb.dominators = hold;
                }
            }
        } while (changed);

        for (BasicBlock bb : this.bbList) {
            System.out.println(bb.dominators);
        }
    }

    private void findLoops() {
        this.loopList = new ArrayList<List<BasicBlock>>();
        for (BasicBlock bb : this.bbList) {
            if (bb.equals(this.head)) {
                continue;
            }

            for (BasicBlock succ : bb.successors) {
                if (bb.dominators.get(succ.id)) {
                    this.loopList.add(computeLoop(succ, bb));
                }
            }
        }
    }

    private List<BasicBlock> computeLoop(BasicBlock succ, BasicBlock bb) {
        List<BasicBlock> loop = new ArrayList<BasicBlock>();
        loop.add(bb);
        Stack<BasicBlock> workList = new Stack<BasicBlock>();
        BasicBlock block;
        if (succ != bb) {
            workList.add(succ);
            loop.add(succ);
        }

        while (!workList.empty()) {
            block = workList.pop();
            for (BasicBlock basicBlock : block.predecessors) {
                if (!loop.contains(basicBlock)) {
                    loop.add(basicBlock);
                    workList.add(basicBlock);
                }
            }
        }

        return loop;
    }

    private void introduceLoops() {
        // only supports do while and un nested loops
        // also make loop a specific class and add functions to it
        // add compare function to determine if one loop is deeper than the other
        // maybe by doing a function that calculates the number of elements in a loop
        // and add 1 to all the loops that contain that basic block and are smaller in the number of basic blocks they contain
        
        for (List<BasicBlock> loop : this.loopList) {
            loop.getFirst().instructions.addFirst(new Instruction(0, "do", 0, 0));
            loop.getLast().instructions.addLast(new Instruction(0, "do_end", 0, 0));
        }
    }

    private void sortLoops() {
        // sort the loops from the inner most to the outermost
    }

    public void stringify() {
        System.out.println("Insert method name:");
        int i = 0;
        for (BasicBlock bb : this.bbList) {
            System.out.println("BB " + i + ":");
            bb.stringify();
            i++;
        }

        for (List<BasicBlock> bbLoopList : this.loopList) {
            System.out.println("Loop");
            for (BasicBlock bb : bbLoopList) {
                System.out.println("\t" + bb.id);
            }
            System.out.println("Loop End");
        }
    }

    public List<Instruction> getInstructions() {
        List<Instruction> newInstructions = new ArrayList<Instruction>();
        for (BasicBlock bb : this.bbList) {
            for (Instruction i : bb.instructions) {
                newInstructions.add(i);
            }
        }
        return newInstructions;
    }

}
