package parser.cfg;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import parser.Instruction;
import parser.cfg.helpers.LoopHelper;
import parser.cfg.types.BasicBlock;
import parser.cfg.types.Loop;

public class ControlFlowGraph {
    private Dictionary<String, Object> method;
    private List<Instruction> instructions;
    public List<BasicBlock> bbList;
    public List<BasicBlock> bbListPostorder;
    public List<BasicBlock> bbListPreorder;
    public List<BasicBlock> bbListReversePostorder;
    public HashMap<Integer, BasicBlock> i2bb;
    public HashMap<Integer, BasicBlock> id2bb;
    private HashSet<Integer> terminators;
    private HashSet<Integer> leaders;
    private HashSet<Integer> fall;
    private boolean fallThrough = false;
    public BasicBlock head = null;
    private BasicBlock curBB = null;
    private Instruction prevInstruction = null;
    private BasicBlock prevBB = null;
    private Map<Integer, BitSet> dominaterMap;
    private List<Loop> loopList;

    public ControlFlowGraph(Dictionary<String, Object> method) {
        this.method = method;
        this.instructions = new ArrayList<Instruction>();
        this.bbList = new ArrayList<BasicBlock>();
        this.terminators = new HashSet<Integer>();
        this.leaders = new HashSet<Integer>();
        this.fall = new HashSet<Integer>();
        this.i2bb = new HashMap<Integer, BasicBlock>();
        this.id2bb = new HashMap<>();
        this.loopList = new ArrayList<>();
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
        this.bbListPostorder = new ArrayList<>();
        this.bbListPreorder = new ArrayList<>();
        computeDominators();
        LoopHelper lhelper = new LoopHelper(this);
        this.loopList = lhelper.getLoops();
        this.bbListReversePostorder = lhelper.getPostorder().reversed();
    }

    private void generateBBS() {
        int i = 0;
        this.leaders.add(this.instructions.getFirst().line);
        for (Instruction instruction : this.instructions) {
            if (leaders.contains(instruction.line)) {
                if (this.prevInstruction != null && terminators.add(this.prevInstruction.line)) {
                    this.fall.add(this.prevInstruction.line);
                }

                this.curBB = new BasicBlock(instruction, i);
                this.i2bb.put(instruction.line, this.curBB);
                this.id2bb.put(i++, this.curBB);
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
        for (int i = 0; i < this.bbList.size(); i++) {
            BasicBlock bb = this.bbList.get(i);
            if (fallToNext) {
                bb.predecessors.add(this.prevBB);
                this.prevBB.successors.add(bb);
                this.prevBB.next = bb;
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
                    bb.branch = bbSwap;
                    bb.next = i != this.bbList.size() ? this.bbList.get(i+1) : null;
                    bb.TYPE = BasicBlock.TYPE_CONDITIONAL_BRANCH;
                } else if (t.type.contains("return")) {
                    bb.TYPE = BasicBlock.TYPE_RETURN;
                } else {
                    bb.TYPE = BasicBlock.TYPE_STATEMENTS;
                    bb.next = i != this.bbList.size() ? this.bbList.get(i+1) : null;
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
                    // dominators can be a map
                    hold.and(pred.dominators);
                }

                hold.set(bb.id);
                if (!hold.equals(bb.dominators)) {
                    changed = true;
                    bb.dominators = hold;
                }
            }
        } while (changed);
    }

    private void analyse2Way() {
        // this.unresolvedConditionals = new HashSet<>();
        for (BasicBlock bb : this.bbListReversePostorder) {
            if (bb.successors.size() == 2 && !bb.isHeader && !bb.isLatch) {
                List<BasicBlock> succList = new ArrayList<>(bb.successors);
                BasicBlock trueBlock = succList.get(0);
                BasicBlock falseBlock = succList.get(1);
                if (trueBlock.successors.size() != 1 || falseBlock.successors.size() != 1) {
                    // not an if else
                    System.out.println("unhandled if");
                    System.exit(1);
                    continue;
                }

                List<BasicBlock> trueSuccList = new ArrayList<>(trueBlock.successors);
                List<BasicBlock> falseSuccList = new ArrayList<>(falseBlock.successors);
                BasicBlock trueBlockSucc = succList.get(0);
                BasicBlock falseBlockSucc = succList.get(0);
                if (!trueBlockSucc.equals(falseBlockSucc)) {
                    // not an if else
                    System.out.println("unhandled if");
                    System.exit(1);
                    continue;
                }

                // it is an if else TODO: insert the instructions if at bb and else at falseBlock
                // verify true block and false block somehow, read bytecode
                System.out.println("-------if-else------");
                System.out.println(bb.id);
                System.out.println(trueBlock.id);
                System.out.println(falseBlock.id);
                System.out.println(trueBlockSucc.id);
                System.out.println("-------end-if-else------");
            }
        }
    }

    public void stringify() {
        System.out.println("Insert method name:");
        int i = 0;
        for (BasicBlock bb : this.bbList) {
            System.out.println("BB " + i + ":");
            bb.stringify();
            i++;
        }

        for (Loop l : this.loopList) {
            l.stringify();
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
