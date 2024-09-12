package parser.cfg;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import parser.Instruction;

public class ControlFlowGraph {
    private Dictionary<String, Object> method;
    private List<Instruction> instructions;
    private List<BasicBlock> bbList;
    private HashMap<Integer, BasicBlock> i2bb;
    private HashSet<Integer> terminators;
    private HashSet<Integer> leaders;
    private HashSet<Integer> fall;
    private boolean fallThrough = false;
    private BasicBlock head = null;
    private BasicBlock fakeEnd = null;
    private BasicBlock curBB = null;
    private Instruction prevInstruction = null;
    private BasicBlock prevBB = null;

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
        reduceCFG();
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

    
    private void reduceCFG() {
        List<Instruction> newInstructions = new ArrayList<Instruction>();
        for (BasicBlock bb : this.bbList) {
            int leader = bb.leader.line;

            // do while - may be able to tag bb upon creation/linking 
            // instead to reduce need for checking
            if (bb.successors.contains(leader) && bb.predecessors.contains(leader)) {
                bb.instructions.addFirst(new Instruction(-1, "do", 0, 0));
                bb.instructions.addLast(new Instruction(-1, "do_end", 0, 0));
            }

            for (Instruction i : bb.instructions) {
                newInstructions.add(i);
            }
        }

        this.instructions = newInstructions;
    }

    private void forwardVisit(BasicBlock bb) {
        bb.visited = true;
        // pre visit
        for (BasicBlock basicBlock : bb.successors) {
            if (!basicBlock.visited) {
                forwardVisit(bb);
            }
        }

        // post visit

    }

    private void computeDominators() {
        // bitset has a higher overhead so bitvectors could be used to improve this
        boolean changed = false;
        int i = 0;
        for (BasicBlock bb : this.bbList) {
            bb.id = i++;
            bb.dominators = new BitSet(bbList.size());
            bb.dominators.set(0, bbList.size(), true);
        }

        this.head.dominators.set(0, bbList.size(), false);
        this.head.dominators.set(this.head.id);
        BitSet T = new BitSet(bbList.size());
        do { 
            changed = false;
            for (BasicBlock bb : this.bbList) {
                if (bb.equals(this.head)) {
                    continue;
                }

                for (BasicBlock basicBlock : bb.predecessors) {
                    T.set(0, bbList.size(), false);
                    T.or(bb.dominators);
                    bb.dominators.and(basicBlock.dominators);
                    bb.dominators.set(bb.id);
                    if (!bb.dominators.equals(T)) {
                        changed = true;
                    }
                }
            }
        } while (changed);
    }

    public void stringify() {
        System.out.println("Insert method name:");
        int i = 0;
        for (BasicBlock bb : this.bbList) {
            System.out.println("BB " + i + ":");
            bb.stringify();
            i++;
        }
    }

    public List<Instruction> getInstructions() {
        return this.instructions;
    }

}
