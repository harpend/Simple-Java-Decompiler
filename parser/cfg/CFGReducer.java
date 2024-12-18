package parser.cfg;

import java.util.HashSet;
import parser.cfg.types.BasicBlock;

public class CFGReducer {
    public static boolean reduceCFG(ControlFlowGraph cfg) {
        BasicBlock head = cfg.head;
        HashSet<BasicBlock> visited = new HashSet(cfg.bbList.size());
        for (BasicBlock bb : cfg.bbListPostorder) {
            if (bb.successors.size() == 2) {
                if (bb.matchType(BasicBlock.TYPE_CONDITIONAL_BRANCH)) {
                    reduceConditional(bb, cfg);
                } else {
                    System.out.println("non conditional branch with 2 successors");
                    System.exit(1);
                }
            }
        }

        return true;
    }

    private static void reduceConditional(BasicBlock bb, ControlFlowGraph cfg) {
        if (bb.branch.successors.contains(bb.next)) {
            // normal if
            BasicBlock ifBB = cfg.newTypeBB(BasicBlock.TYPE_IF);
            for (BasicBlock pred : bb.predecessors) {
                ifBB.predecessors.add(pred);
                pred.successors.add(ifBB);
                pred.successors.remove(bb);
            }

            for (BasicBlock succ : bb.next.successors) {
                ifBB.successors.add(succ);
                succ.predecessors.add(ifBB);
                succ.predecessors.remove(bb);
            }

            ifBB.subNodes.add(bb); ifBB.subNodes.add(bb.branch); ifBB.subNodes.add(bb.next);
            bb.predecessors.clear();
            bb.next.successors.clear();
            int index = cfg.bbListPostorder.indexOf(bb);
            cfg.bbListPostorder.set(index, ifBB);
            cfg.bbListPostorder.removeAll(ifBB.subNodes);
        } else {
            // if-else
            if (bb.next.successors.size() > 1 || bb.branch.successors.size() > 1) {
                System.out.println("too many successors for branch or next");
                System.exit(1);
            } else if (bb.next.successors.size() == 1 && bb.branch.successors.size() == 1) {
                if (!bb.next.successors.containsAll(bb.branch.successors)) {
                    System.out.println("unexpected structure for if-else");
                    System.exit(1);
                }
            }

            BasicBlock ifeBB = cfg.newTypeBB(BasicBlock.TYPE_IF_ELSE);
            for (BasicBlock pred : bb.predecessors) {
                ifeBB.predecessors.add(pred);
                pred.successors.add(ifeBB);
                pred.successors.remove(bb);
            }

            for (BasicBlock succ : bb.next.successors) {
                ifeBB.successors.add(succ);
                succ.predecessors.add(ifeBB);
                succ.predecessors.remove(bb);
            }

            for (BasicBlock succ : bb.branch.successors) {
                ifeBB.successors.add(succ);
                succ.predecessors.add(ifeBB);
                succ.predecessors.remove(bb);
            }

            ifeBB.subNodes.add(bb); ifeBB.subNodes.add(bb.branch); ifeBB.subNodes.add(bb.next);
            bb.predecessors.clear();
            bb.next.successors.clear();
            bb.branch.successors.clear();
            int index = cfg.bbListPostorder.indexOf(bb);
            cfg.bbListPostorder.set(index, ifeBB);
            cfg.bbListPostorder.removeAll(ifeBB.subNodes);
        }
    }
}
