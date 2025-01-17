package parser.cfg;

import java.awt.RenderingHints;
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
    private boolean gotoChecker = false;
    public BasicBlock head = null;
    private BasicBlock curBB = null;
    private Instruction prevInstruction = null;
    private BasicBlock prevBB = null;
    private Map<Integer, BitSet> dominaterMap;
    public List<Loop> loopList;
    private LoopHelper lhelper;
    private BasicBlock endBB;

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
        this.lhelper = null;
    }

    public Instruction addInstruction(Instruction i, boolean cfChange) {
        if (this.fallThrough) {
            leaders.add(i.line);
            this.fallThrough = false;
        }
        
        if (this.gotoChecker) {
            leaders.add(i.line);
            this.gotoChecker = false;
        }

        this.instructions.add(i);
        if (cfChange) {
            terminators.add(i.line);
            if (i.type.equals("if_icmple") || i.type.equals("if_icmpgt") ||
                i.type.equals("ifge") || i.type.equals("ifgt") ||
                i.type.equals("ifle") || i.type.equals("iflt")) {
                leaders.add(i.index1);
                fall.add(i.line);
                fallThrough = true;
            } else if (i.type.contains("invoke")) {
                fall.add(i.line);
                fallThrough = true;
            } else if (i.type.equals("goto")) {
                leaders.add(i.index1);
                this.gotoChecker = true;
            }
        } 

        return i;
    }

    public void generateCFG() {
        generateBBS();
        this.head = this.bbList.getFirst();
        linkBBS(); 
        removeUnreacableBBs();
        this.bbListPostorder = new ArrayList<>();
        this.bbListPreorder = new ArrayList<>();
        computeDominators();
        this.lhelper = new LoopHelper(this);
        this.lhelper.getLoops();
        this.lhelper.reduceLoops();
        this.bbListPostorder = this.lhelper.getPostorder();
        boolean reduced = CFGReducer.reduceCFG(this);
        if (!reduced) {
            System.exit(1);
        }
    }

    private void generateBBS() {
        int i = 0;
        this.leaders.add(this.instructions.getFirst().line);
        for (Instruction instruction : this.instructions) {
            if (leaders.contains(instruction.line)) {
                if (this.prevInstruction != null && terminators.add(this.prevInstruction.line) && !this.prevInstruction.type.equals("goto")) {
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

        this.endBB = newTypeBB(BasicBlock.TYPE_END);
        this.endBB.instructions.add(new Instruction(-1, "nop", 0, 0));
        this.endBB.type = "end";
        this.terminators.add(-1);
    }

    private void linkBBS() {
        boolean fallToNext = false;
        for (int i = 0; i < this.bbList.size(); i++) {
            BasicBlock bb = this.bbList.get(i);
            if (fallToNext) {
                bb.predecessors.add(this.prevBB);
                this.prevBB.successors.add(bb);
                this.prevBB.next = bb;
                fallToNext = false;
            }

            Instruction t = bb.instructions.getLast();
            if (this.terminators.contains(t.line)) {
                if (this.fall.contains(t.line)) {
                    fallToNext = true;
                }

                if (t.type.equals("if_icmple") || t.type.equals("if_icmpgt") ||
                    t.type.equals("ifge") || t.type.equals("ifgt") ||
                    t.type.equals("ifle") || t.type.equals("iflt")) {
                    BasicBlock bbSwap = this.i2bb.get(t.index1);
                    if (bbSwap == null) {
                        bbSwap = this.endBB;
                        t.index1 = -1;
                    }

                    bb.successors.add(bbSwap);
                    bbSwap.predecessors.add(bb);
                    bb.branch = bbSwap;
                    bb.next = i + 1 != this.bbList.size() ? this.bbList.get(i+1) : null;
                    bb.TYPE = BasicBlock.TYPE_CONDITIONAL_BRANCH;
                } else if (t.type.contains("return")) {
                    bb.TYPE = BasicBlock.TYPE_RETURN + BasicBlock.TYPE_STAT;
                    bb.successors.add(this.endBB);
                    this.endBB.predecessors.add(bb);
                } else if (t.type.equals("goto")) {
                    BasicBlock bbSwap = this.i2bb.get(t.index1);
                    if (bbSwap == null) {
                        bbSwap = this.endBB;
                        t.index1 = -1;
                    }

                    bb.successors.add(bbSwap);
                    bbSwap.predecessors.add(bb);
                    bb.branch = bbSwap;
                    bb.TYPE = BasicBlock.TYPE_GOTO;
                } else if (t.type.equals("nop")) {

                } else {
                    bb.TYPE = BasicBlock.TYPE_STAT;
                    bb.next = i + 1 != this.bbList.size() ? this.bbList.get(i+1) : null;
                }
            } else {
                System.out.println(t.line);
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

    private void removeUnreacableBBs() {
        List<BasicBlock> toRemoveList = new ArrayList<>();
        for (BasicBlock bb : this.bbList) {
            if (!bb.equals(this.head) && bb.predecessors.isEmpty()) {
                toRemoveList.add(bb);
            }
        }

        for (BasicBlock bb : toRemoveList) {
            removeBB(bb);
        }
    }

    public void removeBB(BasicBlock bb) {
        this.bbList.remove(bb);
        for (BasicBlock succ : bb.successors) {
            succ.predecessors.remove(bb);
        }

        for (BasicBlock pred : bb.predecessors) {
            pred.successors.remove(bb);
        }

        bb.successors.clear();;
        bb.predecessors.clear();
    }

    public void stringify() {
        System.out.println("--------------");
        System.out.println("Insert method name:");
        int i = 0;
        for (BasicBlock bb : this.bbList) {
            System.out.println("BB " + i + ":");
            bb.stringify();
            
            i++;
        }
        
        System.out.println("--------------");
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

    public BasicBlock newBB(BasicBlock bb) {
        BasicBlock newBB = new BasicBlock(bb, this.bbList.size());
        bbList.add(newBB);
        return newBB;
    }

    public BasicBlock newTypeBB(int type) {
        BasicBlock newBB = new BasicBlock(type, this.bbList.size());
        bbList.add(newBB);
        return newBB;
    }

}
