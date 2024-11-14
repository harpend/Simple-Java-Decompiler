package parser.cfg;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import parser.Instruction;
import parser.cfg.types.BasicBlock;
import parser.cfg.types.Edge;
import parser.cfg.types.Loop;
import parser.cfg.types.UnionFindNode;

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
    private BasicBlock head = null;
    private BasicBlock fakeEnd = null;
    private BasicBlock curBB = null;
    private Instruction prevInstruction = null;
    private BasicBlock prevBB = null;
    private Map<Integer, BitSet> dominaterMap;
    private Stack<BasicBlock> dfsStack;
    private HashSet<Integer> visited;
    private Map<BasicBlock, HashSet<BasicBlock>> backEdges;
    private Map<BasicBlock, HashSet<BasicBlock>> otherEdges;
    private Map<Integer, Integer> loopParent = new HashMap<>(); 
    private Map<BasicBlock, Integer> lowLink;
    private Map<BasicBlock, Integer> number;
    private Stack<BasicBlock> tStack;
    private List<HashSet<BasicBlock>> sccList;
    private List<Integer> lastDesc;
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
        this.loopParent = new HashMap<>();
        this.lowLink = new HashMap<>();
        this.number = new HashMap<>();
        this.tStack = new Stack<>();
        this.sccList = new ArrayList<>();
        this.lastDesc = new ArrayList<>();
        this.backEdges = new HashMap<>();
        this.otherEdges = new HashMap<>();
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
        this.visited = new HashSet<>(this.bbList.size());
        this.bbListPostorder = new ArrayList<>();
        this.bbListPreorder = new ArrayList<>();
        computeDominators();
        DFS(this.head);
        analyseLoops();
        for (Loop l : this.loopList) {
            l.stringify();
        }
        System.out.println("POLength: " + this.bbListPreorder.size());
        this.bbListReversePostorder = bbListPostorder.reversed();
        // loopTypes();
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

    private int index = 0;
    private void DFS(BasicBlock bb) {
        this.visited.add(bb.id);
        this.number.put(bb, this.index);
        this.bbListPreorder.add(bb);
        this.lastDesc.add(bb.id);
        int lastVar = this.index;
        index++;
        for (BasicBlock succ : bb.successors) {
            if (!this.visited.contains(succ.id)) {
                DFS(succ);
                lastVar = Math.max(lastVar, this.number.get(succ));
            } 
        }

        this.lastDesc.set(this.number.get(bb), lastVar);
    }

    // Havlak-Tarjan
    private void analyseLoops() {
        List<UnionFindNode> LP = new ArrayList<>();
        for (int i = 0; i < this.bbList.size(); i++) {
            BasicBlock w = this.bbListPreorder.get(i);
            this.backEdges.put(w, new HashSet<>());
            this.otherEdges.put(w, new HashSet<>());
            LP.add(new UnionFindNode(w, i));
            for (BasicBlock v : w.predecessors) {
                if (isAncestor(w, v)) {
                    this.backEdges.get(w).add(v);
                } else {
                    this.otherEdges.get(w).add(v);
                }
            }
        } 

        System.out.println(this.backEdges);
        for (int i = this.bbListPreorder.size() - 1; i >= 0; i--) {
            BasicBlock w = this.bbListPreorder.get(i);
            List<UnionFindNode> P = new ArrayList<>();
            for (BasicBlock v : this.backEdges.get(w)) {
                if (!v.equals(w)) {
                    P.add(LP.get(i));
                } else {
                    w.type = "self";
                }
            }

            Queue<UnionFindNode> workList = new LinkedList<>();
            workList.addAll(P);
            while(!workList.isEmpty()) {
                UnionFindNode ufn = workList.poll();
                for (BasicBlock y : this.otherEdges.get(ufn.getBasicBlock())) {
                    UnionFindNode yp = LP.get(this.number.get(y)).findSet();
                    if (!isAncestor(w, yp.getBasicBlock())) {
                        w.type = "irreducible";
                        this.otherEdges.get(w).add(yp.getBasicBlock());
                    } else if (yp.getDfsNumber() != this.number.get(w) && !P.contains(yp)) {
                        workList.add(yp);
                        P.add(yp);
                    }
                }
            }

            Loop l = new Loop(w, "temp");
            if (!w.type.equals("irreducible")) {
                l.isReducible = true;
            }

            for (UnionFindNode x : P) {
                UnionFindNode ufn = LP.get(this.number.get(w));
                ufn.union(x);
                if (ufn.getLoop() != null) {
                    ufn.getLoop().parentLoop = l;
                } else {
                    l.nodesInLoop.add(x.getBasicBlock());
                }
            }

            this.loopList.add(l);
        }
    }

    private boolean isAncestor(BasicBlock to, BasicBlock from) {
        int toPO = this.number.get(to);
        return ((toPO <= this.number.get(from)) && (this.number.get(from) <= this.lastDesc.get(toPO)));
    }

    // private void loopTypes() {
    //     for (Edge e : this.loopMap.keySet()) {
    //         Loop l = this.loopMap.get(e);
    //         BasicBlock h = l.backEdge.to;
    //         BasicBlock t = l.backEdge.from;

    //         int tExits = t.successors.size();
    //         int hExits = h.successors.size();
    //         if (tExits == 2) {
    //             if (hExits == 2) {
    //                 boolean inLoop = true;
    //                 for (BasicBlock succ : h.successors) {
    //                     if (!l.nodesInLoop.contains(succ)) {
    //                         inLoop = false;
    //                     }
    //                 }
                    
    //                 if (inLoop) {
    //                     l.loopType = "post";
    //                     h.instructions.addFirst(new Instruction(0, "do", 0, 0));
    //                     t.instructions.addLast(new Instruction(0, "do_end", 0, 0));
    //                   } else {
    //                     l.loopType = "pre";
    //                     h.instructions.addFirst(new Instruction(0, "while", 0, 0));
    //                     t.instructions.addLast(new Instruction(0, "while_end", 0, 0));
    //                 }
    //             } else {
    //                 l.loopType = "post";
    //                 h.instructions.addFirst(new Instruction(0, "do", 0, 0));
    //                 t.instructions.addLast(new Instruction(0, "do_end", 0, 0));
    //             }
    //         } else {
    //             if (hExits == 2) {
    //                 l.loopType = "pre";
    //                 h.instructions.addFirst(new Instruction(0, "while", 0, 0));
    //                 t.instructions.addLast(new Instruction(0, "while_end", 0, 0));
    //             } else {
    //                 l.loopType = "endless";
    //                 System.out.println("unexpected endless loop");
    //                 System.exit(1);
    //             }
    //         }
    //     }
    // }

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

    public void stringify() {
        System.out.println("Insert method name:");
        int i = 0;
        for (BasicBlock bb : this.bbList) {
            System.out.println("BB " + i + ":");
            bb.stringify();
            i++;
        }

        // if (this.loopBackEdges != null) {
        //     for (Edge bbLoopEdge : this.loopBackEdges) {
        //         System.out.println("-----loop-------");
        //         System.out.println(this.loopMap.get(bbLoopEdge).loopType);
        //         for (BasicBlock bb : this.loopMap.get(bbLoopEdge).nodesInLoop) {
        //             System.out.println(bb.id);
        //         }
        //         System.out.println("----------------");
        //     }

        // }
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
