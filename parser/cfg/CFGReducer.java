package parser.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import parser.Instruction;
import parser.cfg.types.BasicBlock;

public class CFGReducer {
    public static boolean reduceCFG(ControlFlowGraph cfg) {
        HashMap<BasicBlock, BasicBlock> branch2ifbbMap = new HashMap<>();
        boolean changed = true;
        // first detect all of the conditional BBs not part of loops and add to List 
        while (changed) {
            changed = false;
            for (BasicBlock bb : cfg.bbListPostorder) {
                if (bb.successors.size() == 2) {
                    if (bb.matchType(BasicBlock.TYPE_CONDITIONAL_BRANCH)) {
                        changed = reduceConditional(bb, cfg, branch2ifbbMap);
                        if (changed)
                            break;
                    } else {
                        System.out.println("non conditional branch with 2 successors");
                        System.exit(1);
                    }
                } else if (bb.predecessors.size() == 1) {
                    boolean check = false;
                    for (BasicBlock basicBlock : bb.predecessors)
                        if (basicBlock.successors.size() != 1)
                            check = true;

                    if (check)
                        continue;

                    changed = reduceConsecutive(bb, cfg);
                    if (changed)
                            break;
                }
            }
        }

        if (cfg.bbListPostorder.size() != 1) {
            cfg.stringify();
            System.out.println(cfg.bbListPostorder.size());
            for (BasicBlock t : cfg.bbListPostorder) {
                System.out.println(t.id);
            }
            System.out.println("failed to reduce CFG");
            return false;
        }

        return true;
    }

    // Here is the general overview of how to implement if decompilation

    // IMPORTANT: check the times when flipping a statemetn

    // 1. You need to set all of the if statments for now this 
    // is any if statement that isn't part of a loop

    // 2. Iterate through all the basic blocks and reduce it in the predefined order

    // 3. reduce on the following patterns: IFIF, IFELSE, ELSE, IFELSECHAIN, REORDERIF
    // make sure IFELSE, ELSE, IFELSECHAIN, REORDERIF are called in the correct nesting

    // inspired by vineflower
    private static boolean reduceIf(BasicBlock bb, ControlFlowGraph cfg, HashSet<BasicBlock> reorderedBBs) {
        if (!bb.matchType(BasicBlock.TYPE_CONDITIONAL_BRANCH + BasicBlock.TYPE_IF + BasicBlock.TYPE_IF_ELSE)) {
            return false;
        }

        boolean reduced = false;
        if (reduceIfIf(bb)) {
            reduced = true;
        } else if (!reorderedBBs.contains(bb)) {
            if (reduceIfElse(bb)) {
                reduced = true;
            } else if (reduceElse(bb)) {
                reduced = true;
            } else if (reduceIfElseIfElse(bb)) {
                reduced = true;
            }
        } else if (reorderIf(bb)) {
            reduced = true;
        }

        return reduced;
    }

    private static boolean reduceIfIf(BasicBlock bb) {
        if (bb.branch.branch!=null) {
            if (bb.next.equals(bb.branch.next)) {
                if (bb.branch.branch.matchType(BasicBlock.TYPE_GOTO)) {
                    // remove bb.brach edges and place them on bb edges
                } else {
                    // fill in
                }
            }

            // set as subnode and later cobine to &&
            return true;
        }
        return false;
    }

    private static boolean reduceIfElse(BasicBlock bb) {
        return false;
    }

    private static boolean reduceElse(BasicBlock bb) {
        return false;
    }

    private static boolean reduceIfElseIfElse(BasicBlock bb) {
        return false;
    }

    private static boolean reorderIf(BasicBlock bb) {
        return false;
    }

    // depreciated
    private static boolean reduceConditional(BasicBlock bb, ControlFlowGraph cfg, HashMap<BasicBlock, BasicBlock> branch2ifbbMap) {
        if ((bb.branch.matchType(BasicBlock.TYPE_RETURN) || bb.branch.matchType(BasicBlock.TYPE_END)) &&!bb.next.matchType(BasicBlock.TYPE_GOTO)
        || bb.branch.predecessors.size() > 1) {
            bb.instructions.getLast().flip();
            BasicBlock tmp = bb.branch;
            bb.branch = bb.next;
            bb.next = tmp;
        }

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
                succ.predecessors.remove(bb.next);
            }
            // if any previous if statements are contained within an if statement
            // there branch must be switched to this new ifbb
            if (branch2ifbbMap.get(bb.branch) != null) {
                ifBB.subNodes.add(bb); ifBB.subNodes.add(branch2ifbbMap.get(bb.branch)); ifBB.subNodes.add(bb.next);
                cfg.bbListPostorder.remove(branch2ifbbMap.get(bb.branch));
            } else {
                ifBB.subNodes.add(bb); ifBB.subNodes.add(bb.branch); ifBB.subNodes.add(bb.next);
            }

