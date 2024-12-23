package parser.cfg;

import java.util.ArrayList;
import java.util.List;
import parser.Instruction;
import parser.cfg.types.BasicBlock;

public class CFGReducer {
    public static boolean reduceCFG(ControlFlowGraph cfg) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (BasicBlock bb : cfg.bbListPostorder) {
                if (bb.successors.size() == 2) {
                    if (bb.matchType(BasicBlock.TYPE_CONDITIONAL_BRANCH)) {
                        changed = reduceConditional(bb, cfg);
                    } else {
                        System.out.println("non conditional branch with 2 successors");
                        System.exit(1);
                    }
                } else if (bb.successors.size() == 1) {
                    changed = reduceConsecutive(bb, cfg);
                }
            }
        }

        if (cfg.bbListPostorder.size() != 1) {
            System.out.println("failed to reduce CFG");
            return false;
        }

        return true;
    }

    private static boolean reduceConditional(BasicBlock bb, ControlFlowGraph cfg) {
        if (bb.branch.matchType(BasicBlock.TYPE_RETURN)) {
            bb.instructions.getLast().flip();
            BasicBlock tmp = bb.branch;
            bb.branch = bb.next;
            bb.next = tmp;
        }
        
        if (bb.branch.successors.contains(bb.next)) {
            System.out.println("9regu");
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
            ifBB.branch = bb.next.branch;
            ifBB.next = bb.next.next;
            bb.branch.instructions.addFirst(new Instruction(0, "if", 0, 0));
            bb.next.instructions.addFirst(new Instruction(0, "if_end", 0, 0));
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
            bb.branch.instructions.addFirst(new Instruction(0, "if", 0, 0));
            bb.next.instructions.addFirst(new Instruction(0, "if_end", 0, 0));
            bb.predecessors.clear();
            bb.next.successors.clear();
            bb.branch.successors.clear();
            int index = cfg.bbListPostorder.indexOf(bb);
            cfg.bbListPostorder.set(index, ifeBB);
            cfg.bbListPostorder.removeAll(ifeBB.subNodes);
        }

        return true;
    }

    private static boolean reduceConsecutive(BasicBlock bb, ControlFlowGraph cfg) {
        if (bb.successors.size() == 1) {
            for (BasicBlock basicBlock : bb.successors) {
                if (basicBlock.predecessors.size() != 1 && !basicBlock.predecessors.contains(bb)) {
                    System.out.println("could not reduce consecutive");
                    System.exit(1);
                }
            }

            List<BasicBlock> subs = enumerateConsecutives(bb);
            if (subs.size() <= 1) {
                return false;
            }
            BasicBlock statsBB = cfg.newTypeBB(BasicBlock.TYPE_STATEMENTS);
            statsBB.subNodes.addAll(subs);
            BasicBlock last = statsBB.subNodes.getLast();
            int index = cfg.bbListPostorder.indexOf(bb);
            cfg.bbListPostorder.set(index, statsBB);
            cfg.bbListPostorder.removeAll(statsBB.subNodes);
            statsBB.successors.addAll(last.successors);
            statsBB.predecessors.addAll(bb.predecessors);
            for (BasicBlock basicBlock : bb.predecessors) {
                basicBlock.successors.remove(bb);
                basicBlock.successors.add(statsBB);
            }
            
            bb.predecessors.clear();
            BasicBlock lastBB = statsBB.subNodes.getLast();
            for (BasicBlock basicBlock : lastBB.successors) {
                basicBlock.successors.remove(lastBB);
                basicBlock.successors.add(statsBB);
            }

            lastBB.successors.clear();
            return true;
        }

        System.out.println("could not reduce consecutive");
        System.exit(1);
        return false;
    }

    private static List<BasicBlock> enumerateConsecutives(BasicBlock bb) {
        List<BasicBlock> consecBlocks = new ArrayList<>();
        boolean check = true;
        BasicBlock tmp = bb;
        while (tmp.successors.size() <= 1 && check) {
            check = false;
            consecBlocks.add(tmp);
            for (BasicBlock basicBlock : tmp.successors) {
                if (basicBlock.predecessors.size() == 1 && basicBlock.predecessors.contains(tmp)) {
                    tmp = basicBlock;
                    check = true;
                }
            }
        }

        return consecBlocks;
    }
}
