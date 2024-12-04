package parser.cfg.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import parser.Instruction;
import parser.cfg.ControlFlowGraph;
import parser.cfg.types.BasicBlock;
import parser.cfg.types.Loop;
import parser.cfg.types.UnionFindNode;

public class LoopHelper {
    private ControlFlowGraph cfg;
    private HashSet<Integer> visited;
    private List<BasicBlock> bbListPreorder;
    private List<BasicBlock> bbListPostorder;
    private List<Integer> lastDesc;
    private Map<BasicBlock, Integer> number;
    private Map<BasicBlock, HashSet<BasicBlock>> backEdges;
    private Map<BasicBlock, HashSet<BasicBlock>> otherEdges;
    private List<UnionFindNode> LP;
    private List<Loop> loopList;


    public LoopHelper(ControlFlowGraph cfg) {
        this.cfg = cfg;
        this.loopList = new ArrayList<>();
        this.visited = new HashSet<>();
        this.bbListPreorder = new ArrayList<>();
        this.bbListPostorder = new ArrayList<>();
        this.lastDesc = new ArrayList<>();
        this.number = new HashMap<>();
        this.backEdges = new HashMap<>();
        this.otherEdges = new HashMap<>();
    }

    public List<Loop> getLoops() {
        DFS(this.cfg.head, 0);
        analyseLoops();
        loopTypes();
        return this.loopList;
    }

    // TODO add reduce loop method in Loop type see jd-core
    private int DFS(BasicBlock bb, int index) {
        this.visited.add(bb.id);
        this.number.put(bb, index);
        this.bbListPreorder.add(bb);
        this.lastDesc.add(index);
        int lastVar = index;
        for (BasicBlock succ : bb.successors) {
            if (!this.visited.contains(succ.id)) {
                lastVar = DFS(succ, index+1);
            } 
        }

        this.bbListPostorder.add(bb);
        this.lastDesc.set(this.number.get(bb), lastVar);
        return lastVar;
    }

        // Havlak-Tarjan
    private void analyseLoops() {
        this.LP = new ArrayList<>();
        for (int i = 0; i < this.cfg.bbList.size(); i++) {
            BasicBlock w = this.bbListPreorder.get(i);
            this.backEdges.put(w, new HashSet<>());
            this.otherEdges.put(w, new HashSet<>());
            LP.add(new UnionFindNode(w, i));
            for (BasicBlock v : w.predecessors) {
                if (isAncestor(w, v)) {
                    this.backEdges.get(w).add(v);
                    v.isLatch = true;
                } else {
                    this.otherEdges.get(w).add(v);
                }
            }
        } 

        for (int i = this.bbListPreorder.size() - 1; i >= 0; i--) {
            BasicBlock w = this.bbListPreorder.get(i);
            List<UnionFindNode> P = new ArrayList<>();
            for (BasicBlock v : this.backEdges.get(w)) {
                if (!v.equals(w)) {
                    P.add(LP.get(this.number.get(v)).findSet());
                } else {
                    w.type = "self";
                    Loop loop = new Loop(w, "temp");
                    this.loopList.add(loop);
                    LP.get(i).setLoop(loop);
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
                    } else if (!this.number.get(yp.getBasicBlock()).equals(this.number.get(w)) && !P.contains(yp)) {
                        workList.add(yp);
                        P.add(yp);
                    }
                }
            }
            
            if (!P.isEmpty()) {
                Loop l = new Loop(w, "temp");
                w.isHeader = true;
                if (!w.type.equals("irreducible")) {
                    l.isReducible = true;
                }
    
                LP.get(i).setLoop(l);
                UnionFindNode ufn = LP.get(this.number.get(w));
                for (UnionFindNode x : P) {
                    x.union(ufn);
                    if (x.getLoop() != null) {
                        ufn.getLoop().parentLoop = l;
                    } else {
                        BasicBlock bb = x.getBasicBlock();
                        if (this.backEdges.get(w).contains(bb)) {
                            l.terminator = bb;
                        }

                        l.nodesInLoop.add(x.getBasicBlock());
                    }
                }
    
                this.loopList.add(l);
            }
        }
    }

    private boolean isAncestor(BasicBlock to, BasicBlock from) {
        int toPO = this.number.get(to);
        return ((toPO <= this.number.get(from)) && (this.number.get(from) <= this.lastDesc.get(toPO)));
    }

    private void loopTypes() {
        for (Loop l : this.loopList) {
            int tExits = l.terminator.successors.size();
            int hExits = l.header.successors.size();
            if (tExits == 2) {
                if (hExits == 2) {
                    boolean inLoop = true;
                    if (!l.header.type.equals("self")) {
                        for (BasicBlock succ : l.header.successors) {
                            if (!l.nodesInLoop.contains(succ) && !l.header.equals(this.LP.get(this.number.get(succ)).findSet().getBasicBlock())) {
                                System.out.println(this.number.get(l.header));
                                inLoop = false;
                            }
                        }
                    }
                    
                    if (inLoop) {
                        l.loopType = "post";
                        l.header.instructions.addFirst(new Instruction(0, "do", 0, 0));
                        l.terminator.instructions.addLast(new Instruction(0, "do_end", 0, 0));
                      } else {
                        System.out.println("check");
                        l.loopType = "pre";
                        l.header.instructions.addFirst(new Instruction(0, "while", 0, 0));
                        l.terminator.instructions.addLast(new Instruction(0, "while_end", 0, 0));
                    }
                } else {
                    l.loopType = "post";
                    l.header.instructions.addFirst(new Instruction(0, "do", 0, 0));
                    l.terminator.instructions.addLast(new Instruction(0, "do_end", 0, 0));
                }
            } else {
                if (hExits == 2) {
                    l.loopType = "pre";
                    l.header.instructions.addFirst(new Instruction(0, "while", 0, 0));
                    l.terminator.instructions.addLast(new Instruction(0, "while_end", 0, 0));
                } else {
                    l.loopType = "endless";
                    System.out.println("unexpected endless loop detected in cfg");
                    System.exit(1);
                }
            }
        }
    }

    public List<BasicBlock> getPostorder() {
        return this.bbListPostorder;
    }

    public List<BasicBlock> getPreorder() {
        return this.bbListPreorder;
    }

}