            bb.predecessors.clear();
            bb.next.successors.clear();
            int index = cfg.bbListPostorder.indexOf(bb);
            cfg.bbListPostorder.set(index, ifBB);
            cfg.bbListPostorder.removeAll(ifBB.subNodes);
            ifBB.branch = bb.next.branch;
            ifBB.next = bb.next.next;
            bb.branch.instructions.addFirst(new Instruction(0, "if", 0, 0));
            bb.next.instructions.addFirst(new Instruction(0, "if_end", 0, 0));
            branch2ifbbMap.put(bb, ifBB);
        } else {
            // if-else
            if (bb.branch.successors.size() > 1) {
                cfg.stringify();
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
                succ.predecessors.remove(bb.next);
            }

            for (BasicBlock succ : bb.branch.successors) {
                ifeBB.successors.add(succ);
                succ.predecessors.add(ifeBB);
                succ.predecessors.remove(bb.branch);
            }

            bb.predecessors.clear();
            bb.next.successors.clear();
            if (branch2ifbbMap.get(bb.branch) != null) {
                ifeBB.subNodes.add(bb); ifeBB.subNodes.add(branch2ifbbMap.get(bb.branch)); ifeBB.subNodes.add(bb.next);
                cfg.bbListPostorder.remove(branch2ifbbMap.get(bb.branch));
            } else {
                ifeBB.subNodes.add(bb); ifeBB.subNodes.add(bb.branch); ifeBB.subNodes.add(bb.next);
            }
            bb.branch.instructions.addFirst(new Instruction(0, "if", 0, 0));
            bb.branch.instructions.addLast(new Instruction(0, "if_end", 0, 0));
            bb.next.instructions.addFirst(new Instruction(0, "else", 0, 0));
            bb.next.instructions.addLast(new Instruction(0, "else_end", 0, 0));
            bb.predecessors.clear();
            bb.next.successors.clear();
            bb.branch.successors.clear();
            int index = cfg.bbListPostorder.indexOf(bb);
            cfg.bbListPostorder.set(index, ifeBB);
            cfg.bbListPostorder.removeAll(ifeBB.subNodes);
            Collections.swap(cfg.bbList, bb.next.id, bb.branch.id);
            branch2ifbbMap.put(bb, ifeBB);
        }

        return true;
    }

    private static boolean reduceConsecutive(BasicBlock bb, ControlFlowGraph cfg) {
        if (bb.predecessors.size() == 1) {
            for (BasicBlock basicBlock : bb.predecessors) {
                if (basicBlock.successors.size() != 1 && !basicBlock.successors.contains(bb)) {
                    System.out.println("could not reduce consecutive");
                    System.exit(1);
                }
            }

            List<BasicBlock> subs = enumerateConsecutives(bb);
            if (subs.size() <= 1) {
                return false;
            }

            BasicBlock statsBB = cfg.newTypeBB(BasicBlock.TYPE_STATEMENTS);
            if (bb.matchType(BasicBlock.TYPE_GOTO)) {
                statsBB.TYPE += BasicBlock.TYPE_GOTO;
            }

            statsBB.subNodes.addAll(subs);
            BasicBlock last = statsBB.subNodes.getLast();
            int index = cfg.bbListPostorder.indexOf(last);
            cfg.bbListPostorder.set(index, statsBB);
            cfg.bbListPostorder.removeAll(statsBB.subNodes);
            statsBB.successors.addAll(bb.successors);
            statsBB.predecessors.addAll(last.predecessors);
            for (BasicBlock basicBlock : bb.successors) {
                basicBlock.predecessors.remove(bb);
                basicBlock.predecessors.add(statsBB);
            }
            
            bb.successors.clear();
            for (BasicBlock basicBlock : last.predecessors) {
                basicBlock.successors.remove(last);
                basicBlock.successors.add(statsBB);
            }

            last.predecessors.clear();
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
            if (tmp.predecessors.size() != 1)
                break;
            for (BasicBlock basicBlock : tmp.predecessors) {
                if (basicBlock.successors.size() == 1 && basicBlock.successors.contains(tmp)) {
                    tmp = basicBlock;
                    check = true;
                }
            }
        }

        return consecBlocks;
    }
}
