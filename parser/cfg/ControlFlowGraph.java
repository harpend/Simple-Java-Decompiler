package parser.cfg;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import parser.Instruction;

public class ControlFlowGraph {
    private Dictionary<String, Object> method;
    private List<Instruction> instructions;
    public List<BasicBlock> bbList;
    public List<BasicBlock> bbListPostorder;
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
    private Map<Integer, BitSet> dominaterMap;
    private Stack<BasicBlock> dfsStack;
    private HashSet<BasicBlock> visited;
    private HashSet<BasicBlock> loopHeaders;
    private HashMap<BasicBlock, Loop> loopMap;

    public ControlFlowGraph(Dictionary<String, Object> method) {
        this.method = method;
        this.instructions = new ArrayList<Instruction>();
        this.bbList = new ArrayList<BasicBlock>();
        this.terminators = new HashSet<Integer>();
        this.leaders = new HashSet<Integer>();
        this.fall = new HashSet<Integer>();
        this.i2bb = new HashMap<Integer, BasicBlock>();
        this.loopHeaders = new HashSet<>();
        this.loopMap = new HashMap<>();
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
        this.visited = new HashSet<>(this.bbList.size());
        this.dfsStack = new Stack<>();
        this.bbListPostorder = new ArrayList<>();
        depthFirstSearch(this.head, 1);
        loopTypes();
        // computeDominators();
    }

    private void generateBBS() {
        int i = 0;
        this.leaders.add(this.instructions.getFirst().line);
        for (Instruction instruction : this.instructions) {
            if (leaders.contains(instruction.line)) {
                if (this.prevInstruction != null && terminators.add(this.prevInstruction.line)) {
                    this.fall.add(this.prevInstruction.line);
                }

                this.curBB = new BasicBlock(instruction, i++);
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
        // this.fakeEnd = new BasicBlock(new Instruction(-1, null, 0, 0));
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
                    // this.fakeEnd.predecessors.add(bb);
                }
            } else {
                System.out.println("error with terminators");
                System.exit(1);
            }
            this.prevBB = bb;
        }
    }

    private BasicBlock depthFirstSearch(BasicBlock bb, int dfspPos) {
        this.visited.add(bb);
        bb.dfspPos = dfspPos;
        for (BasicBlock succ : bb.successors) {
            if (!this.visited.contains(succ)) {
                BasicBlock nh = depthFirstSearch(succ, dfspPos + 1);
                tagLHead(bb, nh);
            } else if (succ.dfspPos > 0) {
                this.loopHeaders.add(succ);
                Loop l = new Loop(succ, "temp");
                this.loopMap.put(succ, l);

                l.nodesInLoop.add(succ);
                succ.loopHeader = succ.id;
                tagLHead(bb, succ);
            } else if(succ.loopHeader == null) {
                
            } else {
                BasicBlock h = this.i2bb.get(succ.loopHeader);
                if (h.dfspPos > 0) {
                    tagLHead(bb, h);
                } else {
                    // re-entry
                    System.out.println("reentry unsupported");
                    System.exit(1);
                }
            }
        }

        bb.dfspPos = 0;
        return this.i2bb.get(bb.loopHeader);
    }

    private void tagLHead(BasicBlock bb, BasicBlock head) {
        if (bb.equals(head) || head == null) { return; }
        BasicBlock temp1 = bb;
        BasicBlock temp2 = head;
        while (temp1.loopHeader != null) {
            BasicBlock ih = this.i2bb.get(temp1.loopHeader);
            if (ih.equals(temp2)) { return; }
            if (ih.dfspPos < temp2.dfspPos) {
                temp1.loopHeader = temp2.id;
                this.loopMap.get(temp2).nodesInLoop.add(temp1);
                temp1 = temp2;
                temp2 = ih;
            } else {
                temp1 = ih;
            }
        }

        temp1.loopHeader = temp2.id;
        this.loopMap.get(temp2).nodesInLoop.add(temp1);
    }

    private void loopTypes() {
        for (BasicBlock h : this.loopMap.keySet()) {
            Loop l = this.loopMap.get(h);
            BasicBlock t = l.nodesInLoop.getLast();
            int tExits = t.successors.size();
            int hExits = h.successors.size();
            if (tExits == 2) {
                if (hExits == 2) {
                    boolean inLoop = true;
                    for (BasicBlock succ : h.successors) {
                        if (!l.nodesInLoop.contains(succ)) {
                            inLoop = false;
                        }
                    }
                    
                    if (inLoop) {
                        System.out.println("check1");
                        l.loopType = "pre";
                        h.instructions.addFirst(new Instruction(0, "while", 0, 0));
                        t.instructions.addLast(new Instruction(0, "while_end", 0, 0));
                    } else {
                        l.loopType = "post";
                        h.instructions.addFirst(new Instruction(0, "do", 0, 0));
                        t.instructions.addLast(new Instruction(0, "do_end", 0, 0));
                    }
                } else {
                    l.loopType = "post";
                    h.instructions.addFirst(new Instruction(0, "do", 0, 0));
                    t.instructions.addLast(new Instruction(0, "do_end", 0, 0));
                }
            } else {
                if (hExits == 2) {
                    System.out.println("check2");
                    l.loopType = "pre";
                    h.instructions.addFirst(new Instruction(0, "while", 0, 0));
                    t.instructions.addLast(new Instruction(0, "while_end", 0, 0));
                } else {
                    l.loopType = "endless";
                    System.out.println("unexpected endless loop");
                    System.exit(1);
                }
            }
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

        for (BasicBlock bb : this.bbList) {
            System.out.println(bb.dominators);
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

        if (this.loopHeaders != null) {
            for (BasicBlock bbLoopHeader : this.loopHeaders) {
                System.out.println("-----loop-------");
                System.out.println(this.loopMap.get(bbLoopHeader).loopType);
                for (BasicBlock bb : this.loopMap.get(bbLoopHeader).nodesInLoop) {
                    System.out.println(bb.id);
                }
                System.out.println("----------------");
            }

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
